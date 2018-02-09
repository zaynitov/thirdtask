package ru.tsconsulting.thirdtask;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;

public class ReadingFiles {
    public static void readAndGet(String fileName, Queue<String> result) {
        try (BufferedReader reder = new BufferedReader(new InputStreamReader
                (new FileInputStream(fileName), "UTF-8"))) {
            String line;
            while ((line = reder.readLine()) != null) {
                if (line.contains("ERROR")) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("I'm Sorry, i didn't find a file");
        }
    }
}