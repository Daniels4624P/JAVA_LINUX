# Java File Organizer

This program organizes files in a specified directory by moving them into subdirectories based on the first four characters of their filenames. It also generates a PDF report of the operations performed.

## Features

- Reads files from `/home/gerente-ti/salidas`.
- Creates subdirectories within `/home/gerente-ti/salidas` named after the first four characters of each file.
- Moves files into their respective subdirectories.
- Generates a PDF report named `report.pdf` in `/home/gerente-ti/salidas` detailing the actions taken.

## Prerequisites

- Java Development Kit (JDK) 8 or higher.
- Apache Maven.

## Dependencies

- Apache PDFBox (for PDF report generation). This is managed by Maven.

## Building the Application

1.  Navigate to the project's root directory (where `pom.xml` is located).
2.  Compile the project and create an executable JAR with dependencies:
    ```bash
    mvn compile assembly:single
    ```
    This will generate a JAR file in the `target/` directory (e.g., `file-organizer-1.0-SNAPSHOT-jar-with-dependencies.jar`).

## Running the Application

1.  Ensure the input directory `/home/gerente-ti/salidas` exists and contains files you want to organize.
2.  Open a terminal or command prompt.
3.  **Navigate to the project's root directory.** This is the directory that contains the `pom.xml` file and the `target` folder.

4.  **From the project's root directory**, run the application using the following command (replace the JAR filename if it's different):
    ```bash
    java -jar target/file-organizer-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```
    *Alternatively*, if you have already navigated into the `target` directory (e.g., by doing `cd target`), you can run the JAR directly like this:
    ```bash
    java -jar file-organizer-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```

5.  The program will process the files and generate a `report.pdf` in `/home/gerente-ti/salidas`. A confirmation message will be printed to the console.

## Important Notes

- The input directory `/home/gerente-ti/salidas` is currently hardcoded in the application.
- The PDF report `report.pdf` is saved in the `/home/gerente-ti/salidas` directory.
- Ensure the application has the necessary read/write permissions for the target directory and its subdirectories.
