package cc.kinami.beepbeep;

import cc.kinami.beepbeep.util.MatlabUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class BeepApp {

    public static void main(String[] args) {
        SpringApplication.run(BeepApp.class, args);
    }

}
