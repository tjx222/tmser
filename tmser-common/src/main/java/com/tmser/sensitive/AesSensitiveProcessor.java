package com.tmser.sensitive;

import com.tmser.codec.CipherUtils;
import com.tmser.enums.EConfigNamespace;
import com.tmser.util.ConfigUtils;
import com.tmser.util.StringUtils;

/**
 * Aes 敏感词解密器
 */
public class AesSensitiveProcessor extends  AbstractSensitiveProcess {

    @Override
    protected String doEncrypt(String sourseStr) {
        String aesKey = ConfigUtils.get(EConfigNamespace.CONFIG_ASEKEY.getMessage());
        if (StringUtils.isEmpty(aesKey)) {
            return sourseStr;
        }
        try {
            return CipherUtils.AES().encryptHexString(aesKey, sourseStr);
        } catch (Exception e) {
            logger.info("加密失败返回原值：{}, error message:{}", sourseStr, e);
        }
        return sourseStr;
    }

    @Override
    protected String doDecrypt(String encryptedStr) {
        String aesKey = ConfigUtils.get(EConfigNamespace.CONFIG_ASEKEY.getMessage());
        if (StringUtils.isEmpty(aesKey)) {
            return encryptedStr;
        }
        try {
            return CipherUtils.AES().decryptHexString(aesKey, encryptedStr);
        } catch (Exception e) {
            logger.info("解密失败返回原值：{}, error message:{}", encryptedStr, e);
        }
        return encryptedStr;
    }
}



