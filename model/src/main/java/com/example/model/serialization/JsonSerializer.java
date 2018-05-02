package com.example.model.serialization;

public interface JsonSerializer {
    <T> T fromJson(String json, Class<T> classOfT);
    String toJson(Object value);
}

