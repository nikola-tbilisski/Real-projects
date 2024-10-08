package com.kvantino.WeatherSensorRestApp.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private long timestamp;

    public static void reportErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError er : errors) {
                errMsg.append(er.getDefaultMessage()).append("; ");
            }

            throw new EntityNotCreatedException(errMsg.toString());
        }
    }
}
