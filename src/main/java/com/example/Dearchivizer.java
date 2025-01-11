package com.example;

import java.io.*;
import java.util.zip.ZipInputStream;


public class Dearchivizer {

    // Метод для разархивации ZIP, JAR и RAR
    public static void unzip(String archivePath, String outputDir) throws IOException {
        if (archivePath.endsWith(".zip") || archivePath.endsWith(".jar")) {
            unzipZipOrJar(archivePath, outputDir);
        } else {
            throw new IllegalArgumentException("Unsupported archive format: " + archivePath);
        }
    }

    // Разархивация ZIP или JAR
    private static void unzipZipOrJar(String archivePath, String outputDir) throws IOException {
        File destDir = new File(outputDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(archivePath))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                File file = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int length;
                        while ((length = zipIn.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        }
    }
}
