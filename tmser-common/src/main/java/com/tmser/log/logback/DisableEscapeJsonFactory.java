package com.tmser.log.logback;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.decorate.JsonFactoryDecorator;

public class DisableEscapeJsonFactory implements JsonFactoryDecorator {

    public MappingJsonFactory decorate(MappingJsonFactory factory) {
        // 禁用对非ascii码进行escape编码的特性
        factory.disable(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature());
        return factory;
    }

}
