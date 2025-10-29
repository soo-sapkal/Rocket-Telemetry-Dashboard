package com.rocketdashboard.main;

import com.rocketdashboard.model.*;
import com.rocketdashboard.service.*;
import com.rocketdashboard.ui.*;
import com.rocketdashboard.report.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RocketDashboard extends JFrame {
    private RocketAnimationPanel animationPanel;
    private TelemetryTablePanel tablePanel;
    private ControlsPanel controlsPanel;
    private List<TelemetryData> telemetryData;
    private Map<Stage, List<TelemetryData>> stages;
    private List<WeatherData> weatherData;
    private List<String> anomalies;

    private CSVReader csvReader;
    private StageClassifier stageClassifier;
    private WeatherService weatherService;
    private PDFReportGenerator reportGenerator;

    public RocketDashboard() {
        initializeServices();
        initializeUI();
        setupEventHandlers();
    }

    private void initializeServices() {
        csvReader = new CSVReader();
        stageClassifier = new StageClassifier();
        weatherService = new WeatherService();
        reportGenerator = new PDFReportGenerator();
    }

    private void initializeUI() {
        setTitle("Rocket Flight Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create UI components
        animationPanel = new RocketAnimationPanel();
        tablePanel = new TelemetryTablePanel();
        controlsPanel = new ControlsPanel();

        // Add to layout
        add(animationPanel, BorderLayout.CENTER);
        add(new JScrollPane(tablePanel), BorderLayout.EAST);
        add(controlsPanel, BorderLayout.SOUTH);

        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private void setupEventHandlers() {
        // Set up control panel actions
        controlsPanel.setPlayAction(e -> {
            if (animationPanel != null) {
                animationPanel.startAnimation();
            }
        });

        controlsPanel.setPauseAction(e -> {
            if (animationPanel != null) {
                animationPanel.pauseAnimation();
            }
        });

        controlsPanel.setResetAction(e -> {
            if (animationPanel != null && telemetryData != null) {
                animationPanel.setTelemetryData(telemetryData, stages);
            }
        });

        controlsPanel.setStageChangeAction(e -> {
            if (controlsPanel != null && animationPanel != null) {
                Stage selectedStage = controlsPanel.getSelectedStage();
                animationPanel.setStage(selectedStage);
            }
        });

        controlsPanel.setThemeChangeAction(e -> {
            if (controlsPanel != null) {
                boolean darkMode = controlsPanel.isDarkMode();
                if (animationPanel != null) animationPanel.setTheme(darkMode);
                if (tablePanel != null) tablePanel.setTheme(darkMode);
                controlsPanel.setTheme(darkMode);
            }
        });

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load CSV");
        JMenuItem reportItem = new JMenuItem("Generate Report");
        JMenuItem exitItem = new JMenuItem("Exit");

        loadItem.addActionListener(e -> loadCSVFile());
        reportItem.addActionListener(e -> generateReport());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(loadItem);
        fileMenu.add(reportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void loadCSVFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Rocket Telemetry CSV File");

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Step 1: Read CSV
                telemetryData = csvReader.readCSV(selectedFile.getAbsolutePath());

                // Step 2: Display as table
                tablePanel.setTelemetryData(telemetryData);

                // Step 3: Classify stages and detect anomalies
                stages = stageClassifier.classifyStages(telemetryData);
                anomalies = stageClassifier.detectAnomalies(telemetryData);

                // Step 4: Set animation data
                animationPanel.setTelemetryData(telemetryData, stages);

                // Step 6: Fetch weather data (simulated)
                LocalDateTime launchTime = telemetryData.get(0).getTimestamp();
                weatherData = weatherService.getHistoricalWeather("Launch Site", launchTime);

                // Show anomalies if any
                if (!anomalies.isEmpty()) {
                    StringBuilder anomalyMessage = new StringBuilder("Detected Anomalies:\n");
                    for (String anomaly : anomalies) {
                        anomalyMessage.append("• ").append(anomaly).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, anomalyMessage.toString(),
                            "Anomaly Alert", JOptionPane.WARNING_MESSAGE);
                }

                JOptionPane.showMessageDialog(this,
                        "CSV loaded successfully!\n" +
                                "Data points: " + telemetryData.size() + "\n" +
                                "Stages detected: " + stages.size());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading CSV: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void generateReport() {
        if (telemetryData == null) {
            JOptionPane.showMessageDialog(this,
                    "Please load CSV data first!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Flight Report");
        fileChooser.setSelectedFile(new File("rocket_flight_report.txt"));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                reportGenerator.generateReport(telemetryData, stages, weatherData,
                        anomalies, fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Report generated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error generating report: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Simple and clean - no look and feel customization
        SwingUtilities.invokeLater(() -> {
            new RocketDashboard().setVisible(true);
        });
    }
}