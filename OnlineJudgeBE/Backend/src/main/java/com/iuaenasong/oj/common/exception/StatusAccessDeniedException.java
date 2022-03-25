/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.common.exception;

public class StatusAccessDeniedException extends Exception {

    public StatusAccessDeniedException() {
    }

    public StatusAccessDeniedException(String message) {
        super(message);
    }

    public StatusAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public StatusAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}