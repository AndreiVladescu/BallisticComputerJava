package org.example;

import org.javatuples.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for exporting data to different file formats, such as CSV and TXT.
 */
public class ExportDataClass {

    /**
     * Exports the provided list of pairs to a CSV file.
     *
     * @param data     A list of pairs representing the (x, y) coordinates of the data.
     * @param filePath The file path where the CSV file will be created.
     */
    public static void exportToCSV(List<Pair<Float, Float>> data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Pair<Float, Float> entry : data) {
                writer.write(entry.getValue0() + "," + entry.getValue1() + System.lineSeparator());
            }
            System.out.println("Data exported to CSV successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Exports the provided list of pairs to a TXT file.
     *
     * @param data     A list of pairs representing the (x, y) coordinates of the data.
     * @param filePath The file path where the TXT file will be created.
     */
    public static void exportToTXT(List<Pair<Float, Float>> data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Pair<Float, Float> entry : data) {
                writer.write(entry.getValue0() + " " + entry.getValue1() + System.lineSeparator());
            }
            System.out.println("Data exported to TXT successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
