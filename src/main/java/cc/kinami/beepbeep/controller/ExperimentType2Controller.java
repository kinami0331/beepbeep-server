package cc.kinami.beepbeep.controller;

import cc.kinami.beepbeep.model.dto.CreateExperimentType1DTO;
import cc.kinami.beepbeep.model.dto.CreateExperimentType2DTO;
import cc.kinami.beepbeep.model.dto.ResponseDTO;
import cc.kinami.beepbeep.model.entity.ExperimentType2;
import cc.kinami.beepbeep.service.ExperimentType2Service;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/experiment/type2")
public class ExperimentType2Controller {

    ExperimentType2Service experimentService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseDTO<String> handleRecordUpload(
            @RequestParam("file") @ApiParam(value = "用form-data传输的文件", required = true) MultipartFile file,
            @RequestParam("experimentId") int id,
            HttpServletRequest request
    ) {
        String location = experimentService.storeRecord(id, file);
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + location;

        return new ResponseDTO<>(200, "OK", url);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseDTO<ExperimentType2> createExperiment(
            @RequestBody CreateExperimentType2DTO createExperimentType2DTO
    ) {

        return new ResponseDTO<>(200, "OK", experimentService.createExperiment(createExperimentType2DTO));
    }

    @RequestMapping(value = "/begin", method = RequestMethod.POST)
    public ResponseDTO<String> experimentBegin(
            @RequestBody JsonNode jsonNode
    ) {

        int experimentId = jsonNode.path("experimentId").asInt();
        experimentService.experimentBegin(experimentId);

        return new ResponseDTO<>(200, "OK", "");
    }

}
