package org.example;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquationCalculator {
    public static double computeCircularErrorProbable(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        Pair<Double, Double> mean = computeMean(ballisticEntries);

        double[] radialDistances = new double[ballisticEntries.size()];
        for (int i = 0; i < ballisticEntries.size(); i++) {
            Pair<Float, Float> entry = ballisticEntries.get(i);
            double deltaX = entry.getValue0() - mean.getValue0();
            double deltaY = entry.getValue1() - mean.getValue1();
            radialDistances[i] = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }

        double circularError = computeCircularError(radialDistances);

        return round(0.845 * circularError, 3); // 0.845 is a constant factor for the standard normal distribution
    }

    public static double computeRadialStandardDeviation(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        Pair<Double, Double> mean = computeMean(ballisticEntries);

        double sumOfSquaredDistances = 0;
        for (Pair<Float, Float> entry : ballisticEntries) {
            double deltaX = entry.getValue0() - mean.getValue0();
            double deltaY = entry.getValue1() - mean.getValue1();
            double radialDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            sumOfSquaredDistances += radialDistance * radialDistance;
        }

        double meanSquaredDistance = sumOfSquaredDistances / ballisticEntries.size();
        System.out.println(Math.sqrt(meanSquaredDistance));
        return round(Math.sqrt(meanSquaredDistance), 3);
    }

    public static double computeAverageDeviation(List<Pair<Float, Float>> ballisticEntries) throws InsufficientValuesException, NullListException {
        if (ballisticEntries.size() < 2)
            throw new InsufficientValuesException();

        Pair<Double, Double> mean = computeMean(ballisticEntries);

        double sum = ballisticEntries.stream()
                .mapToDouble(entry -> Math.abs(entry.getValue0() - mean.getValue0()) +
                        Math.abs(entry.getValue1() - mean.getValue1()))
                .sum();

        return round(sum / ballisticEntries.size(), 3);
    }

    public static double computeExtremeSpread(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        if (ballisticEntries.isEmpty())
            throw new NullListException();

        return round(ballisticEntries.stream()
                .flatMap(i -> ballisticEntries.stream()
                        .filter(j -> !i.equals(j))
                        .map(j -> computeDistance(i, j)))
                .max(Double::compare)
                .orElse(0.0), 3);
    }

    public static ArrayList<Float> getCircumscribedCircle(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        if (ballisticEntries.isEmpty())
            throw new NullListException();

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
        x = (ballisticEntries.get(point1).getValue0() + ballisticEntries.get(point2).getValue0()) / 2;
        y = (ballisticEntries.get(point1).getValue1() + ballisticEntries.get(point2).getValue1()) / 2;
        radius = (float) (maxDist / 2);
        return new ArrayList<>(Arrays.asList(x, y, radius));
    }


    /* Helper Functions */
    private static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private static double computeDistance(Pair<Float, Float> point1, Pair<Float, Float> point2) {
        float deltaX = point1.getValue0() - point2.getValue0();
        float deltaY = point1.getValue1() - point2.getValue1();
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private static Pair<Double, Double> computeMean(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        if (ballisticEntries.isEmpty())
            throw new NullListException();

        double meanX = ballisticEntries.stream()
                .mapToDouble(Pair::getValue0)
                .average()
                .orElse(0.0);

        double meanY = ballisticEntries.stream()
                .mapToDouble(Pair::getValue1)
                .average()
                .orElse(0.0);

        return new Pair<>(meanX, meanY);
    }

    private static double computeCircularError(double[] radialDistances) {
        double sumOfSquaredDistances = 0;
        for (double radialDistance : radialDistances) {
            sumOfSquaredDistances += radialDistance * radialDistance;
        }
        double meanSquaredDistance = sumOfSquaredDistances / radialDistances.length;
        return Math.sqrt(meanSquaredDistance);
    }
}
