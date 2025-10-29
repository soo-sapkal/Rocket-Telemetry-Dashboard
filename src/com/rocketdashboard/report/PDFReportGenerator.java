package com.rocketdashboard.report;

import com.rocketdashboard.model.TelemetryData;
import com.rocketdashboard.model.Stage;
import com.rocketdashboard.model.WeatherData;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PDFReportGenerator {

    public void generateReport(List<TelemetryData> telemetryData,
                               Map<Stage, List<TelemetryData>> stages,
                               List<WeatherData> weatherData,
                               List<String> anomalies,
                               String outputPath) throws IOException {

        StringBuilder report = new StringBuilder();
        report.append("ROCKET FLIGHT ANALYSIS REPORT\n");
        report.append("=============================\n\n");

        // Flight statistics
        if (telemetryData != null && !telemetryData.isEmpty()) {
            double maxAltitude = 0;
            double maxVelocity = 0;

            for (TelemetryData data : telemetryData) {
                if (data.getAltitude() > maxAltitude) maxAltitude = data.getAltitude();
                if (Math.abs(data.getVelocity()) > Math.abs(maxVelocity)) maxVelocity = data.getVelocity();
            }

            report.append("FLIGHT STATISTICS:\n");
            report.append(String.format("Maximum Altitude: %.2f meters\n", maxAltitude));
            report.append(String.format("Maximum Velocity: %.2f m/s\n", maxVelocity));
            report.append(String.format("Flight Duration: %d data points\n\n", telemetryData.size()));
        }

        // Stage information
        if (stages != null && !stages.isEmpty()) {
            report.append("FLIGHT STAGES:\n");
            for (Map.Entry<Stage, List<TelemetryData>> entry : stages.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    report.append(String.format("%s: %d data points\n",
                            entry.getKey().getDisplayName(),
                            entry.getValue().size()));
                }
            }
            report.append("\n");
        }

        // Anomalies
        if (anomalies != null && !anomalies.isEmpty()) {
            report.append("DETECTED ANOMALIES:\n");
            for (String anomaly : anomalies) {
                report.append("• ").append(anomaly).append("\n");
            }
        } else {
            report.append("ANOMALIES: None detected\n");
        }

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(report.toString().getBytes());
        }
    }
}