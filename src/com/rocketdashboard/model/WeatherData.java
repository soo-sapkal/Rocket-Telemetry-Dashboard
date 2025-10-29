package com.rocketdashboard.model;

import java.time.LocalDateTime;

public class WeatherData {
    private LocalDateTime timestamp;
    private double temperature;
    private double pressure;
    private double windSpeed;
    private double windDirection;
    private double humidity;
    
    public WeatherData(LocalDateTime timestamp, double temperature, double pressure,
                      double windSpeed, double windDirection, double humidity) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidity = humidity;
    }
    
    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getTemperature() { return temperature; }
    public double getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public double getWindDirection() { return windDirection; }
    public double getHumidity() { return humidity; }
    
    @Override
    public String toString() {
        return String.format("Weather: %.1f°C, %.1fkm/h wind", temperature, windSpeed);
    }
}