package com.batchat.thomas.batchat.lib;

import com.androidnetworking.interfaces.Parser;

/**
 * Created by thomas on 13/12/16.
 */

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by amitshekhar on 15/09/16.
 */
public final class JsonParserFactory extends Parser.Factory {

    private final ObjectMapper mapper;

    public JsonParserFactory() {
        this.mapper = new ObjectMapper();
    }

    public JsonParserFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Parser<ResponseBody, ?> responseBodyParser(Type type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectReader reader = mapper.readerFor(javaType);
        return new JacksonResponseBodyParser<>(reader);
    }

    @Override
    public Parser<?, RequestBody> requestBodyParser(Type type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mapper.writerFor(javaType);
        return new JacksonRequestBodyParser<>(writer);
    }

}
