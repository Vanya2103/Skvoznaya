package com.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Encryptor {
    public static void encryptFile(String key, String inputFile, String outputFile) throws Exception {
        byte[] keyBytes = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] inputBytes = new byte[(int) new File(inputFile).length()];
            fis.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);

            System.out.println("File successfully encrypted to: " + outputFile);
        }
    }
}
