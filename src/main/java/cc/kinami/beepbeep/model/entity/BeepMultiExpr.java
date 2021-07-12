package cc.kinami.beepbeep.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


//同样参数的多次重复实验
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeepMultiExpr {
    private ChirpParameters chirpParameters;
    private List<Integer> exprIdList;
}
