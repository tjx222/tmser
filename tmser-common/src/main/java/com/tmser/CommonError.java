package com.tmser;

public enum CommonError implements IError{

    /**
     * 公共错误码: 一位数字。
     * 错误码拼接： 100000
     * 前两位：模块 eg.  10-学生,11-老师,12顾问，13其他,14课程
     * 后三位：错误码
     * eg.121001-顾问没有权限执行此操作
     */
    SUCCESS(0, "SUCCESS"),
    FAILED(1,"FAILED"), //通用错误
    NO_PERMISSION(10, "no permission!"),//通用权限错误
    NOT_EXIST(11, "not exist!"),//通用资源不存在
    BAD_PARAMETER(12, "illegal parameter!"),//通用参数非法，如类型错误，格式错误， 参数范围
    NOT_EMPTY(13, "not empty"), //参数不能为空
    BAD_TOKEN(14,"bad token"),//通用签名或token错误， 签名不存在，或存在错误或过期
    DUPLICATE_DATA(15,"duplicate data"),//通用数据重复
    UNKNOWN(99, "unknown error");//自定义错误

    private final Integer code;

    private final String message;

    CommonError(Integer code, String name) {
        this.code = code;
        this.message = name;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getErrorKey() {
        return this.getClass().getSimpleName();
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过状态值获取状态名称
     */
    public static String getMessage(int code) {
        for (CommonError item : CommonError.values()) {
            if (item.getCode() == code) {
                return item.getMessage();
            }
        }
        return "";
    }

}
