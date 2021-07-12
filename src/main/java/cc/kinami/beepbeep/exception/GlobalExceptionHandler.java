package cc.kinami.beepbeep.exception;

import cc.kinami.beepbeep.model.dto.ResponseDTO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = KnownException.class)
    public ResponseDTO<Map<String, Object>> knownExceptionHandler(Exception e) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("errMsg", e.getMessage());
        returnMap.put("errStackTrace", e.getStackTrace());
        e.printStackTrace();
        return new ResponseDTO<>(40000, "Unknown Error", returnMap);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseDTO<String> defaultExceptionHandler(KnownException e) {
        return new ResponseDTO<>(e.getErrCode(), e.getErrMsg(), "");
    }

}
