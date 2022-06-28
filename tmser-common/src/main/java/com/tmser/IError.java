package com.tmser;

public interface IError {
    String SUCCESS_CODE = "0";
    String SUCCESS_MSG = "Success";

    /**
     * 错误码：
     * 大于0 代表存在错误
     * 业务错误码规则如下：
     * 错误码拼接： 10 10 001
     * 前两位：服务.  10-学生,11-老师,12顾问，13其他, 14课程 （独立微服务） 统一配置
     * 中二位：模块,  服务自定义
     * 后三位: 错误类型， 常用的错误码参考 @see CommonError
     *
     */
    String getCode();

    /**
     * 错误名称
     * 可以用于国际化获取错误消息内容
     */
    default String getErrorKey() {
        return this.getClass().getSimpleName();
    }

    /**
     * 错误消息
     * @return
     */
   String getMessage();

}
