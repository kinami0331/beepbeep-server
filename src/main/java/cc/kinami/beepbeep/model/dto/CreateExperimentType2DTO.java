package cc.kinami.beepbeep.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateExperimentType2DTO {

    // 控制设备名
    private String masterDeviceName;
    // 设备名
    private String deviceName;
    // 设备用的麦克风，0为主麦克风，1为上方麦克风
    private Integer deviceMic;
    // 使用的信号id
    private Integer signalId;
}
