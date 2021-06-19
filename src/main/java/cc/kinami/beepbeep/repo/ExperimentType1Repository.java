package cc.kinami.beepbeep.repo;

import cc.kinami.beepbeep.model.entity.ExperimentType1;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExperimentType1Repository extends MongoRepository<ExperimentType1, ObjectId>, ExperimentType1Operations {
    ExperimentType1 findByExperimentId(Integer id);
}
