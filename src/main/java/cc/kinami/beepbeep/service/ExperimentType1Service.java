package cc.kinami.beepbeep.service;

import cc.kinami.beepbeep.exception.ErrorInfoEnum;
import cc.kinami.beepbeep.exception.KnownException;
import cc.kinami.beepbeep.model.dto.AddMultiExprDTO;
import cc.kinami.beepbeep.model.dto.ControlDTO;
import cc.kinami.beepbeep.model.dto.CreateBeepExprGroupDTO;
import cc.kinami.beepbeep.model.dto.CreateExperimentType1DTO;
import cc.kinami.beepbeep.model.entity.BeepExprGroup;
import cc.kinami.beepbeep.model.entity.DeviceInfo;
import cc.kinami.beepbeep.model.entity.ExperimentType1;
import cc.kinami.beepbeep.model.entity.ChirpParameters;
import cc.kinami.beepbeep.model.enums.ProcessControlEnum;
import cc.kinami.beepbeep.repo.BeepExprGroupRepository;
import cc.kinami.beepbeep.repo.ExperimentType1Repository;
import cc.kinami.beepbeep.util.MatlabUtil;
import cc.kinami.beepbeep.websocket.DeviceWebSocket;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWStringArray;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Service
@AllArgsConstructor
public class ExperimentType1Service {

    static String WEB_ROOT;

    static String EXPERIMENT_RELATIVE_PATH = "experiment/type1/";

    ExperimentType1Repository experimentType1Repository;

    BeepExprGroupRepository beepExprGroupRepository;

    @Value("${beepbeep.static-dir}")
    public void setWebRoot(String webRoot) {
        ExperimentType1Service.WEB_ROOT = webRoot;
        File dir = new File(ExperimentType1Service.WEB_ROOT + EXPERIMENT_RELATIVE_PATH);
        if (!dir.exists()) {
            boolean rst = dir.mkdirs();
            assert rst;
        }
    }

    public synchronized String storeRecord(int id, String from, String to, MultipartFile file) {
        String tarDir = WEB_ROOT + EXPERIMENT_RELATIVE_PATH + id + "/";
        String relativePath = EXPERIMENT_RELATIVE_PATH + id + "/";
        File dir = new File(tarDir);
        if (!dir.exists()) {
            boolean rst = dir.mkdirs();
            assert rst;
        }
        String tarFileName = from + "_to_" + to + ".wav";
        String tarFullPath = tarDir + tarFileName;
        relativePath += tarFileName;

        //尝试存储
        try {
            file.transferTo(new File(tarFullPath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException(ioe);
        }

        ExperimentType1 experimentType1 = experimentType1Repository.findByExperimentId(id);
        experimentType1.getRecordList().add("./" + relativePath);
        experimentType1Repository.save(experimentType1);

        return relativePath;
    }

    public int createExperiment(CreateExperimentType1DTO createExperimentType1DTO) {
        ArrayList<DeviceInfo> deviceList = new ArrayList<>();
        for (String device : createExperimentType1DTO.getDeviceList()) {
            System.out.println(device);
            if (DeviceWebSocket.getWebSocketMap().containsKey(device)) {
                DeviceInfo deviceInfo = DeviceInfo.builder()
                        .deviceName(device)
                        .m2sLength(DeviceWebSocket.getDeviceWebSocket(device).getSpeakerToMicrophoneDistance())
                        .build();
                deviceList.add(deviceInfo);
            } else
                throw new KnownException(ErrorInfoEnum.DEVICE_NOT_ONLINE);
        }
        int thisID = experimentType1Repository.findLastExperimentId() + 1;
        // 新建文件夹
        String experimentPath = WEB_ROOT + EXPERIMENT_RELATIVE_PATH + thisID + "/";
        File dir = new File(experimentPath);
        if (!dir.exists()) {
            boolean rst = dir.mkdir();
            assert rst;
        }
        ExperimentType1 experimentType1 = ExperimentType1.builder()
                .experimentId(thisID)
                .recordList(new ArrayList<>())
                .imageList(new ArrayList<>())
                .imageDescriptionList(new ArrayList<>())
                .chirpParameters(createExperimentType1DTO.getChirpParameters())
                .chirpFile(generateChirpFile(thisID, createExperimentType1DTO.getChirpParameters()))
                .build();
        experimentType1Repository.save(experimentType1);
        return thisID;
    }

    public int createExprGroup(CreateBeepExprGroupDTO createBeepExprGroupDTO) {
        int thisID = beepExprGroupRepository.findLastExperimentId() + 1;
        BeepExprGroup beepExprGroup = BeepExprGroup.builder()
                .experimentGroupId(thisID)
                .deviceList(createBeepExprGroupDTO.getDeviceList())
                .exprAbstract(createBeepExprGroupDTO.getExprAbstract())
                .beepMultiExprList(new ArrayList<>())
                .realDistance(createBeepExprGroupDTO.getRealDistance())
                .build();
        beepExprGroupRepository.save(beepExprGroup);
        return thisID;
    }

    public void addMultiExpr(AddMultiExprDTO addMultiExprDTO) {
        BeepExprGroup beepExprGroup = beepExprGroupRepository.findByExperimentGroupId(addMultiExprDTO.getExperimentGroupId());
        beepExprGroup.getBeepMultiExprList().add(addMultiExprDTO.getBeepMultiExpr());
        beepExprGroupRepository.save(beepExprGroup);
    }

    public ExperimentType1 getExpr(int id) {
        return experimentType1Repository.findByExperimentId(id);
    }

    public BeepExprGroup getExprGroup(int id) {
        return beepExprGroupRepository.findByExperimentGroupId(id);
    }

    public List<BeepExprGroup> getExprGroupList() {
        return beepExprGroupRepository.findAll();
    }


    public String getChirpFile(int exprId) {
        ExperimentType1 experimentType1 = experimentType1Repository.findByExperimentId(exprId);
        return experimentType1.getChirpFile();
    }

    public ExperimentType1 experimentBegin(int experimentId) {
        ExperimentType1 experimentType1 = experimentType1Repository.findByExperimentId(experimentId);
        final CountDownLatch latch1 = new CountDownLatch(experimentType1.getDeviceList().size());
        for (DeviceInfo device : experimentType1.getDeviceList()) {
            new Thread(() -> {
                // 首先获取chirp信号
                DeviceWebSocket deviceWebSocket = DeviceWebSocket.getDeviceWebSocket(device.getDeviceName());
                deviceWebSocket.sendControlInfo(ControlDTO.builder()
                        .controlInfo(ProcessControlEnum.GET_CHIRP)
                        .experimentId(experimentId)
                        .from("server")
                        .to(device.getDeviceName())
                        .experimentType(1)
                        .build());
                deviceWebSocket.waitUntil(ProcessControlEnum.GET_CHIRP_ACK);
                latch1.countDown();
            }).start();
        }
        // 等待所有人都获取了chirp信号
        try {
            latch1.await();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("latch 1 error");
        }

        // 两两一组进行测距
        for (int i = 0; i < experimentType1.getDeviceList().size(); i++) {
            for (int j = i + 1; j < experimentType1.getDeviceList().size(); j++) {

                String device1 = experimentType1.getDeviceList().get(i).getDeviceName();
                String device2 = experimentType1.getDeviceList().get(j).getDeviceName();

                DeviceWebSocket device1ws = DeviceWebSocket.getDeviceWebSocket(device1);
                DeviceWebSocket device2ws = DeviceWebSocket.getDeviceWebSocket(device2);

                // 设备1和设备2打开麦克风
                device1ws.sendControlInfo(ControlDTO.builder()
                        .controlInfo(ProcessControlEnum.START_RECORD)
                        .experimentId(experimentId)
                        .from(device2)
                        .to(device1)
                        .experimentType(1)
                        .mic(0)
                        .build());
                device2ws.sendControlInfo(ControlDTO.builder()
                        .controlInfo(ProcessControlEnum.START_RECORD)
                        .experimentId(experimentId)
                        .from(device1)
                        .to(device2)
                        .experimentType(1)
                        .mic(0)
                        .build());

                device1ws.waitUntil(ProcessControlEnum.START_RECORD_ACK);
                device2ws.waitUntil(ProcessControlEnum.START_RECORD_ACK);

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }

                // 设备1发送
                device1ws.sendControlInfo(ControlDTO.builder()
                        .controlInfo(ProcessControlEnum.PLAY_CHIRP)
                        .experimentId(experimentId)
                        .experimentType(1)
                        .build());
                device1ws.waitUntil(ProcessControlEnum.PLAY_CHIRP_ACK);
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }

                // 设备2发送
                device2ws.sendControlInfo(ControlDTO.builder()
                        .controlInfo(ProcessControlEnum.PLAY_CHIRP)
                        .experimentId(experimentId)
                        .experimentType(1)
                        .build());
                device2ws.waitUntil(ProcessControlEnum.PLAY_CHIRP_ACK);
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
                // 设备1和设备2接收
                device1ws.sendControlInfo(ControlDTO.builder()
                        .controlInfo(ProcessControlEnum.FINISH_RECORD)
                        .experimentId(experimentId)
                        .from(device2)
                        .to(device1)
                        .experimentType(1)
                        .build());
                device2ws.sendControlInfo(ControlDTO.builder()
                        .controlInfo(ProcessControlEnum.FINISH_RECORD)
                        .experimentId(experimentId)
                        .from(device1)
                        .to(device2)
                        .experimentType(1)
                        .build());
                device1ws.waitUntil(ProcessControlEnum.FINISH_RECORD_ACK);
                device2ws.waitUntil(ProcessControlEnum.FINISH_RECORD_ACK);
                List<String> recordList = experimentType1.getRecordList();
                recordList.add("./" + EXPERIMENT_RELATIVE_PATH + experimentId + "/" + device1 + "_to_" + device2 + ".wav");
                recordList.add("./" + EXPERIMENT_RELATIVE_PATH + experimentId + "/" + device2 + "_to_" + device1 + ".wav");

                experimentType1.setRecordList(recordList);
            }
        }


        computeDistanceOfTwoDevices(experimentId, experimentType1);
        experimentType1Repository.save(experimentType1);
        saveConfigFile(experimentId);

        return experimentType1;

    }

    private void saveConfigFile(int exprId) {
        ExperimentType1 experimentType1 = experimentType1Repository.findByExperimentId(exprId);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = "";
        try {
            jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(experimentType1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new KnownException(ErrorInfoEnum.JSON_ERROR);
        }
        String tarFilePath = WEB_ROOT + EXPERIMENT_RELATIVE_PATH + exprId + "/info.json";
        File tarFile = new File(tarFilePath);
        if (tarFile.exists())
            tarFile.delete();
        try {
            tarFile.createNewFile();
            FileWriter fileWriter = new FileWriter(tarFile);
            fileWriter.write(jsonStr);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new KnownException(ErrorInfoEnum.CREATE_FILE_ERROR);
        }

    }

    private String generateChirpFile(int exprId, ChirpParameters argument) {
        String tarFile = WEB_ROOT + EXPERIMENT_RELATIVE_PATH + exprId + "/chirp.wav";

        try {
            MatlabUtil matlabUtil = new MatlabUtil();
            matlabUtil.generateChirpFile(argument.getSamplingRate(),
                    argument.getLowerLimit(),
                    argument.getUpperLimit(),
                    argument.getChirpTime(),
                    argument.getPrepareTime(),
                    tarFile);
            matlabUtil.dispose();
        } catch (MWException e) {
            e.printStackTrace();
            throw new KnownException(ErrorInfoEnum.MATLAB_ERROR);
        }
        return "./" + EXPERIMENT_RELATIVE_PATH + exprId + "/chirp.wav";
    }

    private void computeDistanceOfTwoDevices(int exprId, ExperimentType1 experimentType1) {
        try {
            String recordFile1 = WEB_ROOT + experimentType1.getRecordList().get(0).substring(2);
            String recordFile2 = WEB_ROOT + experimentType1.getRecordList().get(1).substring(2);
            System.out.println(recordFile1);

            System.out.println(recordFile2);
            double m2s1 = experimentType1.getDeviceList().get(0).getM2sLength();
            double m2s2 = experimentType1.getDeviceList().get(1).getM2sLength();
            MatlabUtil matlabUtil = new MatlabUtil();
            Object[] oriRst = matlabUtil.calcDistance(3, recordFile1, recordFile2,
                    WEB_ROOT + EXPERIMENT_RELATIVE_PATH + exprId + "/chirp.wav",
                    m2s1 / 100, m2s2 / 100,
                    experimentType1.getChirpParameters().getSamplingRate(),
                    experimentType1.getChirpParameters().getLowerLimit(),
                    experimentType1.getChirpParameters().getUpperLimit(),
                    experimentType1.getChirpParameters().getSoundSpeed()
            );
            System.out.println(((MWNumericArray) oriRst[0]).getDouble());
            System.out.println(Arrays.asList(((MWStringArray) oriRst[1]).toArray()));
            System.out.println(Arrays.asList(((MWStringArray) oriRst[2]).toArray()));

            // 设置计算后的距离
            double rst = ((MWNumericArray) oriRst[0]).getDouble();
            experimentType1.setDistance(rst);
            // 设置图片列表
            ArrayList<String> imageList = new ArrayList<>();
            for (String imgFilename : ((String[]) ((MWStringArray) oriRst[1]).toArray()))
                imageList.add("./" + EXPERIMENT_RELATIVE_PATH + exprId + "/" + imgFilename);
            experimentType1.setImageList(imageList);
            // 设置图片描述


            matlabUtil.dispose();
            ((MWArray) oriRst[0]).dispose();
            ((MWArray) oriRst[1]).dispose();
            ((MWArray) oriRst[2]).dispose();


        } catch (MWException e) {
            e.printStackTrace();
            throw new KnownException(ErrorInfoEnum.MATLAB_ERROR);
        }
    }

}
