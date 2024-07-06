package cn.occi.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LineCounter {
    public static void main(String[] args) {
        File projectDir = new File("C:\\Users\\93567\\Documents\\learning\\term1\\big-market"); // 替换为你的项目路径
        int totalLines = countLines(projectDir);
        System.out.println("Total lines: " + totalLines);
    }

    private static int countLines(File file) {
        int lines = 0;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                lines += countLines(child);
            }
        } else if (file.getName().endsWith(".java")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.readLine() != null) {
                    lines++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }
}