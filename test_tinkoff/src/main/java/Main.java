package main.java;

import main.java.scanning.FileScanner;
import main.java.scanning.LinearFileScanner;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // 2 варианта: один за O(nlogn) - надёжнее,
        // другой за O(n), но использует
        // то, что id вызовов идут по порядку
        // и могут выйти за пределы AtomicLong

        if (args.length != 0) {
//            ArrayList<String> result = FileScanner.scan(args[0]);
            ArrayList<String> result = LinearFileScanner.scan(args[0]);
            if (result != null) {
                result.forEach(System.out::println);
            }
        }
    }
}