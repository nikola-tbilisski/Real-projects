package com.kvantino.WeatherSensorRestApp.services;

import com.kvantino.WeatherSensorRestApp.models.Measurement;
import com.kvantino.WeatherSensorRestApp.repositories.MeasurementRepository;
import com.kvantino.WeatherSensorRestApp.repositories.WeatherSensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    private final WeatherSensorRepository weatherSensorRepository;

    public List<Measurement> findAll() {
        return measurementRepository.findAll();
    }

    public Integer findRainingOrNot(boolean value) {
        return measurementRepository.countRainingTrueOrFalse(value);
    }

    public List<Measurement> findAllDaysEqualOrLessOfValue(double value) {
        return measurementRepository.findDaysDegreeLessThenOrEqualToValue(value);
    }

    public List<Measurement> findByName(String name) {
        log.info("Fetching measurement by Sensor name :{} ", name);
        return measurementRepository.findBySensorName(name);
    }


    @Transactional
    @Caching(evict = @CacheEvict(cacheNames = "measurementCache", key = "#measurement.sensorName"),
            put = @CachePut(cacheNames = "measurementCache", key = "#measurement.sensorName")
    )
    public void save(Measurement measurement) {
        enrichMeasurement(measurement);
        log.info("Saving measurement by Sensor name: {}", measurement.getSensorName());
        measurementRepository.save(measurement);
    }

    private void enrichMeasurement(Measurement measurement) {
        measurement.setMeasurementDate(LocalDateTime.now());
        measurement.setWeatherSensor(weatherSensorRepository.findByName(measurement.getSensorName()).orElse(null));
        //System.out.println(measurement.getSensorName());
    }
}
