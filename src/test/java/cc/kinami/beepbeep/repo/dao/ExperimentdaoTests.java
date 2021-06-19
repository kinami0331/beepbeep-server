package cc.kinami.beepbeep.repo.dao;

import cc.kinami.beepbeep.BeepApp;
import cc.kinami.beepbeep.repo.ExperimentType1Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BeepApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExperimentdaoTests {

    @Autowired
    ExperimentType1Repository experimentType1Repository;

    @Test
    public void saveTest() {
//        ExperimentType1 experiment = ExperimentType1.builder()
//                .deviceList(new ArrayList<>(Arrays.asList("device 1", "device 2")))
//                .recordList(new ArrayList<>())
//                .experimentId(3)
//                .build();
//        experimentType1Repository.save(experiment);
    }
}
