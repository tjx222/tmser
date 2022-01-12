package com.tmser.sensitive;

/**
 * 敏感词加解密处理器
 * <p>
 *     对持久化字段进行加解密处理
 * </p>
 */
public interface SensitiveProcessor {
    /**
     * 加密方法
     *
     * @param object 需要进行加密处理的对象，基本类型及其包装类型不处理，
     *               Iterable 可迭代类型，迭代处理内部元素。
     *               其他类型， 判定是否包含 SensitiveClass 注解， 则遍历其属性，对包含 SensitiveField 注解且为String类型属性进行处理
     *
     * @return 加密好的对象
     */
    <T> T encrypt(T object);


    /**
     * 解密方法
     *
     * @param object  需要进行解密处理的对象，基本类型及其包装类型，不处理，
     *      *               Iterable 可迭代类型，迭代处理内部元素。
     *      *               其他类型， 判定是否包含 SensitiveClass 注解， 则遍历其属性，对包含 SensitiveField 属性进行处理
     * @return 解密好对象
     */
    <T> T decrypt(T object);
}
