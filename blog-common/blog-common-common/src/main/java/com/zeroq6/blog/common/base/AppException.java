package com.zeroq6.blog.common.base;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

}
