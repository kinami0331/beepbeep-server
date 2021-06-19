package cc.kinami.beepbeep.service;

import cc.kinami.beepbeep.websocket.DeviceWebSocket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class ManageService {

    public ArrayList<String> getOnlineDeviceList() {
        ArrayList<String> arrayList = new ArrayList<>();
        for(String device: DeviceWebSocket.getWebSocketMap().keySet()) {
            arrayList.add(device);
        }

        return arrayList;
    }
}
