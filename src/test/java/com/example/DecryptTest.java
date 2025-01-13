package com.example;

import org.junit.Before;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class DecryptTest {
    private static final String KEY = "my-secret-key";
    private static final String INPUT_FILE = "encrypted.txt";
    private static final String OUTPUT_FILE = "decrypted.txt";
    private static final String ORIGINAL_CONTENT = "Hello, World!";

    @Before
    public void setUp() throws Exception {
        // Создаём зашифрованный файл для тестирования
        byte[] keyBytes = Arrays.copyOf(KEY.getBytes(), 16);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // Создаём IV (инициализационный вектор)
        byte[] iv = new byte[16]; // Нулевой вектор

        // Шифруем оригинальное содержимое
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] encryptedBytes = cipher.doFinal(ORIGINAL_CONTENT.getBytes());

        // Сохраняем зашифрованные данные в файл
        try (FileWriter writer = new FileWriter(INPUT_FILE)) {
            String encodedInput = Base64.getEncoder().encodeToString(encryptedBytes);
            writer.write(encodedInput);
        }
    }

    @Test
    public void testDecryptFile() throws Exception {
        Decrypt.decryptFile(Base64.getEncoder().encodeToString(KEY.getBytes()), INPUT_FILE, OUTPUT_FILE);

        // Чтение расшифрованного файла
        String decryptedContent = new String(Files.readAllBytes(new File(OUTPUT_FILE).toPath()));
        assertTrue(decryptedContent.equals(ORIGINAL_CONTENT));
    }

    @Test(expected = Exception.class)
    public void testDecryptFile_InvalidKey() throws Exception {
        // Попробуем расшифровать файл с неверным ключом
        String invalidKey = "invalid-key";
        Decrypt.decryptFile(Base64.getEncoder().encodeToString(invalidKey.getBytes()), INPUT_FILE, OUTPUT_FILE);
    }

    @Test(expected = Exception.class)
    public void testDecryptFile_InvalidFile() throws Exception {
        // Попробуем расшифровать несуществующий файл
        Decrypt.decryptFile(Base64.getEncoder().encodeToString(KEY.getBytes()), "nonexistent.txt", OUTPUT_FILE);
    }


    @Test(expected = Exception.class)
    public void testDecryptFile_InvalidBase64() throws Exception {
        // Создаем файл с некорректными данными (невалидный Base64)
        try (FileWriter writer = new FileWriter(INPUT_FILE)) {
            writer.write("invalid base64 string");
        }

        Decrypt.decryptFile(Base64.getEncoder().encodeToString(KEY.getBytes()), INPUT_FILE, OUTPUT_FILE);
    }

    @Test(expected = Exception.class)
    public void testDecryptFile_ModifiedEncryptedContent() throws Exception {
        // Изменяем зашифрованный файл после его создания
        Decrypt.decryptFile(Base64.getEncoder().encodeToString(KEY.getBytes()), INPUT_FILE, OUTPUT_FILE);

        // Изменяем файл
        Files.write(new File(INPUT_FILE).toPath(), "malicious data".getBytes());

        // Попытаемся расшифровать измененный файл
        Decrypt.decryptFile(Base64.getEncoder().encodeToString(KEY.getBytes()), INPUT_FILE, OUTPUT_FILE);
    }
}
