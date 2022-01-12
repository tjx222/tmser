package com.tmser.database.mybatis;

import com.tmser.sensitive.SensitiveClass;
import com.tmser.sensitive.SensitiveParam;
import com.tmser.sensitive.SensitiveProcessor;
import lombok.Setter;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
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

@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}
        )})
public class SensitiveFiledInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFiledInterceptor.class);

    @Setter
    @Resource
    private SensitiveProcessor sensitiveProcessor;

    public void beforeUpdate(Executor executor, MappedStatement ms, Object arg) throws SQLException {
        //批量更新
        if (arg instanceof Map) {
            Map map = (Map) arg;
            List list = (List) map.get("list");
            for (Object o : list) {
                sensitiveProcessor.encrypt(o);
            }
        }

        if (arg instanceof List) {
            List list = (List) arg;
            for (Object o : list) {
                sensitiveProcessor.encrypt(o);
            }
        }
        //model下的bean
        if (arg != null && isSensitiveType(arg)) {
            sensitiveProcessor.encrypt(arg);
        }
    }

    private void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        String namespaceId = ms.getId();
        //参数类型为String 或者 List<String>
        if (parameter instanceof HashMap) {
            handleMapParameter((HashMap) parameter, namespaceId);
        } else {
            //bean
            handleBeanParameter(parameter);
        }
    }

    protected Object handleResult(Object result) {
        if (isSensitiveType(result)) {
            return sensitiveProcessor.decrypt(result);
        }

        //返回结果为list
        if (result instanceof List) {
            List list = (List) result;
            list.forEach(sensitiveProcessor::decrypt);
        }

        return result;
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


    private void handleMapParameter(Map<Object, Object> parameterMap, String namespaceId) {
        Method method = getMethod(namespaceId);
        if (method == null) {
            return;
        }
        Parameter[] parameters = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        int i = 0;
        for (Parameter parameter : parameters) {
            Class<?> pType = parameterTypes[i];
            if (parameter.isAnnotationPresent(SensitiveParam.class)) {
                SensitiveParam annotation = parameter.getAnnotation(SensitiveParam.class);
                String pname = getParameterName(parameter, i);
                for (Iterator iterator = parameterMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<Object, Object> entry = (Map.Entry) iterator.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (pname.equals(key)) {
                        if (value instanceof String || value instanceof Number) {
                            setNewValue(parameterMap, key, sensitiveProcessor.encrypt(value.toString()), i);
                        } else if (value instanceof java.util.List) {
                            List<Object> list = new ArrayList<>();
                            for (Object o : (List) value) {
                                list.add(sensitiveProcessor.encrypt(o));
                            }
                            setNewValue(parameterMap, list, sensitiveProcessor.encrypt(value.toString()), i);
                        } else if (value instanceof java.util.Map) {
                            Map newValue = encryptMapParameter((Map<Object, Object>) value, annotation);
                            setNewValue(parameterMap, newValue, sensitiveProcessor.encrypt(value.toString()), i);
                        }
                    }
                }
            } else if (pType.isAnnotationPresent(SensitiveClass.class)) {
                String pname = getParameterName(parameter, i);
                for (Iterator iterator = parameterMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<Object, Object> entry = (Map.Entry) iterator.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (pname.equals(key)) {
                        sensitiveProcessor.encrypt(value);
                    }
                }
            }
            i++;
        }

    }

    private void setNewValue(Map<Object, Object> parameterMap, Object key, String encrypt, int i) {
        parameterMap.put(key, encrypt);
        if (!key.equals("param" + (i + 1))) {
            parameterMap.put("param" + (i + 1), parameterMap.get(key));
        }
    }

    private Map encryptMapParameter(Map<Object, Object> value, SensitiveParam annotation) {
        if (Objects.isNull(annotation) || Objects.isNull(value)) {
            return value;
        }
        String[] keys = annotation.keys();
        if (keys.length == 0) {
            return value;
        }
        Map<Object, Object> newValue = new HashMap<>();
        for (Map.Entry<Object, Object> o : value.entrySet()) {
            newValue.put(o.getKey(), sensitiveProcessor.encrypt(o.getValue()));
        }
        return newValue;
    }

    private String getParameterName(Parameter parameter, int i) {
        final Param paramAnnotation = parameter.getAnnotation(Param.class);
        if (paramAnnotation != null) {
            return paramAnnotation.value();
        }
        return "param" + (i + 1);
    }


    private static final ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>(16);

    public static Method getMethod(String namespaceId) {
        Method method = METHOD_CACHE.get(namespaceId);
        if (method == null) {
            String className = namespaceId.substring(0, namespaceId.lastIndexOf("."));
            String methodName = namespaceId.substring(namespaceId.lastIndexOf(".") + 1);
            try {
                Class clazz = Class.forName(className);
                Method[] methods = clazz.getDeclaredMethods();
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
                RowBounds rowBounds = (RowBounds) args[2];
                ResultHandler resultHandler = (ResultHandler) args[3];
                BoundSql boundSql;
                if (args.length == 4) {
                    boundSql = ms.getBoundSql(parameter);
                } else {
                    // 几乎不可能走进这里面,除非使用Executor的代理对象调用query[args[6]]
                    boundSql = (BoundSql) args[5];
                }

                beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
                return handleResult(invocation.proceed());
            }

            if (isUpdate) {
                beforeUpdate(executor, ms, parameter);
            }
        }
        return invocation.proceed();
    }
}
