package com.rocketdashboard.model;

import java.time.LocalDateTime;

public class TelemetryData {
    private LocalDateTime timestamp;
    private double altitude;
    private double velocity;
    private double acceleration;
    private double latitude;
    private double longitude;
    private boolean recoveryDeployed;
    private boolean motorBurning;
    
    public TelemetryData(LocalDateTime timestamp, double altitude, double velocity, 
                        double acceleration, double latitude, double longitude,
                        boolean recoveryDeployed, boolean motorBurning) {
        this.timestamp = timestamp;
        this.altitude = altitude;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recoveryDeployed = recoveryDeployed;
        this.motorBurning = motorBurning;
    }
    
    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAltitude() { return altitude; }
    public double getVelocity() { return velocity; }
    public double getAcceleration() { return acceleration; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isRecoveryDeployed() { return recoveryDeployed; }
    public boolean isMotorBurning() { return motorBurning; }
    
    @Override
    public String toString() {
        return String.format("Time: %s, Alt: %.2f, Vel: %.2f", timestamp, altitude, velocity);
    }
}