package com.kvantino.WeatherSensorRestApp.util;

import com.kvantino.WeatherSensorRestApp.dto.WeatherSensorDTO;
import com.kvantino.WeatherSensorRestApp.models.WeatherSensor;
import com.kvantino.WeatherSensorRestApp.services.WeatherSensorValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class WeatherSensorValidator implements Validator {

    private final WeatherSensorValidatorService weatherSensorValidatorService;

    @Override
    public boolean supports(Class<?> clazz) {
        return WeatherSensor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        WeatherSensorDTO weatherSensorDTO = (WeatherSensorDTO) target;

        if (weatherSensorValidatorService.loadWeatherSensorByName(weatherSensorDTO.getName()).isPresent()) {
            errors.rejectValue("name", "", "Sorry Sensor by this name is already registered");
        }
    }
}
