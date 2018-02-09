package ru.tsconsulting.thirdtask;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Queue;

public class PrintInFile {
    public static void printInFile(Queue<String> queueToPut, String fileName) {


        try (FileOutputStream f = new FileOutputStream((fileName))) {
            System.setOut(new PrintStream(f, true, "UTF-8"));
            for (String putIn : queueToPut) {
                System.out.print(putIn);
                System.out.println("\n");
            }
        } catch (IOException e) {
            System.out.println("We have some problems with output file");
        }
    }

}
