package com.jy.crypto.system.infrastructure.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//很多时候需要用到json，例如okhttp发送请求
//不同的服务提供者对json格式要求不同
//为了应对这种需求，不再使用Spring Bean的方式注入，而是提供不同的单例对象，并提供便捷的builder进行个性化定制。
public class JacksonHelper {
    public static final JacksonHelper CAMEL = new Builder().camelCase().ignoreNullFields().build();
    public static final JacksonHelper SNAKE = new Builder().snakeCase().ignoreNullFields().build();
    public static final JacksonHelper NULLABLE_CAMEL = new Builder().camelCase().build();
    public static final JacksonHelper NULLABLE_SNAKE = new Builder().snakeCase().build();

    private ObjectMapper mapper;

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public String writeJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readObject(String jsonStr, Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode readTree(String jsonStr) {
        try {
            return mapper.readTree(jsonStr);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public <T> T readObject(String jsonStr, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(jsonStr, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public <T> T treeToObject(JsonNode jsonNode,Class<T> clazz){
        try {
           return mapper.treeToValue(jsonNode, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {
        private final ObjectMapper mapper = new ObjectMapper();

        {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            mapper.registerModule(javaTimeModule);//系列化时间格式化
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }

        public Builder ignoreNullFields() {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return this;
        }

        public Builder camelCase() {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
            return this;
        }

        public Builder snakeCase() {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            return this;
        }

        public JacksonHelper build() {
            JacksonHelper helper = new JacksonHelper();
            helper.setMapper(mapper);
            return helper;
        }

    }

}
