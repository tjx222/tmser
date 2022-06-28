package com.tmser;

public enum CommonError implements IError {


    /**
     * 错误码拼接： 100000
     * 前两位：模块 eg.  10-学生,11-老师,12顾问，13其他,14课程
     * 后三位：错误码
     * eg.121001-顾问没有权限执行此操作
     */
    SUCCESS("0", "SUCCESS"),
    FAILED("99", "unknown error");//未知错误

    private IError error;

    private CommonError(String errorCode, String message) {
        error = new AbstractError("00", errorCode, message) {
            @Override
            public String getSysCode() {
                return "00";
            }
        };
    }


    @Override
    public String getCode() {
        return error.getCode();
    }

    @Override
    public String getMessage() {
        return error.getMessage();
    }
}
