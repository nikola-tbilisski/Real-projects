package com.kvantino.WeatherSensorRestApp.util;

import com.kvantino.WeatherSensorRestApp.dto.MeasurementDTO;
import com.kvantino.WeatherSensorRestApp.models.WeatherSensor;
import com.kvantino.WeatherSensorRestApp.services.WeatherSensorValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class MeasurementValidator implements Validator {

    private final WeatherSensorValidatorService weatherSensorValidatorService;

    @Override
    public boolean supports(Class<?> clazz) {
        return WeatherSensor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MeasurementDTO measurementDTO = (MeasurementDTO) target;

        if (weatherSensorValidatorService.loadWeatherSensorByName(measurementDTO.getWeatherSensor().getName()).isEmpty()) {
            errors.rejectValue("weatherSensor", "", "Sorry the Sensor name is not registered in DB");
        }
    }
}
