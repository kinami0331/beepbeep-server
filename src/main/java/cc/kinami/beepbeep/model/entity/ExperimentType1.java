package cc.kinami.beepbeep.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentType1 {
    @Id
    private String id;
    private Integer experimentId;
    private List<DeviceInfo> deviceList;
    private List<String> recordList;
    private List<String> imageList;
    private List<String> imageDescriptionList;
    private String chirpFile;
    private ChirpParameters chirpParameters;
    double distance;
}
