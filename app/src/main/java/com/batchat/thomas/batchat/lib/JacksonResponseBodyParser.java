package com.batchat.thomas.batchat.lib;

import com.androidnetworking.interfaces.Parser;
import com.fasterxml.jackson.databind.ObjectReader;

import okhttp3.ResponseBody;

/**
 * Created by thomas on 13/12/16.
 */
import com.androidnetworking.interfaces.Parser;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by amitshekhar on 15/09/16.
 */
final class JacksonResponseBodyParser<T> implements Parser<ResponseBody, T> {

    private final ObjectReader adapter;

    JacksonResponseBodyParser(ObjectReader adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return adapter.readValue(value.charStream());
        } finally {
            value.close();
        }
    }

}
