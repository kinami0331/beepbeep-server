package cc.kinami.beepbeep.repo;

import cc.kinami.beepbeep.model.entity.ExperimentType1;
import cc.kinami.beepbeep.model.entity.ExperimentType2;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExperimentType2Repository extends MongoRepository<ExperimentType2, ObjectId>, ExperimentType2Operations {
    ExperimentType2 findByExperimentId(Integer id);
}
