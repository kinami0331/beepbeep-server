package cc.kinami.beepbeep.controller;

import cc.kinami.beepbeep.model.dto.ResponseDTO;
import cc.kinami.beepbeep.service.ManageService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Api(value = "管理接口")
@RestController
@AllArgsConstructor
@RequestMapping("/api/manage")
public class ManageController {

    ManageService manageService;

    @RequestMapping(value = "/device-list", method = RequestMethod.GET)
    public ResponseDTO<ArrayList<String>> getOnlineDeviceList() {

        return new ResponseDTO<>(200, "ok", manageService.getOnlineDeviceList());
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseDTO<String> helloWorld(@RequestParam(required = false) String name) {
        if (name == null)
            return new ResponseDTO<>(200, "OK", "Hello, world!");
        else
            return new ResponseDTO<>(200, "OK", "Hello, " + name + "!");
    }
}
