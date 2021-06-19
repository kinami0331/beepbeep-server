package cc.kinami.beepbeep.repo.impl;

import cc.kinami.beepbeep.model.entity.ExperimentType1;
import cc.kinami.beepbeep.repo.ExperimentType2Operations;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ExperimentType2RepositoryImpl implements ExperimentType2Operations {

    private final MongoTemplate mongo;

    @Override
    public int findLastExperimentId() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "experimentId"));
        query.limit(1);
        List<ExperimentType1> rst = mongo.find(query, ExperimentType1.class, "experimentType2");
        if (rst.isEmpty())
            return 0;
        else
            return rst.get(0).getExperimentId();
    }
}
