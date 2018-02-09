package ru.tsconsulting.thirdtask;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.Executors;

public class Main {

    static ExecutorService service;
    static ConcurrentLinkedQueue<String> result = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        if (args.length != 3) {
            System.out.println("Input data is incorrect");
            System.exit(1);
        }

        File dir = new File(args[0]);
        int numberThreads = 1;
        try {
            numberThreads = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("It's should be a Number!");
            System.exit(1);
        }
        service = Executors.newFixedThreadPool(numberThreads);
        recours(dir);
        service.shutdown();
        try {
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException ex) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.print("Time of the program is " + (System.currentTimeMillis() - startTime) + " ms");
        PrintInFile.printInFile(result, args[2]);


    }

    public static void recours(File dir) {
        try {
        for (File item : dir.listFiles()) {
                if (!item.isHidden() && item.exists() && item != null && item.isDirectory()) {
                    recours(item);
                } else {
                    if (!item.isHidden() && item.exists() && item != null && item.getName().endsWith(".log")) {
                        service.submit(new Runnable() {
                            public void run() {
                                ReadingFiles.readAndGet(item.getPath(), result);

                            }
                        });
                    }
                }
            }
        } catch (NullPointerException e) {
                System.out.println("Empty folder");
        }

    }
}
