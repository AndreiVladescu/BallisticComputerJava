package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.javatuples.Pair; // Import the necessary class from the javatuples library

public class EquationCalculator {
    /*TODO*/
    public static double computeCircularErrorProbable(List<Pair<Float, Float>> ballisticEntries)
    {
        return 0;
    }
    /*TODO*/
    public static double computeRadialStandardDerivation(List<Pair<Float, Float>> ballisticEntries)
    {
        return 0;
    }
    /*TODO*/
    public static double computeStandardDerivation(List<Pair<Float, Float>> ballisticEntries)
    {
        if (ballisticEntries.size() < 2)
            return 0.0;
        double standardDerivation = 0;

        float sumX = 0;
        float sumY = 0;
        for (Pair<Float, Float> point : ballisticEntries) {
            sumX += point.getValue0();
            sumY += point.getValue1();
        }

        float centroidX = sumX / ballisticEntries.size();
        float centroidY = sumY / ballisticEntries.size();

        // Calculate the sum of squared differences
        double sumOfSquaredDifferences = 0.0;

        for (Pair<Float, Float> point : ballisticEntries) {
            float distance = (float) calculateDistance(point, Pair.with(centroidX, centroidY));
            float difference = (float) (distance - computeExtremeSpread(ballisticEntries));
            sumOfSquaredDifferences += difference * difference;
        }

        // Calculate the variance and return the square root as standard deviation
        standardDerivation = sumOfSquaredDifferences / (ballisticEntries.size() - 1);

        return round(standardDerivation,4);
    }
    /* Stream Calculations */
    public static double computeExtremeSpread(List<Pair<Float, Float>> ballisticEntries) {
        if (ballisticEntries.isEmpty())
            return 0.0;

        return round(ballisticEntries.stream()
                .flatMap(i -> ballisticEntries.stream()
                        .filter(j -> !i.equals(j))
                        .map(j -> calculateDistance(i, j)))
                .max(Double::compare)
                .orElse(0.0),3) ;
    }public static ArrayList<Float> getCircumscribedCircle(List<Pair<Float, Float>> ballisticEntries) {
        if (ballisticEntries.isEmpty())
            return new ArrayList<>();

        double maxDist = 0;
        int point1 = 0, point2 = 0;
        for (int i = 0; i < ballisticEntries.size() - 1; i++) {
            for (int j = i + 1; j < ballisticEntries.size(); j++) {
                double delta1 = (ballisticEntries.get(i).getValue0() - ballisticEntries.get(j).getValue0());
                double delta2 = (ballisticEntries.get(i).getValue1() - ballisticEntries.get(j).getValue1());
                double distance = Math.sqrt(delta1 * delta1 + delta2 * delta2);
                if (maxDist < distance) {
                    maxDist = distance;
                    point1 = i;
                    point2 = j;
                }
            }
        }

        float x, y, radius;
        //x = (ballisticEntries.get(point1).getValue0() + ballisticEntries.get(point2).getValue0()) / 2;
        x = Math.min(ballisticEntries.get(point2).getValue0(), ballisticEntries.get(point1).getValue0());
        //y = (ballisticEntries.get(point1).getValue1() + ballisticEntries.get(point2).getValue1()) / 2;
        y = Math.min(ballisticEntries.get(point2).getValue1(), ballisticEntries.get(point1).getValue1());
        radius = (float) (maxDist / 2);
        return new ArrayList<>(Arrays.asList(x, y, radius));
    }


    /* Helper Functions */
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    private static double calculateDistance(Pair<Float, Float> point1, Pair<Float, Float> point2) {
        float deltaX = point1.getValue0() - point2.getValue0();
        float deltaY = point1.getValue1() - point2.getValue1();
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
