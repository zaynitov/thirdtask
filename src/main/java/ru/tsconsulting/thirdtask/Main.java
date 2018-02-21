package ru.tsconsulting.thirdtask;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static ExecutorService service;
    private static AtomicInteger countOfFiles = new AtomicInteger(0);
    private static AtomicInteger countOfFolders = new AtomicInteger(0);
    private static AtomicInteger countOfLines = new AtomicInteger(0);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        if (args.length != 3) {
            System.out.println("Input data is incorrect");
            System.exit(1);
        }
        File dir = new File(args[0]);
        int numberThreads = 1;
        try {
            numberThreads = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("It's should be a Number!");
            System.exit(1);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]))) {
            service = Executors.newFixedThreadPool(numberThreads);

            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                    System.out.println("We already read " + countOfFolders + " folders and " + countOfFiles +
                            " files. " + (System.currentTimeMillis() - startTime) + " milliseconds has passed." +
                                         " We found "+countOfLines+" suitable files");
                }
            });
            t.setDaemon(true);
            t.start();

            recours(dir, bw);
            service.shutdown();
            try {
                if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                    service.shutdownNow();
                }
            } catch (InterruptedException ex) {
                service.shutdownNow();
                Thread.currentThread().interrupt();
            }

        } catch (IOException e) {
            System.out.println("We have some problems with output file");
        }
        System.out.print("Time of the program is " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public static void recours(File dir, BufferedWriter bw) {
        if (dir.listFiles() != null) {
            for (File item : dir.listFiles()) {
                if (item != null && !item.isHidden() && item.exists() && item.isDirectory()) {
                    countOfFolders.incrementAndGet();
                    recours(item, bw);
                } else {
                    countOfFiles.incrementAndGet();
                    if (item != null && item.canRead() && !item.isHidden() && item.exists() && item.getName().endsWith(".log")) {
                        service.submit(() -> {
                            try (BufferedReader reder = new BufferedReader(new InputStreamReader
                                    (new FileInputStream(item.getPath()), "UTF-8"))) {
                                String line;
                                while ((line = reder.readLine()) != null) {
                                    if (line.contains("ERROR")) {
                                        synchronized (bw) {
                                            countOfLines.incrementAndGet();
                                            bw.write(line);
                                            bw.write("\n");
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                //System.out.println("We have some problems with input file" + e.getMessage());
                            }

                        });
                    }
                }
            }
        }
    }


}
