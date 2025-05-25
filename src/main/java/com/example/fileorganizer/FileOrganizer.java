package com.example.fileorganizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class FileOrganizer {

    private static final String TARGET_DIRECTORY = "/home/gerente-ti/salidas";
    private static final String REPORT_FILE_NAME = "report.pdf";
    private static List<String> movedFilesLog = new ArrayList<>();

    public static void main(String[] args) {
        File mainDir = new File(TARGET_DIRECTORY);

        if (!mainDir.exists() || !mainDir.isDirectory()) {
            System.err.println("Error: The directory " + TARGET_DIRECTORY + " does not exist or is not a directory.");
            return;
        }

        File[] files = mainDir.listFiles();

        if (files == null) {
            System.err.println("Error: Could not list files in " + TARGET_DIRECTORY + ". Check permissions.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                processFile(file, mainDir.toPath());
            }
        }

        System.out.println("\n--- File Processing Complete ---");
        
        String reportOutputPath = Paths.get(TARGET_DIRECTORY, REPORT_FILE_NAME).toString();
        generatePdfReport(movedFilesLog, reportOutputPath);

        System.out.println("Alert: File organization process finished. Report generated at " + reportOutputPath);
    }

    private static void processFile(File file, Path baseDir) {
        String fileName = file.getName();
        if (fileName.length() >= 4) {
            String subDirName = fileName.substring(0, 4).toUpperCase();
            Path subDirPath = baseDir.resolve(subDirName);

            try {
                if (!Files.exists(subDirPath)) {
                    Files.createDirectories(subDirPath);
                    String log = "Created directory: " + subDirPath;
                    System.out.println(log);
                    movedFilesLog.add(log);
                }

                Path sourcePath = file.toPath();
                Path destinationPath = subDirPath.resolve(fileName);
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                String logMessage = "Moved file: " + fileName + " to " + subDirName + File.separator + fileName;
                System.out.println(logMessage);
                movedFilesLog.add(logMessage);

            } catch (IOException e) {
                String errorLog = "Error processing file " + fileName + ": " + e.getMessage();
                System.err.println(errorLog);
                movedFilesLog.add(errorLog);
            }
        } else {
            String logMessage = "File name " + fileName + " is less than 4 characters long. Skipping.";
            System.out.println(logMessage);
            movedFilesLog.add(logMessage);
        }
    }

    private static void generatePdfReport(List<String> logEntries, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("File Organization Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                float yPosition = 720;
                final float leading = 14.5f;
                final float pageTopMargin = 750;
                final float pageBottomMargin = 50;
                final float leftMargin = 50;

                // Start text block for the first page's content
                contentStream.beginText();
                contentStream.newLineAtOffset(leftMargin, yPosition);

                for (String entry : logEntries) {
                    if (yPosition < pageBottomMargin) { 
                        contentStream.endText(); // End text on current page
                        contentStream.close();   // Close current stream

                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page); // Create new stream for new page
                        
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        yPosition = pageTopMargin;
                        contentStream.beginText(); // Start new text block on new page
                        contentStream.newLineAtOffset(leftMargin, yPosition);
                    }
                    contentStream.showText(entry);
                    contentStream.newLineAtOffset(0, -leading);
                    yPosition -= leading;
                }
                contentStream.endText(); // End the last text block
            } finally {
                if (contentStream != null) {
                    contentStream.close(); // Ensure the last content stream is closed
                }
            }
            document.save(outputPath);
            System.out.println("PDF report generated successfully at " + outputPath);
            movedFilesLog.add("PDF report generated successfully at " + outputPath);
        } catch (IOException e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            movedFilesLog.add("Error generating PDF report: " + e.getMessage());
        }
    }
}
