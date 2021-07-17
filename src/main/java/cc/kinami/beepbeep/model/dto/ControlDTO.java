package cc.kinami.beepbeep.model.dto;

import cc.kinami.beepbeep.model.enums.ProcessControlEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlDTO {
    ProcessControlEnum controlInfo;
    Integer experimentId;
    String from;
    String to;
    Integer experimentType;
    Integer mic;
    Integer fs;
}
