package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementToSendDTO {
    private String sensorName;
    private Double value;
    private Boolean raining;
}
