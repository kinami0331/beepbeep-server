package cc.kinami.beepbeep.model.dto;

import cc.kinami.beepbeep.model.entity.BeepMultiExpr;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddMultiExprDTO {
    Integer experimentGroupId;
    BeepMultiExpr beepMultiExpr;
}
