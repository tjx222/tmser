package com.tmser.spring;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Map;


public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringApplicationContext.applicationContext = applicationContext;
    }

    public static void setAppContext(
            ApplicationContext applicationContext) {
        SpringApplicationContext.applicationContext = applicationContext;
    }

    /**
     * @param <T>
     * @param beanName
     * @return 从spring容器中取出一个bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        Object ret = applicationContext.getBean(beanName);
        if (ret == null) {
            throw new RuntimeException("Spring容器里没有名字为" + beanName + "的bean");
        }
        return (T) ret;
    }

    /**
     * @param beanName * 注意 bean name默认 = 类名(首字母小写)
     *                 例如: A8sClusterDao = getBean("k8sClusterDao")
     * @param args
     * @return 从spring容器中取出一个bean
     */
    public static <T> T getBean(String beanName, Object... args) {
        Object ret = applicationContext.getBean(beanName, args);
        if (ret == null) {
            throw new RuntimeException("Spring容器里没有名字为" + beanName + "的bean");
        }
        return (T) ret;
    }

    /**
     * 根据类型获取实例
     *
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static void initClassPathXmlApplicationContext(
            String... configLocations) {
        applicationContext = new ClassPathXmlApplicationContext(configLocations);
    }

    public static void initFileSystemXmlApplicationContext(
            String... configLocations) {
        applicationContext = new FileSystemXmlApplicationContext(
                configLocations);
    }

    /**
     * 注意 bean name默认 = 类名(首字母小写)
     * 例如: ClusterDao = getBean("clusterDao")
     *
     * @param name
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    public static Class getType(String name) {
        return applicationContext.getType(name);
    }

    public static String[] getAliases(String name) {
        return applicationContext.getAliases(name);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class annotationType) {
        return applicationContext.getBeansWithAnnotation(annotationType);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取当前代理对象（必须配置expose-proxy="true"）
     * <aop:aspectj-autoproxy proxy-target-class="true"expose-proxy="true"/>
     * or
     * springBootApp  conifg @EnableAspectJAutoProxy(proxyTargetClass=true)  and dependcy aop jar
     *
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T getCurrentProxy() {
        return (T) AopContext.currentProxy();
    }
}
