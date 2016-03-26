package main.java;

import main.java.scanning.FileOpener;
import main.java.scanning.FileScanner;
import java.util.ArrayList;


class Main {

    public static void main(String[] args) {

        if (args.length != 0) {
            ArrayList<String> result =
                    FileScanner.scan(FileOpener.open(args[0]));
            if (result != null) {
                result.forEach(System.out::println);
            }
        }

    }

}