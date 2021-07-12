package cc.kinami.beepbeep.websocket;

import cc.kinami.beepbeep.model.dto.ControlDTO;
import cc.kinami.beepbeep.model.enums.ProcessControlEnum;
import cc.kinami.beepbeep.service.ExperimentType1Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Getter
@ServerEndpoint(value = "/ws/{deviceID}/{speakerToMicrophoneDistance}")
public class DeviceWebSocket {

    private final static Logger logger = LoggerFactory.getLogger(DeviceWebSocket.class);

    @Getter
    private static final ConcurrentHashMap<String, DeviceWebSocket> webSocketMap = new ConcurrentHashMap<>();

    ProcessControlEnum controlEnum;
    private Session session;
    private String deviceID;
    private Double speakerToMicrophoneDistance;


    public static void sendMsgToDevice(String message, String deviceID) {
    }

    public static DeviceWebSocket getDeviceWebSocket(String deviceID) {
        if (webSocketMap.containsKey(deviceID))
            return webSocketMap.get(deviceID);
        else
            throw new RuntimeException("device not exist");
    }

    public void sendControlInfo(ControlDTO controlDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            session.getAsyncRemote().sendText(mapper.writeValueAsString(controlDTO));
        } catch (Exception e) {
            throw new RuntimeException("websocket info error");
        }
    }

    public void waitUntil(ProcessControlEnum controlEnum) {
        ProcessControlEnum currentControlEnum;
        synchronized (this) {
            currentControlEnum = this.controlEnum;
        }
        while (currentControlEnum != controlEnum && currentControlEnum != ProcessControlEnum.CLIENT_ERROR) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                throw new RuntimeException("wait error");
            }
            synchronized (this) {
                currentControlEnum = this.controlEnum;
            }
        }
        if (currentControlEnum == ProcessControlEnum.CLIENT_ERROR)
            throw new RuntimeException("client error");
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("deviceID") String deviceID,
                       @PathParam("speakerToMicrophoneDistance") Double speakerToMicrophoneDistance) {
        this.session = session;
        webSocketMap.put(deviceID, this);
        this.deviceID = deviceID;
        this.speakerToMicrophoneDistance = speakerToMicrophoneDistance;

        logger.info("New device connected: id = " + deviceID + ", speakerToMicrophoneDistance = " + speakerToMicrophoneDistance);
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(this.deviceID);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("Received message: " + message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ControlDTO controlDTO = mapper.readValue(message, ControlDTO.class);
            synchronized (this) {
                controlEnum = controlDTO.getControlInfo();
            }

        } catch (Exception e) {
            throw new RuntimeException("websocket info error");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
