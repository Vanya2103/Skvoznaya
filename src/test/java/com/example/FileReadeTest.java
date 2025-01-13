package com.example;

import org.junit.Before;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonWriter;
import org.w3c.dom.NodeList;

import java.io.FileWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileReadeTest {

    private static final String TEST_TXT_FILE = "testFile.txt";
    private static final String TEST_JSON_FILE = "testFile.json";
    private static final String TEST_XML_FILE = "testFile.xml";

    @Before
    public void setUp() throws Exception {
        // Создание тестового TXT файла
        try (FileWriter writer = new FileWriter(TEST_TXT_FILE)) {
            writer.write("Hello, World!\nThis is a test.");
        }

        // Создание тестового JSON файла
        try (FileWriter writer = new FileWriter(TEST_JSON_FILE)) {
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("tasks", Json.createArrayBuilder()
                            .add(Json.createObjectBuilder().add("name", "Task 1"))
                            .add(Json.createObjectBuilder().add("name", "Task 2")))
                    .build();

            try (JsonWriter jsonWriter = Json.createWriter(writer)) {
                jsonWriter.write(jsonObject);
            }
        }

        try (FileWriter writer = new FileWriter(TEST_XML_FILE)) {
            writer.write("<tasks><task><name>Task 1</name></task><task><name>Task 2</name></task></tasks>");
        }
    }

    @Test
    public void testReadTxt() throws Exception {
        String content = FileReade.readTxt(TEST_TXT_FILE);
        assertEquals("Hello, World!\nThis is a test.\n", content);
    }

    @Test
    public void testReadJson() throws Exception {
        JsonArray tasks = FileReade.readJson(TEST_JSON_FILE);
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.getJsonObject(0).getString("name"));
        assertEquals("Task 2", tasks.getJsonObject(1).getString("name"));
    }

    @Test
    public void testReadXml() throws Exception {
        NodeList tasks = FileReade.readXml(TEST_XML_FILE);
        assertNotNull(tasks);
        assertEquals(2, tasks.getLength());
    }
}
