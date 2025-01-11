package com.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.Arrays;

public class Decrypt {
    public static void decryptFile(String key, String inputFile, String outputFile) throws Exception {
        // Декодирование ключа из URL-safe Base64
        byte[] keyBytes = Base64.getUrlDecoder().decode(key);
        keyBytes = Arrays.copyOf(keyBytes, 16); // Обрезка до 16 байт (128 бит)
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // Создание шифра с нулевым IV (если IV не используется, можно оставить массив с нулями)
        byte[] iv = new byte[16]; // Если IV не требуется, оставляем как есть
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            // Чтение зашифрованного текста и декодирование из Base64
            byte[] inputBytes = fis.readAllBytes();
            String encodedInput = new String(inputBytes);
            byte[] encryptedBytes = Base64.getDecoder().decode(encodedInput);

            // Расшифровка данных
            byte[] outputBytes = cipher.doFinal(encryptedBytes);
            fos.write(outputBytes);

            System.out.println("File successfully decrypted to: " + outputFile);
        }
    }
}
