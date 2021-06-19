package cc.kinami.beepbeep.util;

import cc.kinami.beepbeep.BeepApp;
import cc.kinami.beepbeep.model.enums.ProcessControlEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathworks.toolbox.javabuilder.MWException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BeepApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UtilTest {

    @Test
    public void matlabTest() {
//        try {
//            ChirpGenerator generator = new ChirpGenerator();
//            generator.generateChirpFile(44100.0, 2000.0, 6000.0, 100.0, 10.0, "test2.wav");
//        } catch (MWException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void enumToJsonTest() {


        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(new A(ProcessControlEnum.FINISH_RECORD_ACK, 5));
            System.out.println(jsonString);
            System.out.println(mapper.readValue("{\"controlEnum\":\"finish_record\",\"code\":5}", A.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class A {
    ProcessControlEnum controlEnum;
    int code;
}
