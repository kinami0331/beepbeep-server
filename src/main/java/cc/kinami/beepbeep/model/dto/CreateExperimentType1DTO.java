package cc.kinami.beepbeep.model.dto;

import cc.kinami.beepbeep.model.entity.DeviceInfo;
import cc.kinami.beepbeep.model.entity.ChirpParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateExperimentType1DTO {
    private ArrayList<String> deviceList;
    private ChirpParameters chirpParameters;
}
