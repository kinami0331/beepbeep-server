package cc.kinami.beepbeep.config;

import cc.kinami.beepbeep.util.MatlabUtil;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MatlabConfig {

    @PostConstruct
    public void initMatlab() {

        try {
            MatlabUtil matlabUtil = new MatlabUtil();
            matlabUtil.initMcr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
