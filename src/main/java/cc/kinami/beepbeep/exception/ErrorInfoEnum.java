package cc.kinami.beepbeep.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorInfoEnum {
    COMMON_ERROR(40000, "就是错了"),
    DEVICE_NOT_ONLINE(40001, "设备不在线，请检查实验设备"),
    MATLAB_ERROR(40002, "matlab库错误"),
    NO_SUCH_SIGNAL_ERROR(40003, "没有这个信号");

    private final Integer errCode;
    private final String errMsg;
}
