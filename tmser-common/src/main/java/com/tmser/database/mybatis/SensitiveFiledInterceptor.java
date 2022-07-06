package com.tmser.database.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tmser.sensitive.SensitiveClass;
import com.tmser.sensitive.SensitiveMap;
import com.tmser.sensitive.SensitiveParam;
import com.tmser.sensitive.SensitiveProcessor;
import com.tmser.util.CollectionUtils;
import lombok.Setter;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 敏感词过滤，使用说明
 * <p>
 * 查询： 对查询参数标记为@SensitiveParam 的参数或标记为@SensitiveClass 的类型参数进行加密
 * 查询结果对类型注解了@SensitiveClass 或标记了@SensitiveMap 的Map 类型 进行解密
 * Wrapper 类型查询，只对其中Entity 做处理
 * 更新或插入：对更新参数进行加密参照查询参数加密， 更新后会对查询参数进行解密， 执行结果部处理。
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}
        )})
public class SensitiveFiledInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFiledInterceptor.class);

    @Setter
    @Resource
    private SensitiveProcessor sensitiveProcessor;

    protected void beforeUpdate(Executor executor, MappedStatement ms, Object arg) throws SQLException {
        //批量更新
        if (arg instanceof Map) {
            handleMapParameter((Map) arg, ms.getId(), false);
        } else if (arg instanceof List) {
            List list = (List) arg;
            for (Object o : list) {
                if (!isSensitiveType(o)) {//只处理sensitiveClass 注解对象类型
                    break;
                }
                sensitiveProcessor.encrypt(o);
            }
        } else if (arg != null && isSensitiveType(arg)) {
            sensitiveProcessor.encrypt(arg);
        }
    }

    protected void afterUpdate(Executor executor, MappedStatement ms, Object arg) throws SQLException {
        //批量更新
        if (arg instanceof Map) {
            handleMapParameter((Map) arg, ms.getId(), true);
        } else if (arg instanceof List) {
            List list = (List) arg;
            for (Object o : list) {
                if (!isSensitiveType(o)) {//只处理sensitiveClass 注解对象类型
                    break;
                }
                sensitiveProcessor.decrypt(o);
            }
        } else if (arg != null && isSensitiveType(arg)) {
            sensitiveProcessor.decrypt(arg);
        }
    }

    private void beforeQuery(Executor executor, MappedStatement ms, Object parameter) {
        String namespaceId = ms.getId();
        //参数类型为String 或者 List<String>
        if (parameter instanceof Map) {
            handleMapParameter((Map) parameter, namespaceId, false);
        } else {
            //bean
            handleBeanParameter(parameter);
        }
    }

    protected Object handleResult(Object result, String namespaceId) {
        if (isSensitiveType(result)) {
            return sensitiveProcessor.decrypt(result);
        }

        String[] keys = null;
        Method method = getMethod(namespaceId);
        if (Objects.nonNull(method)) {
            SensitiveMap annotation = method.getAnnotation(SensitiveMap.class);
            if (Objects.nonNull(annotation)) {
                keys = annotation.keys();
            }
        }

        //返回结果为list
        if (result instanceof List) {
            List list = (List) result;
            if (CollectionUtils.isEmpty(list)) {
                return result;
            }

            for (int i = 0; i < list.size(); i++) {
                Object e = list.get(i);
                if (e instanceof String) {
                    list.set(i, sensitiveProcessor.decrypt(e));
                } else if (e instanceof Map) {
                    if (Objects.isNull(keys) || keys.length == 0) {
                        break;
                    }
                    decryptMap((Map) e, keys);
                } else if (isSensitiveType(e)) {
                    sensitiveProcessor.decrypt(e);
                } else {
                    break;
                }

            }
        } else if (result instanceof Map) {
            decryptMap((Map) result, keys);
        }

        return result;
    }

    private void decryptMap(Map<Object, Object> map, String[] keys) {
        if (Objects.nonNull(keys) || keys.length == 0 || CollectionUtils.isEmpty(map)) {
            return;
        }
        Set<String> keySet = Sets.newHashSet(keys);
        for (Map.Entry e : map.entrySet()) {
            if (keySet.contains(e.getKey())) {
                map.put(e.getKey(), sensitiveProcessor.decrypt(e.getValue()));
            }
        }
    }

    public boolean isSensitiveType(Object object) {
        if (Objects.isNull(object)) {
            return false;
        }
        Class<?> objectClass = object.getClass();
        SensitiveClass encryptDecryptClass = AnnotationUtils.findAnnotation(objectClass, SensitiveClass.class);
        return Objects.nonNull(encryptDecryptClass);
    }


    private void handleBeanParameter(Object parameter) {
        sensitiveProcessor.encrypt(parameter);
    }


    private void handleMapParameter(Map<Object, Object> parameterMap, String namespaceId, boolean isDecrypt) {
        Method method = getMethod(namespaceId);
        if (method == null) {
            logger.info("can't found method for namespaceId: {}", namespaceId);
            return;
        }
        Parameter[] parameters = method.getParameters();
        int i = 0;
        for (Parameter parameter : parameters) {
            SensitiveParam annotation = parameter.getAnnotation(SensitiveParam.class);
            boolean isSensitiveParam = Objects.nonNull(annotation);
            String pname = getParameterName(parameter, i);
            Object value = parameterMap.get(pname);
            if (Objects.isNull(value)) {
                i++;
                continue;
            }
            if (isSensitiveParam && (value instanceof String || value instanceof Number)) {
                setNewValue(parameterMap, pname,
                        isDecrypt ? sensitiveProcessor.decrypt(value.toString()) : sensitiveProcessor.encrypt(value.toString()), i);
            } else if (isSensitiveParam && value instanceof java.util.List) {
                List<Object> list = new ArrayList<>();
                for (Object o : (List) value) {
                    list.add(isDecrypt ? sensitiveProcessor.decrypt(o) : sensitiveProcessor.encrypt(o));
                }
                setNewValue(parameterMap, pname, list, i);
            } else if (isSensitiveParam && value instanceof java.util.Map) {
                Map newValue = encryptMapParameter((Map<Object, Object>) value, annotation, isDecrypt);
                setNewValue(parameterMap, pname, newValue, i);
            } else if (value instanceof Wrapper) {
                sensitiveProcessor.encrypt(((Wrapper) value).getEntity());
            } else if (isSensitiveParam || (value != null && value.getClass().isAnnotationPresent(SensitiveClass.class))) {
                sensitiveProcessor.encrypt(value);
            }

            i++;
        }

    }

    private void setNewValue(Map<Object, Object> parameterMap, Object key, Object encrypt, int i) {
        parameterMap.put(key, encrypt);
        if (!key.equals(ParamNameResolver.GENERIC_NAME_PREFIX + (i + 1))) {
            parameterMap.put(ParamNameResolver.GENERIC_NAME_PREFIX + (i + 1), parameterMap.get(key));
        }
    }

    private Map<Object, Object> encryptMapParameter(Map<Object, Object> value, SensitiveParam annotation, boolean isDecrypt) {
        if (Objects.isNull(annotation) || Objects.isNull(value)) {
            return value;
        }
        String[] keys = annotation.keys();
        if (keys.length == 0) {
            return value;
        }
        Map<Object, Object> newValue = new HashMap<>();
        for (Map.Entry<Object, Object> o : value.entrySet()) {
            newValue.put(o.getKey(), isDecrypt ? sensitiveProcessor.decrypt(o.getValue()) : sensitiveProcessor.encrypt(o.getValue()));
        }
        return newValue;
    }

    private String getParameterName(Parameter parameter, int i) {
        final Param paramAnnotation = parameter.getAnnotation(Param.class);
        if (paramAnnotation != null) {
            return paramAnnotation.value();
        }
        return ParamNameResolver.GENERIC_NAME_PREFIX + (i + 1);
    }


    private static final ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>(16);

    private static Method getMethod(String namespaceId) {
        Method method = METHOD_CACHE.get(namespaceId);
        if (method == null) {
            String className = namespaceId.substring(0, namespaceId.lastIndexOf("."));
            String methodName = namespaceId.substring(namespaceId.lastIndexOf(".") + 1);
            try {
                Class clazz = Class.forName(className);
                Set<Method> methods = Sets.newHashSet(clazz.getMethods());
                methods.addAll(Lists.newArrayList(clazz.getDeclaredMethods()));
                for (Method tempMethod : methods) {
                    if (tempMethod.getName().equals(methodName)) {
                        METHOD_CACHE.put(namespaceId, tempMethod);
                        return tempMethod;
                    }
                }
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        return method;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (target instanceof Executor) {
            final Executor executor = (Executor) target;
            Object parameter = args[1];
            boolean isUpdate = args.length == 2;
            MappedStatement ms = (MappedStatement) args[0];
            if (!isUpdate && ms.getSqlCommandType() == SqlCommandType.SELECT) {
                beforeQuery(executor, ms, parameter);
                return handleResult(invocation.proceed(), ms.getId());
            }

            if (isUpdate) {
                beforeUpdate(executor, ms, parameter);
                Object proceed = invocation.proceed();
                afterUpdate(executor, ms, parameter);
                return proceed;
            }
        }
        return invocation.proceed();
    }
}
