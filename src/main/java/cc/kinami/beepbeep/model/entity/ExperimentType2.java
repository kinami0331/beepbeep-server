package cc.kinami.beepbeep.model.entity;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentType2 {
    @Id
    private String id;
    // 实验id
    private Integer experimentId;
    // 控制设备名
    private String masterDeviceName;
    // 目标设备名
    private String targetDeviceName;
    // 设备用的麦克风
    private Integer targetDeviceMic;
    // 使用的信号id
    private Integer signalId;
    // 信号的相对地址
    private String signalPath;
    // 路径
    private String experimentPath;

}
