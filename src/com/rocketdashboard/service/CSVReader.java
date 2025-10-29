package com.rocketdashboard.service;

import com.rocketdashboard.model.TelemetryData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public List<TelemetryData> readCSV(String filePath) throws IOException {
        List<TelemetryData> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] values = line.split(",");
                if (values.length >= 7) {
                    LocalDateTime timestamp = LocalDateTime.parse(values[0], formatter);
                    double altitude = Double.parseDouble(values[1]);
                    double velocity = Double.parseDouble(values[2]);
                    double acceleration = Double.parseDouble(values[3]);
                    double latitude = Double.parseDouble(values[4]);
                    double longitude = Double.parseDouble(values[5]);
                    boolean recoveryDeployed = Boolean.parseBoolean(values[6]);
                    boolean motorBurning = values.length > 7 ? Boolean.parseBoolean(values[7]) : false;

                    data.add(new TelemetryData(timestamp, altitude, velocity, acceleration,
                            latitude, longitude, recoveryDeployed, motorBurning));
                }
            }
        }

        return data;
    }
}