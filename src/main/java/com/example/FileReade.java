package com.example;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileReade {

    // Метод чтения из TXT
    public static String readTxt(String filePath) throws Exception {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    // Метод чтения из JSON с использованием javax.json
    public static JsonArray readJson(String filePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            JsonReader jsonReader = Json.createReader(br);
            JsonObject jsonObject = jsonReader.readObject();
            return jsonObject.getJsonArray("tasks");
        }
    }

    // Метод чтения из XML
    public static NodeList readXml(String filePath) throws Exception {
        File file = new File(filePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        return document.getElementsByTagName("task");
    }
}
