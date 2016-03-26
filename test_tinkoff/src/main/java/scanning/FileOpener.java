package main.java.scanning;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileOpener {

    static public Scanner open(String path) {
        try {

            File file = new File(path);
            if (!file.isFile() || !file.canRead())
                throw new IOException("could not read " + path);
            return new Scanner(file);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
