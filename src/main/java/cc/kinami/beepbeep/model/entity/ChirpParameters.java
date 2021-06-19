package cc.kinami.beepbeep.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ChirpParameters {
    double samplingRate;
    double lowerLimit;
    double upperLimit;
    double chirpTime;
    double prepareTime;
    double soundSpeed;
}
