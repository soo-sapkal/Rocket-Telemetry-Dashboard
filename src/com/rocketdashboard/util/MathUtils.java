package com.rocketdashboard.util;

public class MathUtils {

    public static double calculateApogee(double[] altitudes) {
        if (altitudes == null || altitudes.length == 0) {
            return 0.0;
        }

        double max = Double.MIN_VALUE;
        for (double alt : altitudes) {
            if (alt > max) max = alt;
        }
        return max;
    }

    public static double calculateMaxVelocity(double[] velocities) {
        if (velocities == null || velocities.length == 0) {
            return 0.0;
        }

        double max = 0;
        for (double vel : velocities) {
            if (Math.abs(vel) > Math.abs(max)) max = vel;
        }
        return max;
    }

    public static double calculateMaxAcceleration(double[] accelerations) {
        if (accelerations == null || accelerations.length == 0) {
            return 0.0;
        }

        double max = 0;
        for (double acc : accelerations) {
            if (Math.abs(acc) > Math.abs(max)) max = acc;
        }
        return max;
    }

    // Parabolic interpolation for smooth animation
    public static double parabolicInterpolation(double x, double x1, double y1, double x2, double y2, double x3, double y3) {
        double denom = (x1 - x2) * (x1 - x3) * (x2 - x3);
        if (Math.abs(denom) < 1e-10) {
            return y2; // Avoid division by zero
        }

        double a = (x3 * (y2 - y1) + x2 * (y1 - y3) + x1 * (y3 - y2)) / denom;
        double b = (x3*x3 * (y1 - y2) + x2*x2 * (y3 - y1) + x1*x1 * (y2 - y3)) / denom;
        double c = (x2 * x3 * (x2 - x3) * y1 + x3 * x1 * (x3 - x1) * y2 + x1 * x2 * (x1 - x2) * y3) / denom;

        return a*x*x + b*x + c;
    }

    // Calculate flight duration in seconds
    public static double calculateFlightDuration(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        java.time.Duration duration = java.time.Duration.between(start, end);
        return duration.toMillis() / 1000.0;
    }
}