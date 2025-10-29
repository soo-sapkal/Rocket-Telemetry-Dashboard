package com.rocketdashboard.service;

import com.rocketdashboard.model.TelemetryData;
import com.rocketdashboard.model.Stage;
import java.util.*;

public class StageClassifier {
    private static final double VELOCITY_CHANGE_THRESHOLD = -20.0; // Sudden velocity decrease for parachute
    private static final double LANDING_VELOCITY_THRESHOLD = 1.0; // Velocity close to zero for landing
    private static final double LANDING_ALTITUDE_THRESHOLD = 5.0; // Close to ground for landing

    public Map<Stage, List<TelemetryData>> classifyStages(List<TelemetryData> telemetryData) {
        Map<Stage, List<TelemetryData>> stages = new LinkedHashMap<>();

        if (telemetryData == null || telemetryData.isEmpty()) {
            return stages;
        }

        // Initialize all stage lists
        for (Stage stage : Stage.values()) {
            stages.put(stage, new ArrayList<>());
        }

        // Find key events (points)
        TelemetryData launchPoint = findLaunchPoint(telemetryData);
        TelemetryData apogeePoint = findApogeePoint(telemetryData);
        TelemetryData parachutePoint = findParachutePoint(telemetryData, apogeePoint);
        TelemetryData landingPoint = findLandingPoint(telemetryData);

        // Add point events
        if (launchPoint != null) stages.get(Stage.LAUNCH).add(launchPoint);
        if (apogeePoint != null) stages.get(Stage.APOGEE).add(apogeePoint);
        if (parachutePoint != null) stages.get(Stage.PARACHUTE_DEPLOYED).add(parachutePoint);
        if (landingPoint != null) stages.get(Stage.LANDING).add(landingPoint);

        // Classify range stages
        classifyRangeStages(telemetryData, stages, launchPoint, apogeePoint, landingPoint);

        // Remove empty stages
        stages.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return stages;
    }

    private TelemetryData findLaunchPoint(List<TelemetryData> telemetryData) {
        // Launch is the first point where velocity becomes positive
        for (TelemetryData data : telemetryData) {
            if (data.getVelocity() > 0.1) { // Small threshold to avoid noise
                return data;
            }
        }
        return telemetryData.get(0); // Fallback to first point
    }

    private TelemetryData findApogeePoint(List<TelemetryData> telemetryData) {
        // Apogee is the point where next altitude is less than current
        for (int i = 0; i < telemetryData.size() - 1; i++) {
            TelemetryData current = telemetryData.get(i);
            TelemetryData next = telemetryData.get(i + 1);

            if (next.getAltitude() < current.getAltitude()) {
                return current; // Current point is the peak
            }
        }
        return telemetryData.get(telemetryData.size() - 1); // Fallback to last point
    }

    private TelemetryData findParachutePoint(List<TelemetryData> telemetryData, TelemetryData apogeePoint) {
        if (apogeePoint == null) return null;

        int apogeeIndex = telemetryData.indexOf(apogeePoint);

        // Look for sudden velocity decrease after apogee (parachute deployment)
        for (int i = apogeeIndex + 1; i < telemetryData.size() - 1; i++) {
            TelemetryData current = telemetryData.get(i);
            TelemetryData next = telemetryData.get(i + 1);

            double velocityChange = next.getVelocity() - current.getVelocity();

            // Sudden negative velocity change indicates parachute deployment
            if (velocityChange < VELOCITY_CHANGE_THRESHOLD) {
                return next; // Return the point where velocity suddenly drops
            }
        }

        return null; // No parachute deployment detected
    }

    private TelemetryData findLandingPoint(List<TelemetryData> telemetryData) {
        // Landing is when velocity becomes near zero close to ground
        for (int i = telemetryData.size() - 1; i >= 0; i--) {
            TelemetryData data = telemetryData.get(i);

            if (Math.abs(data.getVelocity()) < LANDING_VELOCITY_THRESHOLD &&
                    data.getAltitude() <= LANDING_ALTITUDE_THRESHOLD) {
                return data;
            }
        }

        // If no perfect landing found, return the last point
        return telemetryData.get(telemetryData.size() - 1);
    }

    private void classifyRangeStages(List<TelemetryData> telemetryData,
                                     Map<Stage, List<TelemetryData>> stages,
                                     TelemetryData launchPoint,
                                     TelemetryData apogeePoint,
                                     TelemetryData landingPoint) {

        if (launchPoint == null || apogeePoint == null || landingPoint == null) {
            return;
        }

        int launchIndex = telemetryData.indexOf(launchPoint);
        int apogeeIndex = telemetryData.indexOf(apogeePoint);
        int landingIndex = telemetryData.indexOf(landingPoint);

        // Ascent: from launch to apogee (excluding apogee point itself)
        for (int i = launchIndex; i < apogeeIndex; i++) {
            stages.get(Stage.ASCENT).add(telemetryData.get(i));
        }

        // Descent: from apogee to landing (excluding apogee and landing points)
        for (int i = apogeeIndex + 1; i < landingIndex; i++) {
            stages.get(Stage.DESCENT).add(telemetryData.get(i));
        }

        // Include apogee point in both ascent and descent for continuity
        stages.get(Stage.ASCENT).add(apogeePoint);
        stages.get(Stage.DESCENT).add(apogeePoint);
    }

    public List<String> detectAnomalies(List<TelemetryData> telemetryData) {
        List<String> anomalies = new ArrayList<>();

        if (telemetryData == null || telemetryData.isEmpty()) {
            return anomalies;
        }

        Map<Stage, List<TelemetryData>> stages = classifyStages(telemetryData);

        // Check for premature parachute deployment (before apogee)
        if (stages.containsKey(Stage.PARACHUTE_DEPLOYED) && stages.containsKey(Stage.APOGEE)) {
            TelemetryData parachutePoint = stages.get(Stage.PARACHUTE_DEPLOYED).get(0);
            TelemetryData apogeePoint = stages.get(Stage.APOGEE).get(0);

            int parachuteIndex = telemetryData.indexOf(parachutePoint);
            int apogeeIndex = telemetryData.indexOf(apogeePoint);

            if (parachuteIndex < apogeeIndex) {
                anomalies.add("Premature parachute deployment detected! Parachute deployed before apogee.");
            }
        }

        // Check for no parachute deployment
        if (!stages.containsKey(Stage.PARACHUTE_DEPLOYED)) {
            anomalies.add("No parachute deployment detected during descent.");
        }

        // Check for hard landing (high velocity at landing)
        if (stages.containsKey(Stage.LANDING)) {
            TelemetryData landingPoint = stages.get(Stage.LANDING).get(0);
            if (Math.abs(landingPoint.getVelocity()) > 5.0) {
                anomalies.add("Hard landing detected! High velocity (" +
                        String.format("%.1f", landingPoint.getVelocity()) + " m/s) at landing.");
            }
        }

        return anomalies;
    }
}