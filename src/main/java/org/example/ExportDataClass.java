package org.example;

import org.javatuples.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportDataClass {

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
