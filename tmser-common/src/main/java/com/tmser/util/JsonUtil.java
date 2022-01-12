package com.tmser.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Maps;
import com.tmser.model.money.Money;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper DEFAULT_MAPPER = getDefaultMapper();

    public static ObjectMapper configure(ObjectMapper mapper) {
        return configure(mapper, false);
    }

    public static ObjectMapper configure(ObjectMapper mapper, boolean withNull) {
        if (!withNull) {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);//属性为NULL 不序列化
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            //mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, false);
        }
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        //输出非null对象
        //mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);

        //设置时间格式
        mapper.setDateFormat(new SimpleDateFormat(DateUtils.YMDHMS));
        return mapper;
    }

    static {
        configure(DEFAULT_MAPPER);
        DEFAULT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        //输出非null对象
        //mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);

        //设置时间格式
        DEFAULT_MAPPER.setDateFormat(new SimpleDateFormat(DateUtils.YMDHMS));

  		/*//空值处理为空串
        mapper.getSerializerProvider().setNullValueSerializer(
				new JsonSerializer<Object>() {
					@Override
					public void serialize(Object value, JsonGenerator jg,
							SerializerProvider sp) throws IOException
							 {
						jg.writeString("");
					}
				});*/

		/*// 允许单引号
          mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
  		//字段和值都加引号
  		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
  		//数字也加引号
  		mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
  		mapper.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
  		*/
    }

    public static ObjectMapper registerMoneyModule(ObjectMapper mapper) {
        Module moneySerializerModule = new SimpleModule("MoneySerializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addSerializer(Money.class, new MoneySerializer());
        mapper.registerModule(moneySerializerModule);

        Module moneyDeserializerModule = new SimpleModule("MoneyDeserializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addDeserializer(Money.class, new MoneyDeserializer());
        mapper.registerModule(moneyDeserializerModule);
        return mapper;
    }

    public static ObjectMapper registerLocalDateModule(ObjectMapper mapper) {
        Module localDateSerializerModule = new SimpleModule("LocalDateSerializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addSerializer(LocalDate.class, new LocalDateSerializer());
        mapper.registerModule(localDateSerializerModule);

        Module localDateDeserializerModule = new SimpleModule("LocalDateDeserializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addDeserializer(LocalDate.class, new LocalDateDeserialize());
        mapper.registerModule(localDateDeserializerModule);
        return mapper;
    }

    public static ObjectMapper registerLocalTimeModule(ObjectMapper mapper) {
        Module localTimeSerializerModule = new SimpleModule("LocalTimeSerializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addSerializer(LocalTime.class, new LocalTimeSerializer());
        mapper.registerModule(localTimeSerializerModule);

        Module localTimeDeserializerModule = new SimpleModule("LocalTimeDeserializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addDeserializer(LocalTime.class, new LocalTimeDeserialize());
        mapper.registerModule(localTimeDeserializerModule);
        return mapper;
    }

    public static ObjectMapper registerLocalDateTimeModule(ObjectMapper mapper) {
        Module localDateTimeSerializerModule = new SimpleModule("LocalDateTimeSerializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        mapper.registerModule(localDateTimeSerializerModule);


        Module localDateTimeDeserializerModule = new SimpleModule("LocalDateTimeDeserializerModule",
                new Version(1, 0, 0, null, "com.tmser", "tmser-core"))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserialize());
        mapper.registerModule(localDateTimeDeserializerModule);
        return mapper;
    }

    /**
     * 带有localDate 、 money 的转换
     *
     * @return
     */
    public static ObjectMapper getDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configure(mapper);
        registerModule(mapper);
        return mapper;
    }

    public static void registerModule(ObjectMapper mapper) {
        registerMoneyModule(mapper);
        registerLocalDateModule(mapper);
        registerLocalTimeModule(mapper);
        registerLocalDateTimeModule(mapper);
    }

    public JsonUtil filter(String filterName, String... properties) {
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filterName,
                SimpleBeanPropertyFilter.serializeAllExcept(properties));

        DEFAULT_MAPPER.setFilterProvider(filterProvider);
        return this;
    }

    public static String toJson(Object o) {
        return toJson(DEFAULT_MAPPER, o);
    }

    public static String toJson(ObjectMapper mapper, Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            // mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("{}转换为JSON格式出错", obj.toString(), e);
        }
        return null;
    }

    public static String toJson(Object o, String exclude) {
        return toJson(DEFAULT_MAPPER, o, exclude);
    }

    public static String toJson(ObjectMapper mapper, Object o, String exclude) {
        ObjectNode jsonNode = (ObjectNode) mapper.valueToTree(o);
        jsonNode.remove(exclude);
        return jsonNode.toString();
    }

    public static Map<String, Object> toMap(String jsonString) {
        return (Map) fromJson(jsonString, Map.class);
    }

    public static Map<String, Object> toMap(ObjectMapper mapper, String jsonString) {
        return (Map) fromJson(mapper, jsonString, Map.class);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return fromJson(DEFAULT_MAPPER, jsonString, clazz);
    }

    public static <T> T fromJson(String jsonString, JavaType javaType) {
        return fromJson(DEFAULT_MAPPER, jsonString, javaType);
    }

    public static <T> T readJsonByKey(String jsonString, String property) {
        JsonNode jsonNode = null;
        try {
            jsonNode = DEFAULT_MAPPER.readTree(jsonString);
            Iterator<Map.Entry<String, JsonNode>> elements = jsonNode.fields();
//            if(clazz instanceof Collection){
//                JavaType javaType = getCollectionType(ArrayList.class, clazz);
//                List<T> list = (List<T>) DEFAULT_MAPPER.readValue(value, javaType);
//                    ARRAY,
//                    BINARY,
//                    BOOLEAN,
//                    MISSING,
//                    NULL,
//                    NUMBER,
//                    OBJECT,
//                    POJO,
//                    STRING

//            }
            while (elements.hasNext()) {
                Map.Entry<String, JsonNode> element = elements.next();
                String key = element.getKey();
                if (key.equals(property)) {
                    JsonNode node = element.getValue();
                    JsonNodeType jsonNodeType = node.getNodeType();
                    switch (jsonNodeType) {
                        case ARRAY:
                            return (T) DEFAULT_MAPPER.readValue(node.toString(), List.class);
                        case POJO:
                            return (T) DEFAULT_MAPPER.readValue(node.toString(), Map.class);
                        case OBJECT:
                            return (T) DEFAULT_MAPPER.readValue(node.toString(), Map.class);
                        case STRING:
                            return (T) DEFAULT_MAPPER.readValue(node.toString(), String.class);
                        case NULL:
                            return null;
                        case NUMBER:
                            return (T) DEFAULT_MAPPER.readValue(node.toString(), Number.class);
                        case BOOLEAN:
                            return (T) DEFAULT_MAPPER.readValue(node.toString(), Boolean.class);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("readFromJson  string error:{}, ex:{}", jsonString, ExceptionUtils.getStackTrace(e));
            return null;
        }
        return null;
    }

    public static Map<String, String> readJson(String jsonString) {
        JsonNode jsonNode = null;
        Map<String, String> map = Maps.newHashMap();
        try {
            jsonNode = DEFAULT_MAPPER.readTree(jsonString);
            Iterator<Map.Entry<String, JsonNode>> elements = jsonNode.fields();
            while (elements.hasNext()) {
                Map.Entry<String, JsonNode> element = elements.next();
                String key = element.getKey();
                JsonNode node = element.getValue();
                JsonNodeType jsonNodeType = node.getNodeType();
                String value = null;
                switch (jsonNodeType) {
                    case ARRAY:
                        value = StringUtils.toString(DEFAULT_MAPPER.readValue(node.toString(), List.class));
                        break;
                    case POJO:
                        value = StringUtils.toString(DEFAULT_MAPPER.readValue(node.toString(), Map.class));
                        break;
                    case OBJECT:
                        value = StringUtils.toString(DEFAULT_MAPPER.readValue(node.toString(), Map.class));
                        break;
                    case STRING:
                        value = StringUtils.toString(DEFAULT_MAPPER.readValue(node.toString(), String.class));
                        break;
                    case NULL:
                        return null;
                    case NUMBER:
                        value = StringUtils.toString(DEFAULT_MAPPER.readValue(node.toString(), Number.class));
                        break;
                    case BOOLEAN:
                        value = StringUtils.toString(DEFAULT_MAPPER.readValue(node.toString(), Boolean.class));
                        break;
                }
                map.put(key, value);
            }
        } catch (Exception e) {
            logger.error("readFromJson  string error:{}, ex:{}", jsonString, ExceptionUtils.getStackTrace(e));
            return null;
        }
        return map;
    }

    public static <T> T fromJson(ObjectMapper mapper, String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else {
            try {
                return mapper.readValue(jsonString, javaType);
            } catch (IOException var4) {
                logger.error("parse json string error:{}, ex:{}", jsonString, ExceptionUtils.getStackTrace(var4));
                return null;
            }
        }
    }

    public static <T> T fromJson(Reader reader, JavaType javaType) {
        return fromJson(DEFAULT_MAPPER, reader, javaType);
    }

    public static <T> T fromJson(ObjectMapper mapper, Reader reader, JavaType javaType) {
        if (null == reader) {
            return null;
        } else {
            try {
                return mapper.readValue(reader, javaType);
            } catch (IOException var4) {
                logger.error("parse json, ex:{}", ExceptionUtils.getStackTrace(var4));
                return null;
            }
        }
    }

    public static <T> T fromJson(ObjectMapper mapper, String value, Class<T> clazz) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            //DEFAULT_MAPPER.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
            return mapper.readValue(value, clazz);
        } catch (Exception e) {
            logger.error("{}转换为JOSN格式出错", value.toString(), e);
        }
        return null;
    }

    public static List<Object> toList(String jsonString) {
        return (List) fromJson(jsonString, List.class);
    }

    public static List<Object> toList(ObjectMapper mapper, String jsonString) {
        return (List) fromJson(mapper, jsonString, List.class);
    }

    public static <T> List<T> toListObject(String value, Class<?>... clazz) {

        if (StringUtils.isEmpty(value)
                || "{}".equalsIgnoreCase(value)
                || "[]".equalsIgnoreCase(value)
                || "[{}]".equalsIgnoreCase(value)) {
            return new ArrayList<>();
        }
        try {
            JavaType javaType = getCollectionType(ArrayList.class, clazz);
            List<T> list = (List<T>) DEFAULT_MAPPER.readValue(value, javaType);
            return list;
        } catch (Exception e) {
            logger.error("{}转换为JOSN格式出错", value.toString(), e);
        }
        return null;
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return DEFAULT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = configure(objectMapper);
        return objectMapper;
    }

    public static ObjectMapper newObjectMapperWithClazz() {
        ObjectMapper mapper = new ObjectMapper();
        mapper = configure(mapper);
        registerModule(mapper);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    public static ObjectMapper newObjectMapperWithNullValue() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = configure(objectMapper, true);
        return objectMapper;
    }

    public static ObjectMapper newObjectMapperWithEmptyValue() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = configure(objectMapper, true);
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer() {
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeString("");
            }
        });
        return objectMapper;
    }


    public static class MoneyDeserializer extends JsonDeserializer<Money> {
        @Override
        public Money deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            if (StringUtils.isEmpty(jp.getText())) {
                return new Money();
            }
            return new Money(jp.getText());
        }

    }

    public static class MoneySerializer extends JsonSerializer<Money> {

        @Override
        public void serialize(Money value, JsonGenerator jgen,
                              SerializerProvider provider) throws IOException {
            // value.roundByJiao();//保留到角 @xinya
            jgen.writeNumber(value.getAmount());
        }
    }

    /**
     * <p>Title:</p>
     * <p>Description: 时间转换long</p>
     * Modified History: 支持以下2种方式的转换，其它抛异常再改
     */
    public static class LocalDateDeserialize extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            String value = jp.getValueAsString();
            if (StringUtils.isNotEmpty(value) && value.contains("-")) {
                return LocalDate.parse(value);
            }
            LocalDate date = null;
            try {
                date = DateUtils.long2DateTime(Long.parseLong(jp.getText())).toLocalDate();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return date;
        }
    }

    private static LocalTime localTime = LocalTime.of(0, 0, 0);

    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator jgen,
                              SerializerProvider provider) throws IOException {
            jgen.writeNumber(DateUtils.dateTime2Long(LocalDateTime.of(value, localTime)));
        }
    }

    public static class LocalDateTimeDeserialize extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            String value = jp.getValueAsString();
            if (StringUtils.isNotEmpty(value) && value.contains("T")) {
                return LocalDateTime.parse(value);
            }
            if (StringUtils.isNotEmpty(value) && (value.contains("-") || value.contains(":"))) {
                return DateUtils.string2DateTime(value);
            }
            LocalDateTime date = null;
            try {
                date = DateUtils.long2DateTime(jp.getLongValue());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            return date;
        }
    }

    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator jgen,
                              SerializerProvider provider) throws IOException {
            jgen.writeNumber(DateUtils.dateTime2Long(value));
        }
    }

    public static class LocalTimeDeserialize extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            String value = jp.getValueAsString();
            if (StringUtils.isNotEmpty(value) && (value.contains(":"))) {
                return LocalTime.parse(value);
            }
            LocalTime time = null;
            try {
                time = LocalTime.ofSecondOfDay(jp.getLongValue());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return time;
        }
    }

    public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {
        @Override
        public void serialize(LocalTime value, JsonGenerator jgen,
                              SerializerProvider provider) throws IOException {
            jgen.writeNumber(value.toSecondOfDay());
        }
    }

}