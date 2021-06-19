package cc.kinami.beepbeep.exception;

import lombok.Getter;

@Getter
public class KnownException extends RuntimeException {
    private final Integer errCode;
    private final String errMsg;

    public KnownException(ErrorInfoEnum errorInfoEnum) {
        super(errorInfoEnum.getErrMsg());
        this.errCode = errorInfoEnum.getErrCode();
        this.errMsg = errorInfoEnum.getErrMsg();
    }
}
