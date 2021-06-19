package cc.kinami.beepbeep.model.entity;

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
public class ExperimentType1 {
    @Id
    private String id;
    private Integer experimentId;
    private ArrayList<DeviceInfo> deviceList;
    private ArrayList<String> recordList;
    private ArrayList<String> imageList;
    private String chirpFile;
    private ChirpParameters chirpParameters;
    double distance;
}
