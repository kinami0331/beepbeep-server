package cc.kinami.beepbeep.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBeepExprGroupDTO {
    private ArrayList<String> deviceList;
    private String exprAbstract;
    private Double realDistance;
}
