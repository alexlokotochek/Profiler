package main.java;

import main.java.scanning.FileOpener;
import main.java.scanning.FileScanner;

public class Main {

    public static void main(String[] args) {
        FileScanner filescanner = new FileScanner();
        filescanner.scan(FileOpener.open(args[0]));
    }
}
