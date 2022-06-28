package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.*;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

public class JpaTableInfoHelper {

    private static final Log logger = LogFactory.getLog(JpaTableInfoHelper.class);

    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认表主键名称
     */
    private static final String DEFAULT_ID_NAME = "id";

    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public synchronized static TableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        //1. 本地缓存存在
        TableInfo targetTableInfo = TABLE_INFO_CACHE.get(clazz);

        final Configuration configuration = builderAssistant.getConfiguration();
        if (targetTableInfo != null) {
            Configuration oldConfiguration = targetTableInfo.getConfiguration();
            if (!oldConfiguration.equals(configuration)) {
                // 不是同一个 Configuration,进行重新初始化
                targetTableInfo = initTableInfoAct(builderAssistant, clazz);
            }
            return targetTableInfo;
        }
        return initTableInfoAct(builderAssistant, clazz);
    }

    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    private synchronized static TableInfo initTableInfoAct(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        /* 没有获取到缓存信息,则初始化
         *  先由TableInfoHelper 进行解析
         */
        TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, clazz);

        //只关注 Entity 注解实体
        if (!clazz.isAnnotationPresent(Entity.class)) {
            return tableInfo;
        }

        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(builderAssistant.getConfiguration());

        /* 初始化表名相关 */
        final String[] excludeProperty = initTableName(clazz, globalConfig, tableInfo);

        List<String> excludePropertyList = excludeProperty != null && excludeProperty.length > 0 ? Arrays.asList(excludeProperty) : Collections.emptyList();

        /* 初始化字段相关 */
        initTableFields(clazz, globalConfig, tableInfo, excludePropertyList);

        /* 自动构建 resultMap */
        tableInfo.initResultMapIfNeed();

        TABLE_INFO_CACHE.put(clazz, tableInfo);
        /* 缓存 lambda */
        LambdaUtils.installCache(tableInfo);
        return tableInfo;
    }

    /**
     * <p>
     * 初始化 表数据库类型,表名,resultMap
     * </p>
     *
     * @param clazz        实体类
     * @param globalConfig 全局配置
     * @param tableInfo    数据库表反射信息
     * @return 需要排除的字段名
     */
    private static String[] initTableName(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo) {
        /* 数据库全局配置 */
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        Table table = clazz.getAnnotation(Table.class);
        String tableName = clazz.getSimpleName();
        String tablePrefix = dbConfig.getTablePrefix();
        String schema = dbConfig.getSchema();
        boolean tablePrefixEffect = true;
        String[] excludeProperty = null;

        if (table != null) {
            if (StringUtils.isNotBlank(table.name())) {
                tableName = table.name();
                if (StringUtils.isNotBlank(tablePrefix)) {
                    tablePrefixEffect = false;
                }
            } else {
                tableName = initTableNameWithDbConfig(tableName, dbConfig);
            }
            if (StringUtils.isNotBlank(table.schema())) {
                schema = table.schema();
            }
        } else {
            tableName = initTableNameWithDbConfig(tableName, dbConfig);
        }

        String targetTableName = tableName;
        if (StringUtils.isNotBlank(tablePrefix) && tablePrefixEffect) {
            targetTableName = tablePrefix + targetTableName;
        }
        if (StringUtils.isNotBlank(schema)) {
            targetTableName = schema + StringPool.DOT + targetTableName;
        }

        // System.out.println(tableInfo.getClass().getClassLoader() + " -:- " + Thread.currentThread().getContextClassLoader());
        tableInfo.setTableName(targetTableName);

        /* 开启了自定义 KEY 生成器 */
        if (CollectionUtils.isNotEmpty(dbConfig.getKeyGenerators())) {
            tableInfo.setKeySequence(clazz.getAnnotation(KeySequence.class));
        }
        return excludeProperty;
    }

    /**
     * 根据 DbConfig 初始化 表名
     *
     * @param className 类名
     * @param dbConfig  DbConfig
     * @return 表名
     */
    private static String initTableNameWithDbConfig(String className, GlobalConfig.DbConfig dbConfig) {
        String tableName = className;
        // 开启表名下划线申明
        if (dbConfig.isTableUnderline()) {
            tableName = StringUtils.camelToUnderline(tableName);
        }
        // 大写命名判断
        if (dbConfig.isCapitalMode()) {
            tableName = tableName.toUpperCase();
        } else {
            // 首字母小写
            tableName = StringUtils.firstToLowerCase(tableName);
        }
        return tableName;
    }

    /**
     * <p>
     * 初始化 表主键,表字段
     * </p>
     *
     * @param clazz        实体类
     * @param globalConfig 全局配置
     * @param tableInfo    数据库表反射信息
     */
    private static void initTableFields(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo, List<String> excludeProperty) {
        /* 数据库全局配置 */
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        ReflectorFactory reflectorFactory = tableInfo.getConfiguration().getReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(clazz);
        List<Field> list = getAllFields(clazz);
        // 标记是否读取到主键
        boolean isReadPK = false;
        // 是否存在 @TableId 注解
        boolean existTableId = isExistTableId(list);
        // 是否存在 @TableLogic 注解
        boolean existTableLogic = isExistTableLogic(list);
        // 是否存在 @OrderBy 注解
        boolean existOrderBy = isExistOrderBy(list);

        List<TableFieldInfo> fieldList = new ArrayList<>(list.size());
        for (Field field : list) {
            if (excludeProperty.contains(field.getName())) {
                continue;
            }

            /* 主键ID 初始化 */
            if (existTableId) {
                Id tableId = field.getAnnotation(Id.class);
                if (tableId != null) {
                    if (isReadPK) {
                        throw ExceptionUtils.mpe("@TableId can't more than one in Class: \"%s\".", clazz.getName());
                    } else {
                        initTableIdWithAnnotation(dbConfig, tableInfo, field, tableId, reflector);
                        isReadPK = true;
                        continue;
                    }
                }
            } else if (!isReadPK) {
                isReadPK = initTableIdWithoutAnnotation(dbConfig, tableInfo, field, reflector);
                if (isReadPK) {
                    continue;
                }
            }
            final Column cm = field.getAnnotation(Column.class);
            TableField tableField = null;
            if (cm != null) {
                tableField = new TableField() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return TableField.class;
                    }

                    @Override
                    public String value() {
                        return cm.name();
                    }

                    @Override
                    public boolean exist() {
                        return true;
                    }

                    @Override
                    public String condition() {
                        return "";
                    }

                    @Override
                    public String update() {
                        return "";
                    }

                    @Override
                    public FieldStrategy insertStrategy() {
                        return FieldStrategy.DEFAULT;
                    }

                    @Override
                    public FieldStrategy updateStrategy() {
                        return FieldStrategy.DEFAULT;
                    }

                    @Override
                    public FieldStrategy whereStrategy() {
                        return FieldStrategy.DEFAULT;
                    }

                    @Override
                    public FieldFill fill() {
                        return FieldFill.DEFAULT;
                    }

                    @Override
                    public boolean select() {
                        return true;
                    }

                    @Override
                    public boolean keepGlobalFormat() {
                        return false;
                    }

                    @Override
                    public JdbcType jdbcType() {
                        return JdbcType.UNDEFINED;
                    }

                    @Override
                    public Class<? extends TypeHandler> typeHandler() {
                        return UnknownTypeHandler.class;
                    }

                    @Override
                    public boolean javaType() {
                        return false;
                    }

                    @Override
                    public String numericScale() {
                        return "";
                    }
                };
            }

            /* 有 @TableField 注解的字段初始化 */
            if (tableField != null) {
                fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field, tableField, reflector, existTableLogic, existOrderBy));
                continue;
            }

            /* 无 @TableField  注解的字段初始化 */
            fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field, reflector, existTableLogic, existOrderBy));
        }

        /* 字段列表 */
        tableInfo.setFieldList(fieldList);

        /* 未发现主键注解，提示警告信息 */
        if (!isReadPK) {
            logger.warn(String.format("Can not find table primary key in Class: \"%s\".", clazz.getName()));
        }
    }

    /**
     * <p>
     * 判断主键注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return true 为存在 @TableId 注解;
     */
    public static boolean isExistTableId(List<Field> list) {
        return list.stream().anyMatch(field -> field.isAnnotationPresent(Id.class));
    }

    /**
     * <p>
     * 判断逻辑删除注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return true 为存在 @TableId 注解;
     */
    public static boolean isExistTableLogic(List<Field> list) {
        return list.stream().anyMatch(field -> field.isAnnotationPresent(TableLogic.class));
    }

    /**
     * <p>
     * 判断排序注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return true 为存在 @TableId 注解;
     */
    public static boolean isExistOrderBy(List<Field> list) {
        return list.stream().anyMatch(field -> field.isAnnotationPresent(OrderBy.class));
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param dbConfig  全局配置信息
     * @param tableInfo 表信息
     * @param field     字段
     * @param tableId   注解
     * @param reflector Reflector
     */
    private static void initTableIdWithAnnotation(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo,
                                                  Field field, Id tableId, Reflector reflector) {
        boolean underCamel = tableInfo.isUnderCamel();
        final String property = field.getName();
        if (field.getAnnotation(TableField.class) != null) {
            logger.warn(String.format("This \"%s\" is the table primary key by @Id annotation in Class: \"%s\",So @TableField annotation will not work!",
                    property, tableInfo.getEntityType().getName()));
        }

        GeneratedValue gv = field.getAnnotation(GeneratedValue.class);
        /* 主键策略（ 注解 > 全局 ） */
        // 设置 Sequence 其他策略无效
        if (gv == null || GenerationType.AUTO.equals(gv.strategy())) {
            tableInfo.setIdType(dbConfig.getIdType());
        } else {
            switch (gv.strategy()) {
                case IDENTITY:
                    tableInfo.setIdType(IdType.AUTO);
                    break;
                case SEQUENCE:
                    tableInfo.setIdType(IdType.ASSIGN_ID);
                    break;
                case TABLE:
                    tableInfo.setIdType(IdType.INPUT);
                    break;
            }
        }

        /* 字段 */
        String column = property;
        Column cm = field.getAnnotation(Column.class);
        if (cm != null && StringUtils.isNotBlank(cm.name())) {
            column = cm.name();
        } else {
            // 开启字段下划线申明
            if (underCamel) {
                column = StringUtils.camelToUnderline(column);
            }
            // 全局大写命名
            if (dbConfig.isCapitalMode()) {
                column = column.toUpperCase();
            }
        }
        final Class<?> keyType = reflector.getGetterType(property);
        if (keyType.isPrimitive()) {
            logger.warn(String.format("This primary key of \"%s\" is primitive !不建议如此请使用包装类 in Class: \"%s\"",
                    property, tableInfo.getEntityType().getName()));
        }
        tableInfo.setKeyRelated(checkRelated(underCamel, property, column))
                .setKeyColumn(column)
                .setKeyProperty(property)
                .setKeyType(keyType);
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param tableInfo 表信息
     * @param field     字段
     * @param reflector Reflector
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableIdWithoutAnnotation(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo,
                                                        Field field, Reflector reflector) {
        final String property = field.getName();
        if (DEFAULT_ID_NAME.equalsIgnoreCase(property)) {
            if (field.getAnnotation(TableField.class) != null) {
                logger.warn(String.format("This \"%s\" is the table primary key by default name for `id` in Class: \"%s\",So @TableField will not work!",
                        property, tableInfo.getEntityType().getName()));
            }
            String column = property;
            if (dbConfig.isCapitalMode()) {
                column = column.toUpperCase();
            }
            final Class<?> keyType = reflector.getGetterType(property);
            if (keyType.isPrimitive()) {
                logger.warn(String.format("This primary key of \"%s\" is primitive !不建议如此请使用包装类 in Class: \"%s\"",
                        property, tableInfo.getEntityType().getName()));
            }
            tableInfo.setKeyRelated(checkRelated(tableInfo.isUnderCamel(), property, column))
                    .setIdType(dbConfig.getIdType())
                    .setKeyColumn(column)
                    .setKeyProperty(property)
                    .setKeyType(keyType);
            return true;
        }
        return false;
    }

    /**
     * 判定 related 的值
     * <p>
     * 为 true 表示不符合规则
     *
     * @param underCamel 驼峰命名
     * @param property   属性名
     * @param column     字段名
     * @return related
     */
    public static boolean checkRelated(boolean underCamel, String property, String column) {
        column = StringUtils.getTargetColumn(column);
        String propertyUpper = property.toUpperCase(Locale.ENGLISH);
        String columnUpper = column.toUpperCase(Locale.ENGLISH);
        if (underCamel) {
            // 开启了驼峰并且 column 包含下划线
            return !(propertyUpper.equals(columnUpper) ||
                    propertyUpper.equals(columnUpper.replace(StringPool.UNDERSCORE, StringPool.EMPTY)));
        } else {
            // 未开启驼峰,直接判断 property 是否与 column 相同(全大写)
            return !propertyUpper.equals(columnUpper);
        }
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     * @return 属性集合
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
        return fieldList.stream()
                .filter(field -> {
                    /* 过滤注解非表字段属性 */
                    Transient tableField = field.getAnnotation(Transient.class);
                    return (tableField == null);
                }).collect(toList());
    }

}
