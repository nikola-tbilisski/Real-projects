package com.kvantino.WeatherSensorRestApp.services;

import com.kvantino.WeatherSensorRestApp.models.WeatherSensor;
import com.kvantino.WeatherSensorRestApp.repositories.WeatherSensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeatherSensorValidatorService {

    private final WeatherSensorRepository weatherSensorRepository;

    public Optional<WeatherSensor> loadWeatherSensorByName(String name) {
        return weatherSensorRepository.findByName(name);
    }
}
