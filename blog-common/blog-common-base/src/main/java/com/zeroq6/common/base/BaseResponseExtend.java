package com.zeroq6.common.base;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class BaseResponseExtend<T> extends BaseResponse<T> {

    private static final long serialVersionUID = 1L;

    private String code;

    public BaseResponseExtend(){}

    public BaseResponseExtend(boolean success, String code, String message, T body) {
        super(success, message, body);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
