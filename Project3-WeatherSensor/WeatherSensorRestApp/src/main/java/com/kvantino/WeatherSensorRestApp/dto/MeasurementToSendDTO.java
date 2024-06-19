package com.kvantino.WeatherSensorRestApp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MeasurementToSendDTO implements Serializable {
    @NotEmpty
    private String sensorName;

    @NotNull
    @Min(value = -100, message = "Degree value should be greater then -100")
    @Max(value = 100, message = "Degree value should be less then 100")
    private Double value;

    @NotNull
    private Boolean raining;
}
