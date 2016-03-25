package main.java;

import main.java.scanning.FileScanner;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        if (args.length != 0) {
            ArrayList<String> result = FileScanner.scan(args[0]);
            if (result != null) {
                result.forEach(System.out::println);
            }
        }

    }

}