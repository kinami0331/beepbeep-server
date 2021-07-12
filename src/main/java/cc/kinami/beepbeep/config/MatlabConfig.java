package cc.kinami.beepbeep.config;

import cc.kinami.beepbeep.util.MatlabUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MatlabConfig {

    static boolean ENABLE_MATLAB;
    static boolean enableMatlab;
    @Value("${beepbeep.enable-matlab}")
    String enableMatlabConfig;

    static boolean isEnableMatlab() {
        return enableMatlab;
    }

    @PostConstruct
    public void initMatlab() {
        enableMatlab = enableMatlabConfig.equals("true");

        if (!enableMatlab)
            return;
        try {
            MatlabUtil matlabUtil = new MatlabUtil();
            matlabUtil.initMcr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
