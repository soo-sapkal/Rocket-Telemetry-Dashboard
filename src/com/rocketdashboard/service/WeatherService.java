package com.rocketdashboard.service;

import com.rocketdashboard.model.WeatherData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeatherService {
    private Random random = new Random();

    public List<WeatherData> getHistoricalWeather(String location, LocalDateTime launchTime) {
        List<WeatherData> weatherData = new ArrayList<>();

        // Simulate weather data
        for (int i = 0; i < 24; i++) {
            LocalDateTime time = launchTime.minusHours(24 - i);
            double temperature = 15 + random.nextDouble() * 20;
            double pressure = 1013 + random.nextDouble() * 20 - 10;
            double windSpeed = random.nextDouble() * 30;
            double windDirection = random.nextDouble() * 360;
            double humidity = 30 + random.nextDouble() * 70;

            weatherData.add(new WeatherData(time, temperature, pressure, windSpeed, windDirection, humidity));
        }

        return weatherData;
    }
}