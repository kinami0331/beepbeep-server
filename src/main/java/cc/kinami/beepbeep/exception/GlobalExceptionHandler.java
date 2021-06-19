package cc.kinami.beepbeep.exception;

import cc.kinami.beepbeep.model.dto.ResponseDTO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseDTO<Map<String, Object>> defaultExceptionHandler(Exception e) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("errMsg", e.getMessage());
        returnMap.put("errStackTrace", e.getStackTrace());
        return new ResponseDTO<>(40000, "Unknown Error", returnMap);
    }

}
