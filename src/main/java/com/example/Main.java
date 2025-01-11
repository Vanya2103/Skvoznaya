package com.example;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Information> results = new ArrayList<>(); // Список для хранения результатов задач

        System.out.println("Enter the file or archive name with extension (e.g., tasks.txt, tasks.json, tasks.zip): ");
        String fileName = scanner.nextLine().trim();

        System.out.println("Is the input file encrypted? (yes/no): ");
        String isEncrypted = scanner.nextLine().trim().toLowerCase();

        String decryptedFileName = "decrypted_input.txt"; // Временный файл для расшифровки
        if (isEncrypted.equals("yes")) {
            System.out.println("Enter the decryption key: ");
            String decryptionKey = scanner.nextLine().trim();
            try {
                Decrypt.decryptFile(decryptionKey, fileName, decryptedFileName);
                fileName = decryptedFileName;
            } catch (Exception e) {
                System.out.println("Error during decryption: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        try {
            if (fileName.endsWith(".zip")) {
                processZipArchive(fileName, results);
            } else if (fileName.endsWith(".jar")) {
                processJarArchive(fileName, results);
            } else {
                processFile(fileName, results);
            }

            // Сохранение результатов в файл
            System.out.println("Enter the name for the output file (without extension): ");
            String outputName = scanner.nextLine().trim();

            System.out.println("Enter the file type for saving results (txt, json, xml): ");
            String fileType = scanner.nextLine().trim().toLowerCase();
            String outputFileName = outputName + "." + fileType;

            switch (fileType) {
                case "txt":
                    FileWrite.writeToTxt(outputFileName, results);
                    break;
                case "json":
                    FileWrite.writeToJson(outputFileName, results);
                    break;
                case "xml":
                    FileWrite.writeToXml(outputFileName, results);
                    break;
                default:
                    System.out.println("Unsupported file type: " + fileType);
                    return;
            }
            System.out.println("Results saved to: " + outputFileName);

            // Архивация
            System.out.println("Do you want to archive the result? (yes/no): ");
            String archiveChoice = scanner.nextLine().trim().toLowerCase();
            if (archiveChoice.equals("yes")) {
                System.out.println("Enter the name of the file to archive (e.g., " + outputFileName + "): ");
                String filePathToArchive = scanner.nextLine().trim();

                System.out.println("Enter archive name (without extension): ");
                String archiveName = scanner.nextLine().trim();

                System.out.println("Enter archive format (zip, jar): ");
                String archiveFormat = scanner.nextLine().trim().toLowerCase();

                try {
                    if (archiveFormat.equals("zip") || archiveFormat.equals("jar")) {
                        Archival.archiveSingleFile(filePathToArchive, archiveName, archiveFormat);
                        System.out.println("Result archived as: " + archiveName + "." + archiveFormat);
                    } else {
                        System.out.println("Unsupported archive format: " + archiveFormat);
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred during archiving: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Result not archived.");
            }

            // Шифрование
            System.out.println("Do you want to encrypt the result file? (yes/no): ");
            String encryptChoice = scanner.nextLine().trim().toLowerCase();
            if (encryptChoice.equals("yes")) {
                System.out.println("Enter the encryption key: ");
                String encryptionKey = scanner.nextLine().trim();
                String encryptedFileName = outputName + "_encrypted." + fileType;

                try {
                    Encryptor.encryptFile(encryptionKey, outputFileName, encryptedFileName);
                    System.out.println("Result successfully encrypted to: " + encryptedFileName);
                } catch (Exception e) {
                    System.out.println("An error occurred during encryption: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Result not encrypted.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void processZipArchive(String fileName, List<Information> results) throws Exception {
        System.out.println("Unzipping ZIP archive...");
        String extractFolder = "extracted_files_zip";
        Dearchivizer.unzip(fileName, extractFolder);
        System.out.println("ZIP archive extracted to folder: " + extractFolder);
        processExtractedFolder(extractFolder, results);
    }

    private static void processJarArchive(String fileName, List<Information> results) throws Exception {
        System.out.println("Unpacking JAR archive...");
        String extractFolder = "extracted_files_jar";
        Dearchivizer.unzip(fileName, extractFolder); // Используем метод для ZIP
        System.out.println("JAR archive extracted to folder: " + extractFolder);
        processExtractedFolder(extractFolder, results);
    }

    private static void processExtractedFolder(String extractFolder, List<Information> results) throws Exception {
        File folder = new File(extractFolder);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No files found in the archive.");
            return;
        }

        System.out.println("Files in archive:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the file to process: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Считываем оставшуюся строку

        if (choice < 1 || choice > files.length) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }

        File selectedFile = files[choice - 1];
        processFile(selectedFile.getAbsolutePath(), results);
    }

    // Обработка конкретного файла
    static void processFile(String fileName, List<Information> results) throws Exception {
        if (fileName.endsWith(".txt")) {
            // Чтение из TXT
            String txtContent = FileReade.readTxt(fileName);
            processTxtTasks(txtContent, results);

        } else if (fileName.endsWith(".json")) {
            // Чтение из JSON
            JsonArray jsonTasks = FileReade.readJson(fileName);
            processJsonTasks(jsonTasks, results);

        } else if (fileName.endsWith(".xml")) {
            // Чтение из XML
            NodeList xmlTasks = FileReade.readXml(fileName);
            processXmlTasks(xmlTasks, results);

        } else {
            System.out.println("Unsupported file type. Please provide a .txt, .json, or .xml file.");
        }
    }


    private static void processTxtTasks(String content, List<Information> results) {
        String[] tasks = content.split("\n\n"); // Разделяем задачи по двум переносам строки
        for (int i = 0; i < tasks.length; i++) {
            String[] lines = tasks[i].split("\n"); // Разделяем строки внутри задачи
            String equation = ""; // Переменная для уравнения
            Map<Character, Double> variables = new HashMap<>();

            for (String line : lines) {
                line = line.trim();
                if (line.matches("Task \\d+:?.*")) {
                    // Это заголовок задачи, пропускаем
                    continue;
                } else if (line.contains("=")) {
                    // Это строка с переменной
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        char varName = parts[0].trim().charAt(0);
                        double value = Double.parseDouble(parts[1].trim());
                        variables.put(varName, value);
                    }
                } else {
                    // Это строка с уравнением
                    equation = line;
                }
            }

            // Обрабатываем задачу, если есть уравнение
            if (!equation.isEmpty()) {
                try {
                    Information task = new Information(i + 1, equation, variables);
                    task.calculateResult();
                    task.printResult();
                    results.add(task); // Добавляем задачу в список результатов
                } catch (Exception e) {
                    System.out.println("Error in Task " + (i + 1) + ": " + e.getMessage());
                }
            } else {
                System.out.println("Task " + (i + 1) + ": No equation found.");
            }
        }
    }

    // Обработка задач из JSON файла
    private static void processJsonTasks(JsonArray jsonTasks, List<Information> results) {
        for (int i = 0; i < jsonTasks.size(); i++) {
            JsonObject taskObj = jsonTasks.getJsonObject(i);
            int taskNumber = taskObj.getInt("task_number");
            String equation = taskObj.getString("equation");

            JsonObject varObj = taskObj.getJsonObject("variable_values");
            Map<Character, Double> variables = new HashMap<>();
            for (String key : varObj.keySet()) {
                variables.put(key.charAt(0), varObj.getJsonNumber(key).doubleValue());
            }

            Information task = new Information(taskNumber, equation, variables);
            task.calculateResult();
            task.printResult();
            results.add(task); // Добавляем задачу в список результатов
        }
    }

    // Обработка задач из XML файла
    private static void processXmlTasks(NodeList xmlTasks, List<Information> results) {
        for (int i = 0; i < xmlTasks.getLength(); i++) {
            Node node = xmlTasks.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element element = (org.w3c.dom.Element) node;

                int taskNumber = Integer.parseInt(element.getElementsByTagName("task_number").item(0).getTextContent());
                String equation = element.getElementsByTagName("equation").item(0).getTextContent();

                NodeList variableNodes = element.getElementsByTagName("variable_values").item(0).getChildNodes();
                Map<Character, Double> variables = new HashMap<>();
                for (int j = 0; j < variableNodes.getLength(); j++) {
                    Node varNode = variableNodes.item(j);
                    if (varNode.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element varElement = (org.w3c.dom.Element) varNode;
                        variables.put(varElement.getTagName().charAt(0), Double.parseDouble(varElement.getTextContent()));
                    }
                }

                Information task = new Information(taskNumber, equation, variables);
                task.calculateResult();
                task.printResult();
                results.add(task); // Добавляем задачу в список результатов
            }
        }
    }
}
