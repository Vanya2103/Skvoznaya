package com.example;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FileWrite {

    // Запись в TXT файл: только номер задачи и результат
    public static void writeToTxt(String fileName, List<Information> tasks) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Information task : tasks) {
                writer.println("Task " + task.getTaskNumber() + ": " + task.getResult());
            }
        }
    }

    // Запись в JSON файл: только номер задачи и результат
    public static void writeToJson(String fileName, List<Information> tasks) throws Exception {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to write to JSON.");
            return;
        }

        // Добавлено больше отладочных сообщений
        System.out.println("Writing " + tasks.size() + " tasks to JSON.");

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Information task : tasks) {
            // Структурируем каждый объект с полями task_number и result
            JsonObjectBuilder taskBuilder = Json.createObjectBuilder()
                    .add("task_number", task.getTaskNumber())
                    .add("result", task.getResult());

            arrayBuilder.add(taskBuilder);
        }

        // Запись в файл с использованием JsonWriter
        try (FileWriter writer = new FileWriter(fileName)) {
            JsonWriter jsonWriter = Json.createWriter(writer);
            jsonWriter.writeArray(arrayBuilder.build());  // Запись массива в файл
            jsonWriter.close();
            System.out.println("Tasks successfully written to " + fileName);
        } catch (Exception e) {
            System.out.println("Error writing JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Запись в XML файл: только номер задачи и результат
    public static void writeToXml(String fileName, List<Information> tasks) throws Exception {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to write to XML.");
            return;
        }

        // Добавлено больше отладочных сообщений
        System.out.println("Writing " + tasks.size() + " tasks to XML.");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("tasks");
        document.appendChild(root);

        for (Information task : tasks) {
            Element taskElement = document.createElement("task");

            Element taskNumber = document.createElement("task_number");
            taskNumber.setTextContent(String.valueOf(task.getTaskNumber()));
            taskElement.appendChild(taskNumber);

            Element result = document.createElement("result");
            result.setTextContent(String.valueOf(task.getResult()));
            taskElement.appendChild(result);

            root.appendChild(taskElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(Files.newBufferedWriter(Path.of(fileName)));
        transformer.transform(source, result);

        System.out.println("Tasks successfully written to " + fileName);
    }
}
