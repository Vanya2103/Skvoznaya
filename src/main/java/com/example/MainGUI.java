package com.example;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("File Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150); // Уменьшенный размер окна

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Choose Interface: Console or GUI", SwingConstants.CENTER);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(Box.createVerticalStrut(15)); // Отступ между текстом и кнопками

        JButton consoleButton = new JButton("Console");
        consoleButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        JButton guiButton = new JButton("GUI");
        guiButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        consoleButton.addActionListener(e -> {
            frame.dispose();
            Main.main(new String[0]);
        });

        guiButton.addActionListener(e -> {
            frame.dispose();
            showFileSelectionWindow();
        });

        panel.add(consoleButton);
        panel.add(Box.createVerticalStrut(10)); // Отступ между кнопками
        panel.add(guiButton);

        frame.add(panel);
        frame.setLocationRelativeTo(null); // Центрирование окна на экране
        frame.setVisible(true);
    }

    private static void showFileSelectionWindow() {
        JFrame frame = new JFrame("File Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel fileLabel = new JLabel("Select a file:");
        fileLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        panel.add(fileLabel);

        JTextField filePathField = new JTextField(30);
        filePathField.setEditable(false);
        filePathField.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        JButton fileButton = new JButton("Choose File");
        fileButton.setAlignmentX(JButton.LEFT_ALIGNMENT);

        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        panel.add(fileButton);
        panel.add(filePathField);

        JLabel encryptedLabel = new JLabel("Is the file encrypted?");
        encryptedLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        panel.add(encryptedLabel);

        JRadioButton yesButton = new JRadioButton("Yes");
        yesButton.setAlignmentX(JRadioButton.LEFT_ALIGNMENT);
        JRadioButton noButton = new JRadioButton("No");
        noButton.setAlignmentX(JRadioButton.LEFT_ALIGNMENT);

        ButtonGroup encryptionGroup = new ButtonGroup();
        encryptionGroup.add(yesButton);
        encryptionGroup.add(noButton);

        panel.add(yesButton);
        panel.add(noButton);

        JTextField keyField = new JTextField(20);
        keyField.setVisible(false);
        keyField.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        JLabel keyLabel = new JLabel("Enter decryption key:");
        keyLabel.setVisible(false);
        keyLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        panel.add(keyLabel);
        panel.add(keyField);

        yesButton.addActionListener(e -> {
            keyField.setVisible(true);
            keyLabel.setVisible(true);
        });

        noButton.addActionListener(e -> {
            keyField.setVisible(false);
            keyLabel.setVisible(false);
        });

        JLabel outputLabel = new JLabel("Enter output file name:");
        outputLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        JTextField outputFileField = new JTextField(20);
        outputFileField.setAlignmentX(JTextField.LEFT_ALIGNMENT);

        panel.add(outputLabel);
        panel.add(outputFileField);

        JLabel fileTypeLabel = new JLabel("Select output file type:");
        fileTypeLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        panel.add(fileTypeLabel);

        String[] fileTypes = {"txt", "xml", "json"};
        JComboBox<String> fileTypeComboBox = new JComboBox<>(fileTypes);
        fileTypeComboBox.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
        panel.add(fileTypeComboBox);

        JCheckBox archiveBox = new JCheckBox("Archive the result");
        archiveBox.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
        panel.add(archiveBox);

        JLabel archiveTypeLabel = new JLabel("Select archive type:");
        archiveTypeLabel.setVisible(false);
        archiveTypeLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        panel.add(archiveTypeLabel);

        String[] archiveTypes = {"zip", "jar"};
        JComboBox<String> archiveTypeComboBox = new JComboBox<>(archiveTypes);
        archiveTypeComboBox.setVisible(false);
        archiveTypeComboBox.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
        panel.add(archiveTypeComboBox);

        archiveBox.addActionListener(e -> {
            boolean isSelected = archiveBox.isSelected();
            archiveTypeLabel.setVisible(isSelected);
            archiveTypeComboBox.setVisible(isSelected);
        });

        JCheckBox encryptBox = new JCheckBox("Encrypt the result");
        encryptBox.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
        panel.add(encryptBox);

        JButton processButton = new JButton("Process");
        processButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        panel.add(processButton);

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
                List<Information> results = new ArrayList<>();
                if (isEncrypted) {
                    String decryptedFileName = "decrypted_input.txt";
                    Decrypt.decryptFile(decryptionKey, filePath, decryptedFileName);
                    filePath = decryptedFileName;
                }

                Main.processFile(filePath, results);

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
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
