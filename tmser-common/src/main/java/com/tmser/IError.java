package com.tmser;

public interface IError {
    int SUCCESS_CODE = 0;
    String SUCCESS_MSG = "Success";

    /**
     * 错误码：
     * 大于0 代表存在错误
     * 公共错误码: 10-49 代表。
     * 业务错误码规则如下：
     * 错误码拼接： 10 0001
     * 前两位：模块 eg.  10-学生,11-老师,12顾问，13其他,14课程
     * 后三位：错误码, 与公共错误类型错误类似，可使用公共错误码作为前缀
     * 非公共错误以5xxx-9999
     * eg.12002-顾问没有权限执行此操作
     */
   Integer getCode();

    /**
     * 错误名称
     * 可以用于国际化获取错误消息内容
     */
   String getErrorKey();

    /**
     * 错误消息
     * @return
     */
   String getMessage();

}
