package com.example.fileorganizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

// PDFBox Imports
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
        // No longer printing log here, it will go to the PDF
        
        String reportOutputPath = Paths.get(TARGET_DIRECTORY, REPORT_FILE_NAME).toString();
        generatePdfReport(movedFilesLog, reportOutputPath);

        // Placeholder for actual alert
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

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("File Organization Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                float yPosition = 720;
                float leading = 14.5f; // Line spacing

                for (String entry : logEntries) {
                    if (yPosition < 50) { // Check if new page is needed
                        contentStream.endText(); // End text on current page before closing stream
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page); // new content stream for new page
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        yPosition = 750; // Reset Y position
                        contentStream.beginText(); // Must begin text again for the new page
                        contentStream.newLineAtOffset(50, yPosition);
                    }
                     if (!contentStream.isInTextMode()) { // Ensure we are in text mode
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, yPosition);
                    }
                    contentStream.showText(entry);
                    contentStream.newLineAtOffset(0, -leading);
                    yPosition -= leading;
                }
                contentStream.endText();
            }
            document.save(outputPath);
            System.out.println("PDF report generated successfully at " + outputPath);
            movedFilesLog.add("PDF report generated successfully at " + outputPath); // Add to log as well
        } catch (IOException e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            movedFilesLog.add("Error generating PDF report: " + e.getMessage());
        }
    }
}
