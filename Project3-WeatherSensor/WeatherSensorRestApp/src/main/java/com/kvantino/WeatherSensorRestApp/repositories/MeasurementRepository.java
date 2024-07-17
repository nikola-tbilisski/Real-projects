package com.kvantino.WeatherSensorRestApp.repositories;

import com.kvantino.WeatherSensorRestApp.models.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {

    @Query(value = "SELECT COUNT(*) from measurements WHERE measurements.raining = :isRaining", nativeQuery = true)
    Integer countRainingTrueOrFalse(boolean isRaining);

    @Query(value = "SELECT * from measurements where measurements.value <= :value", nativeQuery = true)
    List<Measurement> findDaysDegreeLessThenOrEqualToValue(double value);

    List<Measurement> findBySensorName(String name);
}
