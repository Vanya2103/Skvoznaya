//package com.example;
//
//import java.io.*;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//import java.util.jar.JarOutputStream;
//
//public class Archival {
//
//    // Метод для архивации в ZIP или JAR
//    public static void archive(String archiveName, String format) throws IOException {
//        String archivePath = archiveName + "." + format;
//        if (format.equalsIgnoreCase("zip") || format.equalsIgnoreCase("jar")) {
//            zipOrJarFiles(archivePath, format);
//        } else {
//            throw new IllegalArgumentException("Unsupported archive format: " + format);
//        }
//    }
//
//    // Архивация файлов в ZIP или JAR
//    private static void zipOrJarFiles(String archivePath, String format) throws IOException {
//        File sourceDir = new File("output_results"); // Директория с файлами для архивации
//        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
//            throw new IOException("Source directory does not exist or is not a directory.");
//        }
//
//        try (FileOutputStream fos = new FileOutputStream(archivePath);
//             ZipOutputStream zipOut = format.equalsIgnoreCase("jar")
//                     ? new JarOutputStream(fos)
//                     : new ZipOutputStream(fos)) {
//            zipFiles(sourceDir, sourceDir.getName(), zipOut);
//        }
//    }
//
//    // Рекурсивное добавление файлов в архив
//    private static void zipFiles(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
//        if (fileToZip.isHidden()) {
//            return;
//        }
//
//        if (fileToZip.isDirectory()) {
//            File[] children = fileToZip.listFiles();
//            if (children == null) return;
//
//            if (fileName.endsWith("/")) {
//                zipOut.putNextEntry(new ZipEntry(fileName));
//                zipOut.closeEntry();
//            } else {
//                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
//                zipOut.closeEntry();
//            }
//
//            for (File childFile : children) {
//                zipFiles(childFile, fileName + "/" + childFile.getName(), zipOut);
//            }
//            return;
//        }
//
//        try (FileInputStream fis = new FileInputStream(fileToZip)) {
//            ZipEntry zipEntry = new ZipEntry(fileName);
//            zipOut.putNextEntry(zipEntry);
//
//            byte[] buffer = new byte[4096];
//            int length;
//            while ((length = fis.read(buffer)) >= 0) {
//                zipOut.write(buffer, 0, length);
//            }
//        }
//    }
//}
package com.example;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.jar.JarOutputStream;

public class Archival {

    // Метод для архивации одного файла в ZIP или JAR
    public static void archiveSingleFile(String filePath, String archiveName, String format) throws IOException {
        String archivePath = archiveName + "." + format;

        if (!format.equalsIgnoreCase("zip") && !format.equalsIgnoreCase("jar")) {
            throw new IllegalArgumentException("Unsupported archive format: " + format);
        }

        File fileToArchive = new File(filePath);
        if (!fileToArchive.exists() || !fileToArchive.isFile()) {
            throw new IOException("File to archive does not exist or is not a valid file: " + filePath);
        }

        try (FileOutputStream fos = new FileOutputStream(archivePath);
             ZipOutputStream zipOut = format.equalsIgnoreCase("jar")
                     ? new JarOutputStream(fos)
                     : new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(fileToArchive)) {

            ZipEntry zipEntry = new ZipEntry(fileToArchive.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, length);
            }

            zipOut.closeEntry();
        }

        System.out.println("File archived as: " + archivePath);
    }
}
