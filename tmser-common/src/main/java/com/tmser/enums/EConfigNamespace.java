package com.tmser.enums;

/**
 * @author tmser
 * @version 1.0
 * @title EConfigNamespace 系统配置和名称
 * @description apollo 配置中心 name space
 * @changeRecord
 */
public  enum EConfigNamespace {
    // 配置中心 name space
    CONFIG_ASEKEY("sensitive.aes_key", "aes加密秘钥");

    private final String message;

    private final String des;

    public String getMessage(){
        return this.message;
    }

    EConfigNamespace(String message, String des) {
        this.message = message;
        this.des = des;
    }


}
