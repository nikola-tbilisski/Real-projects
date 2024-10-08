package com.kvantino.WeatherSensorRestApp.controllers;

import com.kvantino.WeatherSensorRestApp.dto.MeasurementDTO;
import com.kvantino.WeatherSensorRestApp.dto.MeasurementToSendDTO;
import com.kvantino.WeatherSensorRestApp.models.Measurement;
import com.kvantino.WeatherSensorRestApp.services.MeasurementService;
import com.kvantino.WeatherSensorRestApp.util.EntityNotCreatedException;
import com.kvantino.WeatherSensorRestApp.util.ErrorResponse;
import com.kvantino.WeatherSensorRestApp.util.MeasurementValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/measurements")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;
    private final MeasurementValidator measurementValidator;
    private final ModelMapper modelMapper;

    @GetMapping
    @Cacheable(value = "measurementCache", sync = true)
    public List<MeasurementToSendDTO> getAllMeasurements() {
        return measurementService.findAll().stream()
                .map(this::convertModelToDto)
                .toList();
    }

    @GetMapping("/rainyDaysCount")
    @Cacheable(value = "measurementCache", key = "#raining", sync = true)
    public Integer getRainyOrNotDaysCount(@RequestParam("raining") boolean raining) {
        return measurementService.findRainingOrNot(raining);
    }

    @GetMapping("/grade-less")
    @Cacheable(value = "measurementCache", key = "#degree", sync = true)
    public List<MeasurementToSendDTO> getAllEqualOrLessDegree(@RequestParam("degree") double degree) {
        return measurementService.findAllDaysEqualOrLessOfValue(degree).stream()
                .map(this::convertModelToDto)
                .toList();
    }

    //Get method with direct uri of Sensor name
//    @GetMapping("/{name}")
//    public List<MeasurementToSendDTO> showByName(@PathVariable("name") String name) {
//        return measurementService.findByName(name).stream()
//                .map(this::convertModelToDto)
//                .collect(Collectors.toList());
//    }

    //Get method with sensor parameter: ?name=Sensor name!
    @GetMapping("/sensor")
    @Cacheable(value = "measurementCache", key = "#name", sync = true)
    public List<MeasurementToSendDTO> showByName(@RequestParam("name") String name) {
        return measurementService.findByName(name).stream()
                .map(this::convertModelToDto)
                .toList();
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        measurementValidator.validate(measurementDTO, bindingResult);

        ErrorResponse.reportErrors(bindingResult);

        measurementService.save(convertDtoToModel(measurementDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Measurement convertDtoToModel(MeasurementDTO measurementDTO) {
        //Convert DTO to Measurement class with setters & getters (exclude WeatherSensor)
//        Measurement measurement = new Measurement();
//        measurement.setSensorName(measurementDTO.getWeatherSensor().getName());
//        measurement.setValue(measurementDTO.getValue());
//        measurement.setRaining(measurementDTO.getRaining());
//
//        return measurement;

        //Convert via ModelMapper & exclude WeatherSensor & map sensorName from WeatherSensorDTO
        // Create a new instance of ModelMapper to ensure a clean configuration
        ModelMapper innerModelMapper = new ModelMapper();

        // Create a PropertyMap to customize the mapping
        innerModelMapper.addMappings(new PropertyMap<MeasurementDTO, Measurement>() {
            @Override
            protected void configure() {
                // Map sensorName from measurementDTO weatherSensor manually & skip weatherSensor from mapping
                map().setSensorName(source.getWeatherSensor().getName());
                skip().setWeatherSensor(null);
            }
        });

        return innerModelMapper.map(measurementDTO, Measurement.class);
    }

    private MeasurementToSendDTO convertModelToDto(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementToSendDTO.class);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(EntityNotCreatedException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
