package org.example;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EquationCalculatorTest {

    @org.junit.jupiter.api.Test
    void computeProbabilityOfHit() {
        List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();
        ballisticEntries.add(new Pair<>(1.0f, 1.0f)); // A hit within the target
        ballisticEntries.add(new Pair<>(-2.0f, -2.0f)); // A miss outside the target

        double targetRadius = 2.0;
        double probabilityOfHit = 0;
        try {
            probabilityOfHit = EquationCalculator.computeProbabilityOfHit(ballisticEntries, targetRadius);

        } catch (Exception ignored) {

        }
        double expectedProbability = 50;

        assertEquals(expectedProbability, probabilityOfHit, 0.001); // 0.001 is the delta for floating-point comparisons
    }

    @org.junit.jupiter.api.Test
    void computeVerticalDispersion() {
        // Create a list of ballistic entries for testing
        List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();
        ballisticEntries.add(new Pair<>(1.0f, 1.0f));
        ballisticEntries.add(new Pair<>(1.0f, 5.0f));
        ballisticEntries.add(new Pair<>(1.0f, -3.0f));
        double verticalDispersion = 0;

        try {
            verticalDispersion = EquationCalculator.computeVerticalDispersion(ballisticEntries);

        } catch (Exception ignored) {

        }
        // Define the expected result based on the provided ballistic entries
        // In this case, the mean Y is (1 + 5 - 3) / 3 = 1.0
        // The vertical distances are (1 - 1)^2, (5 - 1)^2, and (-3 - 1)^2
        // The mean squared vertical distance is (0 + 16 + 16) / 3 = 10.6667
        // The vertical dispersion is the square root of the mean squared vertical distance
        double expectedVerticalDispersion = Math.sqrt(10.6667);

        // Assert that the actual result matches the expected result
        assertEquals(expectedVerticalDispersion, verticalDispersion, 0.001); // 0.001 is the delta for floating-point comparisons
    }

    @org.junit.jupiter.api.Test
    void computeHorizontalDispersion() {
        // Create a list of ballistic entries for testing
        List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();
        ballisticEntries.add(new Pair<>(1.0f, 1.0f));
        ballisticEntries.add(new Pair<>(5.0f, 1.0f));
        ballisticEntries.add(new Pair<>(-3.0f, 1.0f));

        double horizontalDispersion = 0;

        try {
            horizontalDispersion = EquationCalculator.computeHorizontalDispersion(ballisticEntries);
        } catch (Exception ignored) {

        }

        // Define the expected result based on the provided ballistic entries
        // In this case, the mean X is (1 + 5 - 3) / 3 = 1.0
        // The horizontal distances are (1 - 1)^2, (5 - 1)^2, and (-3 - 1)^2
        // The mean squared horizontal distance is (0 + 16 + 16) / 3 = 10.6667
        // The horizontal dispersion is the square root of the mean squared horizontal distance
        double expectedHorizontalDispersion = Math.sqrt(10.6667);

        // Assert that the actual result matches the expected result
        assertEquals(expectedHorizontalDispersion, horizontalDispersion, 0.001); // 0.001 is the delta for floating-point comparisons
    }

    @org.junit.jupiter.api.Test
    void computeCircularErrorProbable() {
        // Test case 1: Standard scenario
        List<Pair<Float, Float>> case1 = List.of(new Pair<>(1.0f, 1.0f), new Pair<>(5.0f, 5.0f), new Pair<>(-3.0f, -3.0f));
        assertDoesNotThrow(() -> {
            double result = EquationCalculator.computeCircularErrorProbable(case1);
            // Expected result based on your logic
            double expectedResult = 0.845 * 3.0; // Adjust based on your actual expected result
            assertEquals(expectedResult, result, 0.001); // Adjust the delta as needed
        });
    }

    @org.junit.jupiter.api.Test
    void computeRadialStandardDeviation() {
        // Create a list of ballistic entries for testing
        List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();
        ballisticEntries.add(new Pair<>(1.0f, 1.0f));
        ballisticEntries.add(new Pair<>(5.0f, 5.0f));
        ballisticEntries.add(new Pair<>(-3.0f, -3.0f));

        // Call the function and catch any exceptions (ignoring them for the test)
        assertDoesNotThrow(() -> {
            double result = EquationCalculator.computeRadialStandardDeviation(ballisticEntries);
            double meanX = (1 + 5 - 3) / 3.0;
            double meanY = (1 + 5 - 3) / 3.0;
            double sumSquaredDistances = 0.0;
            for (Pair<Float, Float> point : ballisticEntries) {
                sumSquaredDistances += Math.pow(Math.sqrt(Math.pow(point.getValue0() - meanX, 2) + Math.pow(point.getValue1() - meanY, 2)), 2);
            }
            double meanSquaredDistance = sumSquaredDistances / ballisticEntries.size();
            double expectedResult = Math.sqrt(meanSquaredDistance);
            assertEquals(expectedResult, result, 0.001); // Adjust the delta as needed
        });
    }

    @org.junit.jupiter.api.Test
    void computeExtremeSpread() {
        // Create a list of ballistic entries for testing
        List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();
        ballisticEntries.add(new Pair<>(1.0f, 1.0f));
        ballisticEntries.add(new Pair<>(5.0f, 5.0f));
        ballisticEntries.add(new Pair<>(-3.0f, -3.0f));

        // Call the function and catch any exceptions (ignoring them for the test)
        assertDoesNotThrow(() -> {
            double result = EquationCalculator.computeExtremeSpread(ballisticEntries);
            double expectedResult = Math.sqrt(Math.pow(5 - (-3), 2) + Math.pow(5 - (-3), 2));
            assertEquals(expectedResult, result, 0.001); // Adjust the delta as needed
        });
    }

    @org.junit.jupiter.api.Test
    void computeAverageDeviation() {
        // Create a list of ballistic entries for testing
        List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();
        ballisticEntries.add(new Pair<>(1.0f, 1.0f));
        ballisticEntries.add(new Pair<>(5.0f, 5.0f));
        ballisticEntries.add(new Pair<>(-3.0f, -3.0f));

        // Call the function and catch any exceptions (ignoring them for the test)
        assertDoesNotThrow(() -> {
            double result = EquationCalculator.computeAverageDeviation(ballisticEntries);
            // Add an expected value and assertion here
            double meanX = (1 + 5 - 3) / 3.0;
            double meanY = (1 + 5 - 3) / 3.0;
            double sumDistances = 0.0;
            for (Pair<Float, Float> point : ballisticEntries) {
                sumDistances += Math.sqrt(Math.pow(point.getValue0() - meanX, 2) + Math.pow(point.getValue1() - meanY, 2));
            }
            double expectedResult = EquationCalculator.round(sumDistances / ballisticEntries.size(), 3);
            assertEquals(expectedResult, result, 0.001); // Adjust the delta as needed
        });
    }
}