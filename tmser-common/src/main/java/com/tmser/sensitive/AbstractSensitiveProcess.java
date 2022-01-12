package com.tmser.sensitive;

import com.tmser.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象sensitive 处理器
 */
public abstract class AbstractSensitiveProcess implements SensitiveProcessor {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static ConcurrentHashMap<String, Field[]> fieldMap = new ConcurrentHashMap<>(32);

    /**
     * @param object 需要进行加密处理的对象，基本类型及其包装类型不处理，
     *               Iterable 可迭代类型，迭代处理内部元素。
     *               其他类型， 判定是否包含 SensitiveClass 注解， 则遍历其属性，对包含 SensitiveField 属性进行处理
     * @return 加密后对象，加密失败或不符合需加密规则，返回原值
     */
    @Override
    public <T> T encrypt(T object) {
        if (object instanceof String) {
            return (T) doEncrypt((String) object);
        }

        try {
            if (Objects.nonNull(object)) {
                if (object.getClass().isAnnotationPresent(SensitiveClass.class)) {
                    return encryptSensitiveClass(object, false);
                }

                if (object instanceof Iterable) {
                    for (Object temp : (Iterable) object) {
                        if (temp.getClass().isAnnotationPresent(SensitiveClass.class)) {
                            encryptSensitiveClass(temp, false);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.info("加密异常：{}, error message: {}", object, e.getMessage());
        }

        return object;
    }

    /**
     * 加解密 sensitiveClass
     * 只处理标识为SensitiveField ，且类型为String, 或 注解有SensitiveClass 或 Iterable
     * Iterable 只处理 ArrayList<String>, 或元素为注解有SensitiveClass的对象
     *
     * @param object    要进行加解密对象
     * @param isDecrypt 是否解密
     * @return 解密后对象
     * @throws IllegalAccessException 是否加解密
     */
    protected <T> T encryptSensitiveClass(T object, boolean isDecrypt) throws IllegalAccessException {
        Field[] fields = getFields(object);
        for (Field field : fields) {
            //设置为已处理加解密
            if (field.isAnnotationPresent(SensitiveField.class)) {
                field.setAccessible(true);
                Object o = field.get(object);
                if (o instanceof String) {
                    if (StringUtils.isEmpty(o.toString())) {
                        continue;
                    }
                    String value = isDecrypt ? doDecrypt(o.toString()) : doEncrypt(o.toString());
                    field.set(object, value);
                } else if (Objects.nonNull(o) && o.getClass().isAnnotationPresent(SensitiveClass.class)) {
                    encryptSensitiveClass(o, isDecrypt);
                } else if (o instanceof Iterable) { //集合
                    Iterable<String> t = null;
                    for (Object temp : (Iterable) o) {
                        if (Objects.isNull(temp)) {
                            continue;
                        }
                        if (temp instanceof String) {
                            t = doSaveIterableString(field, o, (String) temp, isDecrypt, t);
                        } else if (temp.getClass().isAnnotationPresent(SensitiveClass.class)) {
                            encryptSensitiveClass(temp, isDecrypt);
                        } else { //Iterable 元素非String 且 不存在SensitiveClass 注解
                            break;
                        }
                    }
                }
            }
        }

        return object;
    }


    //处理String 集合
    protected Iterable<String> doSaveIterableString(Field field, Object o, String oldValue, boolean isDecrypt, Iterable<String> t) throws IllegalAccessException {
        if (o instanceof ArrayList) {
            if (t == null) {
                t = new ArrayList<>();
                field.set(o, t);
            }
            ArrayList<String> arr = (ArrayList<String>) t;
            String value = isDecrypt ? doDecrypt(oldValue) : doEncrypt(oldValue);
            arr.add(value);
        }

        return t;
    }

    private static Field[] getFields(Object object) {
        String className = object.getClass().getName();
        return fieldMap.computeIfAbsent(className, cn -> object.getClass().getDeclaredFields());
    }


    /**
     * 加密
     *
     * @param sourseStr 源码字符串
     * @return 加密后字符串
     */
    protected abstract String doEncrypt(String sourseStr);


    /**
     * 解密方法
     *
     * @param object 需要进行解密处理的对象，基本类型及其包装类型，不处理，
     *               *               Iterable 可迭代类型，迭代处理内部元素。
     *               *               其他类型， 判定是否包含 SensitiveClass 注解， 则遍历其属性，对包含 SensitiveField 属性进行处理
     * @return 解密好对象
     */
    public <T> T decrypt(T object) {
        if (object instanceof String) {
            return (T) doDecrypt((String) object);
        }

        try {
            if (Objects.nonNull(object)) {
                if (object.getClass().isAnnotationPresent(SensitiveClass.class)) {
                    return encryptSensitiveClass(object, true);
                }

                if (object instanceof Iterable) {
                    for (Object temp : (Iterable) object) {
                        if (temp.getClass().isAnnotationPresent(SensitiveClass.class)) {
                            encryptSensitiveClass(temp, true);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.info("解密异常：{}, error message: {}", object, e.getMessage());
        }

        return object;
    }


    /**
     * 解密
     *
     * @param encryptedStr 源码字符串
     * @return 解密后字符串
     */
    protected abstract String doDecrypt(String encryptedStr);
}
