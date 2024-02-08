package org.example;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquationCalculator {
    /**
     * Computes the probability of hitting the target, given a target radius.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @param targetRadius     The radius of the target.
     * @return The probability of hitting the target as a percentage.
     * @throws DivisionByZeroException If the total number of shots is zero, resulting in a division by zero.
     */
    public static double computeProbabilityOfHit(List<Pair<Float, Float>> ballisticEntries, double targetRadius) throws DivisionByZeroException {
        int numHits = countHits(ballisticEntries, targetRadius);
        int totalShots = ballisticEntries.size();

        if (totalShots == 0) {
            throw new DivisionByZeroException();
        }

        return round((double) numHits / totalShots * 100, 2);
    }

    /**
     * Counts the number of ballistic entries that hit the target, given a target radius.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @param targetRadius     The radius of the target.
     * @return The number of hits within the specified target radius.
     */
    private static int countHits(List<Pair<Float, Float>> ballisticEntries, double targetRadius) {
        int numHits = 0;
        for (Pair<Float, Float> entry : ballisticEntries) {
            double distanceToTarget = computeDistanceToTarget(entry);
            if (distanceToTarget <= targetRadius) {
                numHits++;
            }
        }
        return numHits;
    }

    /**
     * Computes the Euclidean distance from the origin to a point represented by a pair of coordinates.
     *
     * @param entry The point for which to compute the distance from the origin.
     * @return The Euclidean distance from the origin to the specified point.
     */
    private static double computeDistanceToTarget(Pair<Float, Float> entry) {
        double deltaX = entry.getValue0();
        double deltaY = entry.getValue1();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Computes the Vertical Dispersion, which is a measure of vertical spread of ballistic entries from their mean y-coordinate.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return The Vertical Dispersion calculated based on the provided ballistic entries.
     * @throws NullListException If the provided list of ballistic entries is null or empty.
     */
    public static double computeVerticalDispersion(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        Pair<Double, Double> mean = computeMean(ballisticEntries);
        double sumOfSquaredVerticalDistances = 0;
        for (Pair<Float, Float> entry : ballisticEntries) {
            double deltaY = entry.getValue1() - mean.getValue1();
            sumOfSquaredVerticalDistances += deltaY * deltaY;
        }
        double meanSquaredVerticalDistance = sumOfSquaredVerticalDistances / ballisticEntries.size();
        return round(Math.sqrt(meanSquaredVerticalDistance), 3);
    }

    /**
     * Computes the Horizontal Dispersion, which is a measure of horizontal spread of ballistic entries from their mean x-coordinate.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return The Horizontal Dispersion calculated based on the provided ballistic entries.
     * @throws NullListException If the provided list of ballistic entries is null or empty.
     */
    public static double computeHorizontalDispersion(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        Pair<Double, Double> mean = computeMean(ballisticEntries);
        double sumOfSquaredHorizontalDistances = 0;
        for (Pair<Float, Float> entry : ballisticEntries) {
            double deltaX = entry.getValue0() - mean.getValue0();
            sumOfSquaredHorizontalDistances += deltaX * deltaX;
        }
        double meanSquaredHorizontalDistance = sumOfSquaredHorizontalDistances / ballisticEntries.size();
        return round(Math.sqrt(meanSquaredHorizontalDistance), 3);
    }

    /**
     * Computes the Circular Error Probable (CEP), which is the radius of a circle within which 50% of shots are expected to fall.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return The Circular Error Probable (CEP) calculated based on the provided ballistic entries.
     * @throws NullListException If the provided list of ballistic entries is null.
     */
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

    /**
     * Computes the Radial Standard Deviation, which is the standard deviation calculated radially from the group center.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return The Radial Standard Deviation calculated based on the provided ballistic entries.
     * @throws NullListException If the provided list of ballistic entries is null.
     */
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
        return round(Math.sqrt(meanSquaredDistance), 3);
    }

    /**
     * Computes the Average Deviation, which is the average absolute difference between each shot and the mean.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return The Average Deviation calculated based on the provided ballistic entries.
     * @throws InsufficientValuesException If the provided list of ballistic entries has fewer than two values.
     * @throws NullListException           If the provided list of ballistic entries is null.
     */
    public static double computeAverageDeviation(List<Pair<Float, Float>> ballisticEntries) throws InsufficientValuesException, NullListException {
        if (ballisticEntries.size() < 2) throw new InsufficientValuesException();

        Pair<Double, Double> mean = computeMean(ballisticEntries);

        double sum = ballisticEntries.stream().mapToDouble(entry -> Math.abs(entry.getValue0() - mean.getValue0()) + Math.abs(entry.getValue1() - mean.getValue1())).sum();

        return round(sum / ballisticEntries.size(), 3);
    }

    /**
     * Computes the Extreme Spread, which is the difference between the highest and lowest shot distances in a group.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return The Extreme Spread calculated based on the provided ballistic entries.
     * @throws NullListException If the provided list of ballistic entries is null or empty.
     */
    public static double computeExtremeSpread(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        if (ballisticEntries.isEmpty()) throw new NullListException();

        return round(ballisticEntries.stream().flatMap(i -> ballisticEntries.stream().filter(j -> !i.equals(j)).map(j -> computeDistance(i, j))).max(Double::compare).orElse(0.0), 3);
    }

    /**
     * Computes the parameters of the circumscribed circle, which is the circle that encompasses the given set of points.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return An ArrayList containing the x-coordinate, y-coordinate, and radius of the circumscribed circle.
     * @throws NullListException If the provided list of ballistic entries is null or empty.
     */
    public static ArrayList<Float> getCircumscribedCircle(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        if (ballisticEntries.isEmpty()) throw new NullListException();

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

    /**
     * Rounds the given value to the specified number of decimal places.
     *
     * @param value  The value to round.
     * @param places The number of decimal places to round to.
     * @return The rounded value.
     * @throws IllegalArgumentException If the specified number of decimal places is negative.
     */
    static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     * Computes the Euclidean distance between two points represented by pairs of coordinates.
     *
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The Euclidean distance between the two points.
     */
    private static double computeDistance(Pair<Float, Float> point1, Pair<Float, Float> point2) {
        float deltaX = point1.getValue0() - point2.getValue0();
        float deltaY = point1.getValue1() - point2.getValue1();
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Computes the mean (average) coordinates of a list of points.
     *
     * @param ballisticEntries A list of pairs representing the (x, y) coordinates of ballistic entries.
     * @return A pair containing the mean x-coordinate and mean y-coordinate.
     * @throws NullListException If the provided list of ballistic entries is null or empty.
     */
    private static Pair<Double, Double> computeMean(List<Pair<Float, Float>> ballisticEntries) throws NullListException {
        if (ballisticEntries.isEmpty()) throw new NullListException();

        double meanX = ballisticEntries.stream().mapToDouble(Pair::getValue0).average().orElse(0.0);

        double meanY = ballisticEntries.stream().mapToDouble(Pair::getValue1).average().orElse(0.0);

        return new Pair<>(meanX, meanY);
    }

    /**
     * Computes the Circular Error, which is a measure of dispersion based on radial distances.
     *
     * @param radialDistances An array of radial distances.
     * @return The Circular Error calculated based on the provided radial distances.
     */
    private static double computeCircularError(double[] radialDistances) {
        double sumOfSquaredDistances = 0;
        for (double radialDistance : radialDistances) {
            sumOfSquaredDistances += radialDistance * radialDistance;
        }
        double meanSquaredDistance = sumOfSquaredDistances / radialDistances.length;
        return Math.sqrt(meanSquaredDistance);
    }
}
