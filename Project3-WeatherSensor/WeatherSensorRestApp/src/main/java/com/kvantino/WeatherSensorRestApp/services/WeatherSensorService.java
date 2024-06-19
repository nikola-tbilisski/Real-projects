package com.kvantino.WeatherSensorRestApp.services;

import com.kvantino.WeatherSensorRestApp.models.WeatherSensor;
import com.kvantino.WeatherSensorRestApp.repositories.WeatherSensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeatherSensorService {
    private final WeatherSensorRepository weatherSensorRepository;

    @Transactional
    public void save(WeatherSensor weatherSensor) {
        enrichWeatherSensor(weatherSensor);
        weatherSensorRepository.save(weatherSensor);
    }

    private void enrichWeatherSensor(WeatherSensor weatherSensor) {
        weatherSensor.setCreatedAt(LocalDateTime.now());
        weatherSensor.setCreatedWho("Admin: Zhora :)");
    }
}
