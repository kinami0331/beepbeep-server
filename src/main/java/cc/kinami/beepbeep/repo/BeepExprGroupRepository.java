package cc.kinami.beepbeep.repo;

import cc.kinami.beepbeep.model.entity.BeepExprGroup;
import cc.kinami.beepbeep.model.entity.ExperimentType1;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BeepExprGroupRepository extends MongoRepository<BeepExprGroup, ObjectId>, BeepExprGroupOperations {
    BeepExprGroup findByExperimentGroupId(Integer id);
}
