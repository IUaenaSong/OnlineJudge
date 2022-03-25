/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.common.exception;

import lombok.Data;

@Data
public class RuntimeError extends Exception {
    private String stdout;
    private String stderr;

    public RuntimeError(String message, String stdout, String stderr) {
        super(message);
        this.stdout = stdout;
        this.stderr = stderr;
    }
}