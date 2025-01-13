package com.example;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

public class EncryptorTest {

    private static final String KEY = "mySecretKey12345";
    private static final String INPUT_FILE = "testInput.txt";
    private static final String ENCRYPTED_FILE = "testEncrypted.enc";
    private static final String DECRYPTED_FILE = "testDecrypted.txt";

    @Before
    public void setUp() throws IOException {
        // Создание тестового файла для шифрования
        try (FileWriter writer = new FileWriter(INPUT_FILE)) {
            writer.write("This is a test file for encryption.");
        }
    }

    @Test
    public void testEncryptFile() throws Exception {
        // Шифруем файл
        Encryptor.encryptFile(KEY, INPUT_FILE, ENCRYPTED_FILE);

        // Проверяем, что зашифрованный файл существует
        File encryptedFile = new File(ENCRYPTED_FILE);
        assertTrue("Encrypted file should exist", encryptedFile.exists());

        // Проверяем, что зашифрованный файл отличается от исходного
        try (FileInputStream fis = new FileInputStream(INPUT_FILE);
             FileInputStream fos = new FileInputStream(ENCRYPTED_FILE)) {

            byte[] inputBytes = fis.readAllBytes();
            byte[] encryptedBytes = fos.readAllBytes();

            // Заменили assertNotEquals на проверку неравенства через assertTrue
            assertFalse("Encrypted file should be different from the original",
                    new String(inputBytes, StandardCharsets.UTF_8).equals(new String(encryptedBytes, StandardCharsets.UTF_8)));
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testEncryptFileExceptionHandling() throws Exception {
        // Проверка на случай, если входной файл не существует
        Encryptor.encryptFile(KEY, "nonexistentFile.txt", ENCRYPTED_FILE);
    }


    @Test
    public void testFileEncryptionContent() throws Exception {
        // Шифруем файл
        Encryptor.encryptFile(KEY, INPUT_FILE, ENCRYPTED_FILE);

        // Читаем зашифрованный файл и проверяем его содержимое
        try (FileInputStream fis = new FileInputStream(ENCRYPTED_FILE)) {
            byte[] encryptedBytes = fis.readAllBytes();
            assertNotNull("Encrypted file content should not be null", encryptedBytes);
            assertTrue("Encrypted file content should not be empty", encryptedBytes.length > 0);
        }
    }
}
