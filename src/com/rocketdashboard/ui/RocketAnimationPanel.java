package com.rocketdashboard.ui;

import com.rocketdashboard.model.TelemetryData;
import com.rocketdashboard.model.Stage;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class RocketAnimationPanel extends UIComponent {
    private List<TelemetryData> telemetryData;
    private Map<Stage, List<TelemetryData>> stages;
    private int currentIndex = 0;
    private Timer animationTimer;
    private double rocketX, rocketY;
    private Image rocketImage;

    // Store marker positions to avoid overlap
    private Map<Stage, Point> markerPositions = new java.util.HashMap<>();

    public RocketAnimationPanel() {
        setPreferredSize(new Dimension(800, 600));
        loadRocketImage();
    }

    private void loadRocketImage() {
        rocketImage = createRocketIcon();
    }

    private Image createRocketIcon() {
        int width = 30, height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rocket body
        g2d.setColor(Color.RED);
        g2d.fillRect(width/2-5, 0, 10, height-10);

        // Draw rocket nose
        int[] xPoints = {width/2-8, width/2, width/2+8};
        int[] yPoints = {height-10, height, height-10};
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Draw fins
        g2d.setColor(Color.GRAY);
        g2d.fillRect(width/2-10, height-20, 5, 10);
        g2d.fillRect(width/2+5, height-20, 5, 10);

        g2d.dispose();
        return image;
    }

    public void setTelemetryData(List<TelemetryData> telemetryData, Map<Stage, List<TelemetryData>> stages) {
        this.telemetryData = telemetryData;
        this.stages = stages;
        this.currentIndex = 0;
        this.markerPositions.clear(); // Clear previous positions

        if (telemetryData != null && !telemetryData.isEmpty()) {
            updateRocketPosition();
            calculateMarkerPositions(); // Pre-calculate marker positions
        }
        repaint();
    }

    private void calculateMarkerPositions() {
        if (stages == null || telemetryData == null) return;

        markerPositions.clear();
        double maxAltitude = getMaxAltitude();

        for (Map.Entry<Stage, List<TelemetryData>> entry : stages.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                // Use the middle point of each stage for better distribution
                int middleIndex = entry.getValue().size() / 2;
                TelemetryData markerData = entry.getValue().get(middleIndex);
                int dataIndex = telemetryData.indexOf(markerData);

                if (dataIndex >= 0) {
                    double x = getWidth() * 0.1 + (getWidth() * 0.8) * (dataIndex / (double)telemetryData.size());
                    double y = getHeight() - (getHeight() * 0.1) - (getHeight() * 0.8) * (markerData.getAltitude() / maxAltitude);

                    markerPositions.put(entry.getKey(), new Point((int)x, (int)y));
                }
            }
        }
    }

    public void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }

        if (telemetryData != null && !telemetryData.isEmpty()) {
            animationTimer = new Timer(100, e -> {
                if (currentIndex < telemetryData.size() - 1) {
                    currentIndex++;
                    updateRocketPosition();
                    repaint();
                } else {
                    animationTimer.stop();
                }
            });
            animationTimer.start();
        }
    }

    public void pauseAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public void setStage(Stage stage) {
        if (stages != null && stages.containsKey(stage) && telemetryData != null) {
            List<TelemetryData> stageData = stages.get(stage);
            if (!stageData.isEmpty()) {
                // Jump to the beginning of the stage
                currentIndex = telemetryData.indexOf(stageData.get(0));
                updateRocketPosition();
                repaint();
            }
        }
    }

    private void updateRocketPosition() {
        if (telemetryData == null || telemetryData.isEmpty() || currentIndex >= telemetryData.size()) {
            return;
        }

        TelemetryData current = telemetryData.get(currentIndex);
        double maxAltitude = getMaxAltitude();

        // Convert to panel coordinates
        double normalizedAlt = current.getAltitude() / maxAltitude;
        rocketX = getWidth() * 0.1 + (getWidth() * 0.8) * (currentIndex / (double)telemetryData.size());
        rocketY = getHeight() - (getHeight() * 0.1) - (getHeight() * 0.8) * normalizedAlt;
    }

    private double getMaxAltitude() {
        if (telemetryData == null || telemetryData.isEmpty()) {
            return 1.0;
        }
        double max = 0;
        for (TelemetryData data : telemetryData) {
            if (data.getAltitude() > max) {
                max = data.getAltitude();
            }
        }
        return max > 0 ? max : 1.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw trajectory
        drawTrajectory(g2d);

        // Draw stage markers
        drawStageMarkers(g2d);

        // Draw rocket
        if (rocketImage != null && telemetryData != null && !telemetryData.isEmpty()) {
            g2d.drawImage(rocketImage, (int)rocketX - 15, (int)rocketY - 25, null);
        }

        // Draw info
        drawInfo(g2d);
    }

    private void drawTrajectory(Graphics2D g2d) {
        if (telemetryData == null || telemetryData.isEmpty()) return;

        Path2D path = new Path2D.Double();
        double maxAltitude = getMaxAltitude();

        for (int i = 0; i < telemetryData.size(); i++) {
            TelemetryData data = telemetryData.get(i);
            double x = getWidth() * 0.1 + (getWidth() * 0.8) * (i / (double)telemetryData.size());
            double y = getHeight() - (getHeight() * 0.1) - (getHeight() * 0.8) * (data.getAltitude() / maxAltitude);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(path);
    }

    // In the drawStageMarkers method, update the stage order and colors:
    private void drawStageMarkers(Graphics2D g2d) {
        if (markerPositions.isEmpty()) return;

        Color[] stageColors = {
                new Color(0, 255, 0),      // GREEN - Launch
                new Color(255, 255, 0),    // YELLOW - Apogee
                new Color(255, 165, 0),    // ORANGE - Parachute Deployed
                new Color(255, 0, 0),      // RED - Landing
                new Color(0, 0, 255),      // BLUE - Ascent (range)
                new Color(128, 0, 128)     // PURPLE - Descent (range)
        };

        // Draw point events first
        Stage[] pointEvents = {Stage.LAUNCH, Stage.APOGEE, Stage.PARACHUTE_DEPLOYED, Stage.LANDING};

        int colorIndex = 0;
        for (Stage stage : pointEvents) {
            if (markerPositions.containsKey(stage)) {
                Point pos = markerPositions.get(stage);

                // Draw larger marker for point events
                g2d.setColor(stageColors[colorIndex]);
                g2d.fillOval(pos.x - 8, pos.y - 8, 16, 16);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(pos.x - 8, pos.y - 8, 16, 16);

                // Draw label
                g2d.setColor(textColor);
                g2d.drawString(stage.getDisplayName(), pos.x + 12, pos.y - 10);

                colorIndex++;
            }
        }
    }
    private void drawInfo(Graphics2D g2d) {
        if (telemetryData != null && currentIndex < telemetryData.size()) {
            TelemetryData current = telemetryData.get(currentIndex);
            g2d.setColor(textColor);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            g2d.drawString(String.format("Time: %s", current.getTimestamp().toLocalTime()), 10, 20);
            g2d.drawString(String.format("Altitude: %.1f m", current.getAltitude()), 10, 40);
            g2d.drawString(String.format("Velocity: %.1f m/s", current.getVelocity()), 10, 60);
            g2d.drawString(String.format("Acceleration: %.1f m/s²", current.getAcceleration()), 10, 80);
            g2d.drawString(String.format("Progress: %d/%d (%.1f%%)",
                    currentIndex + 1, telemetryData.size(),
                    (currentIndex + 1) * 100.0 / telemetryData.size()), 10, 100);
        }
    }

    @Override
    protected void updateTheme() {
        repaint();
    }
}