package de.koware.cfs.chatwith10k.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestUtil {




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


    public static float cosineDistance(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must be of same length");
        }

        float dotProduct = 0f;
        float normA = 0f;
        float normB = 0f;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            throw new IllegalArgumentException("Vectors must not be zero");
        }

        return 1 - (dotProduct / ((float) (Math.sqrt(normA) * Math.sqrt(normB))));
    }

}
