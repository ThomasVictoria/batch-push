package com.batchat.thomas.batchat.lib;

import com.androidnetworking.interfaces.Parser;
import com.fasterxml.jackson.databind.ObjectWriter;

import okhttp3.RequestBody;

/**
 * Created by thomas on 13/12/16.
 */

import com.androidnetworking.interfaces.Parser;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by amitshekhar on 15/09/16.
 */
final class JacksonRequestBodyParser<T> implements Parser<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private final ObjectWriter adapter;

    JacksonRequestBodyParser(ObjectWriter adapter) {
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        byte[] bytes = adapter.writeValueAsBytes(value);
        return RequestBody.create(MEDIA_TYPE, bytes);
    }
}