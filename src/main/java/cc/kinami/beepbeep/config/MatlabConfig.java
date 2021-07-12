package cc.kinami.beepbeep.config;

import cc.kinami.beepbeep.service.ExperimentType2Service;
import cc.kinami.beepbeep.util.MatlabUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MatlabConfig {

    private final static Logger logger = LoggerFactory.getLogger(MatlabConfig.class);
    static boolean ENABLE_MATLAB;
    @Value("${beepbeep.enable-matlab}")
    String enableMatlabConfig;

    static boolean isEnableMatlab() {
        return ENABLE_MATLAB;
    }

    @PostConstruct
    public void initMatlab() {
        ENABLE_MATLAB = enableMatlabConfig.equals("true");


        if (!ENABLE_MATLAB) {
            logger.info("Enable python");
            return;
        } else
            logger.info("Enable matlab");

        try {
            long startTime = System.currentTimeMillis();
            MatlabUtil matlabUtil = new MatlabUtil();
            matlabUtil.initMcr();
            long endTime = System.currentTimeMillis();
            logger.info("Matlab Util: initialization completed in " + (endTime - startTime) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
