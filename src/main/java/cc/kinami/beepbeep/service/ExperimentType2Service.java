package cc.kinami.beepbeep.service;

import cc.kinami.beepbeep.exception.ErrorInfoEnum;
import cc.kinami.beepbeep.exception.KnownException;
import cc.kinami.beepbeep.model.dto.ControlDTO;
import cc.kinami.beepbeep.model.dto.CreateExperimentType2DTO;
import cc.kinami.beepbeep.model.entity.DeviceInfo;
import cc.kinami.beepbeep.model.entity.ExperimentType1;
import cc.kinami.beepbeep.model.entity.ExperimentType2;
import cc.kinami.beepbeep.model.enums.ProcessControlEnum;
import cc.kinami.beepbeep.repo.ExperimentType2Repository;
import cc.kinami.beepbeep.util.MatlabUtil;
import cc.kinami.beepbeep.websocket.DeviceWebSocket;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Service
@AllArgsConstructor
public class ExperimentType2Service {

    static String WEB_ROOT;

    static String EXPERIMENT_RELATIVE_PATH = "experiment/type2/";

    ExperimentType2Repository experimentType2Repository;

    @Value("${beepbeep.static-dir}")
    public void setWebRoot(String webRoot) {
        ExperimentType2Service.WEB_ROOT = webRoot;
        File dir = new File(ExperimentType2Service.WEB_ROOT + EXPERIMENT_RELATIVE_PATH);
        if (!dir.exists()) {
            boolean rst = dir.mkdirs();
            assert rst;
        }

        String relativeSignalPath = EXPERIMENT_RELATIVE_PATH + "_signals/with_warming_up/";
//        System.out.println(relativeSignalPath);
        dir = new File(ExperimentType2Service.WEB_ROOT + relativeSignalPath);
        if (!dir.exists()) {
            boolean rst = dir.mkdirs();
            assert rst;
        }
    }

    public synchronized String storeRecord(int id, MultipartFile file) {
        String tarDir = WEB_ROOT + EXPERIMENT_RELATIVE_PATH + id + "/";
        String relativePath = EXPERIMENT_RELATIVE_PATH + id + "/";
        File dir = new File(tarDir);
        if (!dir.exists()) {
            boolean rst = dir.mkdirs();
            assert rst;
        }
        String tarFileName = "record.wav";
        String tarFullPath = tarDir + tarFileName;
        relativePath += tarFileName;

        //尝试存储
        try {
            file.transferTo(new File(tarFullPath));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return relativePath;
    }

    public ExperimentType2 createExperiment(CreateExperimentType2DTO createExperimentType2DTO) {
        // 首先检测设备是否在线
//        if (!DeviceWebSocket.getWebSocketMap().containsKey(createExperimentType2DTO.getDeviceName()))
//            throw new KnownException(ErrorInfoEnum.DEVICE_NOT_ONLINE);

        int thisID = experimentType2Repository.findLastExperimentId() + 1;
        // 新建文件夹
        String experimentPath = WEB_ROOT + EXPERIMENT_RELATIVE_PATH + thisID + "/";
        File dir = new File(experimentPath);
        if (!dir.exists()) {
            boolean rst = dir.mkdir();
            assert rst;
        }
        ExperimentType2 experimentType2 = ExperimentType2.builder()
                .experimentId(thisID)
                .experimentPath("./" + EXPERIMENT_RELATIVE_PATH)
                .targetDeviceMic(createExperimentType2DTO.getDeviceMic())
                .targetDeviceName((createExperimentType2DTO.getDeviceName()))
                .signalId(createExperimentType2DTO.getSignalId())
                .signalPath("./" + getSignalPath(createExperimentType2DTO.getSignalId()))
                .masterDeviceName(createExperimentType2DTO.getMasterDeviceName())
                .build();

        experimentType2Repository.save(experimentType2);

        return experimentType2;
    }

    public void experimentBegin(int experimentId) {
        ExperimentType2 experimentType2 = experimentType2Repository.findByExperimentId(experimentId);

        DeviceWebSocket tarDeviceWs = DeviceWebSocket.getDeviceWebSocket(experimentType2.getTargetDeviceName());
        DeviceWebSocket masterDeviceWs = DeviceWebSocket.getDeviceWebSocket(experimentType2.getMasterDeviceName());

        // 通知tar设备打开麦克风
        tarDeviceWs.sendControlInfo(ControlDTO.builder()
                .controlInfo(ProcessControlEnum.START_RECORD)
                .experimentId(experimentId)
                .mic(experimentType2.getTargetDeviceMic())
                .experimentType(2)
                .fs(48000)
                .build());
        tarDeviceWs.waitUntil(ProcessControlEnum.START_RECORD_ACK);

        // 等待一阵
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        // 通知master发信号
        masterDeviceWs.sendControlInfo(ControlDTO.builder()
                .controlInfo(ProcessControlEnum.PLAY_CHIRP)
                .experimentId(experimentId)
                .experimentType(2)
                .build());
        masterDeviceWs.waitUntil(ProcessControlEnum.PLAY_CHIRP_ACK);

        // 等待一阵
        try {
            Thread.sleep(600);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        // 通知tar关闭麦克风并上传
        tarDeviceWs.sendControlInfo(ControlDTO.builder()
                .controlInfo(ProcessControlEnum.FINISH_RECORD)
                .experimentId(experimentId)
                .experimentType(2)
                .build());
        tarDeviceWs.waitUntil(ProcessControlEnum.FINISH_RECORD_ACK);

        // 计算结果
        receivedSignalAnalysis(WEB_ROOT + getSignalPath(experimentType2.getSignalId(), true),
                WEB_ROOT + EXPERIMENT_RELATIVE_PATH + experimentId + "/record.wav",
                WEB_ROOT + EXPERIMENT_RELATIVE_PATH + experimentId + "/");


    }

    private String getSignalPath(int signalId) {
        return getSignalPath(signalId, false);
    }

    private String getSignalPath(int signalId, boolean isPure) {
        String relativeSignalPath;
        if (isPure)
            relativeSignalPath = EXPERIMENT_RELATIVE_PATH + "_signals/pure/";
        else
            relativeSignalPath = EXPERIMENT_RELATIVE_PATH + "_signals/with_warming_up/";
        File dir = new File(WEB_ROOT + relativeSignalPath);

        File[] files = dir.listFiles();
        if (files == null)
            throw new KnownException(ErrorInfoEnum.NO_SUCH_SIGNAL_ERROR);
        for (File file : files) {
            if (file.getName().startsWith(signalId + "_")) {
                return relativeSignalPath + file.getName();
            }
        }
        throw new KnownException(ErrorInfoEnum.NO_SUCH_SIGNAL_ERROR);
    }

    private void receivedSignalAnalysis(String originalSignalFile, String receivedSignalFile, String experimentPath) {
        System.out.println(originalSignalFile);
        System.out.println(receivedSignalFile);
        System.out.println(experimentPath);
        try {
            MatlabUtil matlabUtil = new MatlabUtil();
            matlabUtil.recordAnalysis(originalSignalFile, receivedSignalFile, experimentPath);
            matlabUtil.dispose();
        } catch (MWException e) {
            throw new KnownException(ErrorInfoEnum.MATLAB_ERROR);
        }
    }
}
