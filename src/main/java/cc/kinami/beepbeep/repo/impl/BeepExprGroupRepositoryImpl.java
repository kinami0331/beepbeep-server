package cc.kinami.beepbeep.repo.impl;

import cc.kinami.beepbeep.model.entity.BeepExprGroup;
import cc.kinami.beepbeep.model.entity.ExperimentType1;
import cc.kinami.beepbeep.repo.BeepExprGroupOperations;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class BeepExprGroupRepositoryImpl implements BeepExprGroupOperations {
    private final MongoTemplate mongo;

    @Override
    public int findLastExperimentId() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "experimentGroupId"));
        query.limit(1);
        List<BeepExprGroup> rst = mongo.find(query, BeepExprGroup.class, "beepExprGroup");
        if (rst.isEmpty())
            return 0;
        else
            return rst.get(0).getExperimentGroupId();
    }
}
