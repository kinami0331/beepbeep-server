package cc.kinami.beepbeep.controller;

import cc.kinami.beepbeep.model.dto.AddMultiExprDTO;
import cc.kinami.beepbeep.model.dto.CreateBeepExprGroupDTO;
import cc.kinami.beepbeep.model.dto.CreateExperimentType1DTO;
import cc.kinami.beepbeep.model.dto.ResponseDTO;
import cc.kinami.beepbeep.model.entity.BeepExprGroup;
import cc.kinami.beepbeep.model.entity.ExperimentType1;
import cc.kinami.beepbeep.service.ExperimentType1Service;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(value = "实验过程中的相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("/api/experiment")
public class ExperimentType1Controller {

    ExperimentType1Service experimentService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseDTO<String> handleRecordUpload(
            @RequestParam("file") @ApiParam(value = "用form-data传输的文件", required = true) MultipartFile file,
            @RequestParam("experimentId") int id,
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            HttpServletRequest request
    ) {
        String location = experimentService.storeRecord(id, from, to, file);
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + location;

        return new ResponseDTO<>(200, "OK", url);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseDTO<Integer> createExperiment(
            @RequestBody CreateExperimentType1DTO createExperimentType1DTO
    ) {
        Set<String> deviceSet = new HashSet<>(createExperimentType1DTO.getDeviceList());
        if (deviceSet.size() != createExperimentType1DTO.getDeviceList().size())
            throw new RuntimeException("device list error");

        return new ResponseDTO<>(200, "OK", experimentService.createExperiment(createExperimentType1DTO));
    }

    @RequestMapping(value = "/begin", method = RequestMethod.POST)
    public ResponseDTO<ExperimentType1> experimentBegin(
            @RequestBody JsonNode jsonNode
    ) {

        int experimentId = jsonNode.path("experimentId").asInt();

        return new ResponseDTO<>(200, "OK", experimentService.experimentBegin(experimentId));
    }

    @RequestMapping(value = "/getChirp", method = RequestMethod.GET)
    public ResponseDTO<String> getChirpFile(@RequestParam("experimentId") int exprId) {
        return new ResponseDTO<>(200, "OK", experimentService.getChirpFile(exprId));
    }

    @RequestMapping(value = "/group/create", method = RequestMethod.POST)
    public ResponseDTO<Integer> createExperimentGroup(
            @RequestBody CreateBeepExprGroupDTO createBeepExprGroupDTO
    ) {

        return new ResponseDTO<>(200, "OK", experimentService.createExprGroup(createBeepExprGroupDTO));
    }

    @RequestMapping(value = "/group/add", method = RequestMethod.POST)
    public ResponseDTO<String> addMultiExpr(
            @RequestBody AddMultiExprDTO addMultiExprDTO
    ) {
        experimentService.addMultiExpr(addMultiExprDTO);
        return new ResponseDTO<>(200, "OK", "");
    }

    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public ResponseDTO<BeepExprGroup> getExprGroup(
            @RequestParam("experimentGroupId") int id
    ) {
        return new ResponseDTO<>(200, "OK", experimentService.getExprGroup(id));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseDTO<ExperimentType1> getExpr(
            @RequestParam("experimentId") int id
    ) {
        return new ResponseDTO<>(200, "OK", experimentService.getExpr(id));
    }

    @RequestMapping(value = "/group/list", method = RequestMethod.GET)
    public ResponseDTO<List<BeepExprGroup>> getExprGroupList() {
        return new ResponseDTO<>(200, "OK", experimentService.getExprGroupList());
    }

}
