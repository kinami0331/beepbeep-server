package cc.kinami.beepbeep.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class BeepExprGroup {
    @Id
    private String id;
    private Integer experimentGroupId;
    private List<String> deviceList;
    private List<BeepMultiExpr> beepMultiExprList;
    private String exprAbstract;
    private Double realDistance;
}
