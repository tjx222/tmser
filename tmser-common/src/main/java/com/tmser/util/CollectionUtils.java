package com.tmser.util;

import com.google.common.collect.Lists;
import com.tmser.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class CollectionUtils{

        private static final Logger log = LoggerFactory.getLogger(CollectionUtils.class);

        public static boolean isEmpty(Collection collection) {
            return collection == null || collection.isEmpty();
        }

        public static boolean isNotEmpty(Collection collection) {
            return !isEmpty(collection);
        }

        public static boolean isEmpty(Map map) {
            return map == null || map.isEmpty();
        }

        public static <T> boolean isEmpty(T[] array){
            return array !=null && array.length != 0;
        }
        public static <T> boolean isNotEmpty(T[] array){
            return !isEmpty(array);
        }
        public static boolean isNotEmpty(Map map) {
            return !isEmpty(map);
        }
        /*
          数据集合的分页方法，根据传入总共的数据跟页码，返回页码所需要显示多少条的数据
         @param f 带有需要进行分页的数据集合
         @param pageNo 第几页
         @param dataSize 显示多少条数据
         @return 进过分页之后返回的数据
         */
        public static <T> List<T> splitListByPage(List<T> list, int pageNo, int dataSize){
            //参数校验
            if(isEmpty(list)){
                return Lists.newArrayList();
            }
            if(pageNo <= 0){
                pageNo = 1;
            }
            if(dataSize == 0){
                dataSize = 1;
            }
            int totalitems = list.size();
            List<T> retList = new ArrayList<>();
            int begin = (pageNo-1)*dataSize;
            for(int i = begin; i < (begin + dataSize > totalitems ? totalitems:begin +dataSize); i++) {
                retList.add(list.get(i));
            }
            //将处理后的数据集合进行返回
            return retList;
        }
        /**
         * 由list查找数据，支持精确查询
         *
         * @param list
         * @param map
         *            存放属性与值
         * @return
         */
        public static <T> T searchObject(List<T> list,Map<String, Object> map) {
            if (list == null || list.isEmpty() || map == null || map.size() == 0)
                return null;
            boolean flag = true;
            for (T t : list) {
                for (Map.Entry<String,Object> entry:map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (StringUtils.isEmpty(value)) {
                        flag = false;
                        break;
                    }
                    try {
                        String value1 = StringUtils.toString(value);
                        Method pMethod = getReaderMethod(
                                t.getClass(), key);
                        String value2 = StringUtils.toString(pMethod.invoke(t));
                        if (StringUtils.isEmpty(value2) || !value1.equals(value2)) {
                            flag = false;
                            break;
                        }
                    } catch (InvocationTargetException e) {
                        log.warn("获取属性时出错:" , e);
                    } catch (IllegalArgumentException e) {
                        log.warn("获取属性时出错时出现错误参数:", e);
                    } catch (SecurityException e) {
                        log.warn("获取属性时出错:" , e);
                    } catch (IllegalAccessException e) {
                        log.warn("获取属性{}时出错:" ,key, e);
                    }
                }
                if (flag) {
                    return t;
                }
                flag = true;
            }
            return null;
        }

        /**
         * 由list查找数据，支持精确查询
         *
         * @param list
         * @param map
         *            存放属性的名称与值
         * @return
         */
        public static <T > List<T> searchList(List<T> list,Map<String, Object> map) {
            if (list == null || list.isEmpty() || map == null || map.size() == 0)
                return null;
            boolean flag = true;
            List<T> tList = new ArrayList<T>();
            for (T t : list) {
                for (Map.Entry<String,Object> entry:map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (StringUtils.isEmpty(value)) {
                        flag = false;
                        break;
                    }
                    try {
                        String value1 = StringUtils.toString(value);
                        Method pMethod = getReaderMethod(
                                t.getClass(), key);
                        String value2 = StringUtils.toString(pMethod.invoke(t));
                        if (StringUtils.isEmpty(value2) || !value1.equals(value2)) {
                            flag = false;
                            break;
                        }
                    } catch (InvocationTargetException e) {
                        log.warn("获取属性时出错:" , e);
                    } catch (IllegalArgumentException e) {
                        log.warn("获取属性时出错时出现错误参数:", e);
                    } catch (SecurityException e) {
                        log.warn("获取属性时出错:" , e);
                    } catch (IllegalAccessException e) {
                        log.warn("获取属性{}时出错:" ,key, e);
                    }
                }
                if (flag) {
                    tList.add(t);
                }
                flag = true;
            }
            return tList;
        }

        /**
         * 根据属性名获得JavaBean的getter方法名称(getter)
         *
         * @param property
         *            属性名
         * @return getter方法名
         */
        public static String getReaderMethodName(String property) {
            return "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
        }

        public static String getReaderIsMethodName(String property) {
            return "is" + property.substring(0, 1).toUpperCase() + property.substring(1);
        }

        /**
         * 获得Bean类型某个指定属性的getter方法。
         *
         * @param type
         *            JavaBean类型
         * @param property
         *            属性名
         * @return getter方法
         */
        public static Method getReaderMethod(Class<?> type, String property) {
            boolean flag = true;
            Method m = null;
            try {
                m = type.getMethod(getReaderMethodName(property), (Class[]) null);
            } catch (Exception e) {
                flag = false;
            }
            if (!flag) {
                try {
                    m = type.getMethod(getReaderIsMethodName(property), (Class[]) null);
                } catch (Exception e) {
                    throw new BaseException("Error to getReader :" + property);
                }
            }
            return m;
        }
}
