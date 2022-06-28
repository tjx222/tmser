package com.tmser;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractError implements IError {

    private String code;

    private final String moduleCode;

    private final String errorCode;

    private final String message;

    private final static Map<String, IError> errorHashMap = Maps.newHashMap();

    public AbstractError(String moduleCode, String errorCode, String message) {
        this.moduleCode = moduleCode;
        this.errorCode = errorCode;
        this.message = message;
    }

    public final String getCode() {
        checkSysCode(getSysCode());
        if (Objects.nonNull(getSysCode())) {
            code = getSysCode() + moduleCode + errorCode;
        }
        return code;
    }

    protected void checkSysCode(String sysCode) {
    //检查是否配置的系统
    }

    abstract String getSysCode();

    public String getMessage() {
        return message;
    }

    /**
     * 获取错误类型
     */
    public static String getMessage(String code) {
        if (Objects.nonNull(errorHashMap.get(code))) {
            return errorHashMap.get(code).getMessage();
        }
        return "";
    }

}
