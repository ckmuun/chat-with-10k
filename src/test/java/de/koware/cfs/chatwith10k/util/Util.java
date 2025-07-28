package de.koware.cfs.chatwith10k.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {

    public static void writeToDisk(byte[] data, String filename) throws IOException {
        String homeDir = System.getProperty("user.home");
        // Create the file object
        File file = new File(homeDir, filename);

        // Write the byte array to the file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        System.out.println("File written to: " + file.getAbsolutePath());
    }
}
