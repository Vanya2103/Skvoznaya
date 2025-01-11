package com.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("File Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel fileLabel = new JLabel("Select a file:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(fileLabel, gbc);

        JTextField filePathField = new JTextField(30);
        filePathField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(filePathField, gbc);

        JButton fileButton = new JButton("Choose File");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(fileButton, gbc);

        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        JLabel encryptedLabel = new JLabel("Is the file encrypted?");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(encryptedLabel, gbc);

        JRadioButton yesButton = new JRadioButton("Yes");
        JRadioButton noButton = new JRadioButton("No");
        ButtonGroup encryptionGroup = new ButtonGroup();
        encryptionGroup.add(yesButton);
        encryptionGroup.add(noButton);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(yesButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(noButton, gbc);

        JTextField keyField = new JTextField(20);
        keyField.setVisible(false);
        JLabel keyLabel = new JLabel("Enter decryption key:");
        keyLabel.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(keyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(keyField, gbc);

        yesButton.addActionListener(e -> {
            keyField.setVisible(true);
            keyLabel.setVisible(true);
        });
        noButton.addActionListener(e -> {
            keyField.setVisible(false);
            keyLabel.setVisible(false);
        });

        JLabel outputLabel = new JLabel("Enter output file name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(outputLabel, gbc);

        JTextField outputFileField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panel.add(outputFileField, gbc);

        JLabel fileTypeLabel = new JLabel("Select output file type:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(fileTypeLabel, gbc);

        String[] fileTypes = {"txt", "xml", "json"};
        JComboBox<String> fileTypeComboBox = new JComboBox<>(fileTypes);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        panel.add(fileTypeComboBox, gbc);

        JCheckBox archiveBox = new JCheckBox("Archive the result");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(archiveBox, gbc);

        JLabel archiveTypeLabel = new JLabel("Select archive type:");
        archiveTypeLabel.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(archiveTypeLabel, gbc);

        String[] archiveTypes = {"zip", "jar"};
        JComboBox<String> archiveTypeComboBox = new JComboBox<>(archiveTypes);
        archiveTypeComboBox.setVisible(false);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        panel.add(archiveTypeComboBox, gbc);

        archiveBox.addActionListener(e -> {
            boolean isSelected = archiveBox.isSelected();
            archiveTypeLabel.setVisible(isSelected);
            archiveTypeComboBox.setVisible(isSelected);
        });

        JCheckBox encryptBox = new JCheckBox("Encrypt the result");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(encryptBox, gbc);

        JButton processButton = new JButton("Process");
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(processButton, gbc);

        processButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            boolean isEncrypted = yesButton.isSelected();
            String decryptionKey = keyField.getText();
            String outputFileName = outputFileField.getText();
            String selectedFileType = (String) fileTypeComboBox.getSelectedItem();
            boolean archiveResult = archiveBox.isSelected();
            String selectedArchiveType = (String) archiveTypeComboBox.getSelectedItem();
            boolean encryptResult = encryptBox.isSelected();

            if (filePath.isEmpty() || outputFileName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please complete all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Call your processing logic here
                List<Information> results = new ArrayList<>();
                if (isEncrypted) {
                    String decryptedFileName = "decrypted_input.txt";
                    Decrypt.decryptFile(decryptionKey, filePath, decryptedFileName);
                    filePath = decryptedFileName;
                }

                if (filePath.endsWith(".zip") || filePath.endsWith(".jar")) {
                    List<String> filesInArchive = listFilesInArchive(filePath);
                    String selectedFile = (String) JOptionPane.showInputDialog(frame, "Select a file from the archive:",
                            "File Selection", JOptionPane.PLAIN_MESSAGE, null,
                            filesInArchive.toArray(), filesInArchive.get(0));

                    if (selectedFile == null) {
                        JOptionPane.showMessageDialog(frame, "No file selected from the archive.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String extractedFilePath = extractFileFromArchive(filePath, selectedFile);
                    filePath = extractedFilePath;
                }

                Main.processFile(filePath, results);

                // Save the results
                switch (selectedFileType) {
                    case "txt":
                        FileWrite.writeToTxt(outputFileName + ".txt", results);
                        break;
                    case "xml":
                        FileWrite.writeToXml(outputFileName + ".xml", results);
                        break;
                    case "json":
                        FileWrite.writeToJson(outputFileName + ".json", results);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported file type: " + selectedFileType);
                }

                if (archiveResult) {
                    Archival.archiveSingleFile(outputFileName + "." + selectedFileType, outputFileName, selectedArchiveType);
                }
                if (encryptResult) {
                    Encryptor.encryptFile(decryptionKey, outputFileName + "." + selectedFileType, outputFileName + "_encrypted." + selectedFileType);
                }

                JOptionPane.showMessageDialog(frame, "Processing completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private static List<String> listFilesInArchive(String archivePath) throws IOException {
        List<String> fileNames = new ArrayList<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(archivePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                fileNames.add(entry.getName());
            }
        }
        return fileNames;
    }

    private static String extractFileFromArchive(String archivePath, String fileName) throws IOException {
        Path tempDir = Files.createTempDirectory("archive_extraction");
        String extractedFilePath = null;
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(archivePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals(fileName)) {
                    Path extractedFile = tempDir.resolve(fileName);
                    Files.copy(zipInputStream, extractedFile, StandardCopyOption.REPLACE_EXISTING);
                    extractedFilePath = extractedFile.toString();
                    break;
                }
            }
        }
        if (extractedFilePath == null) {
            throw new IOException("File not found in archive: " + fileName);
        }
        return extractedFilePath;
    }
}
