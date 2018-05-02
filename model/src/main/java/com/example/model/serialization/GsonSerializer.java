package com.example.model.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;

public class GsonSerializer implements JsonSerializer {
    private Gson gson;

    public GsonSerializer() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Double.class, (com.google.gson.JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    if (src == src.longValue()) //предотвращаем преобразование целочисленных значений как double
                        return new JsonPrimitive(src.longValue());
                    return new JsonPrimitive(src);
                })
                .create();

    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        if(json == null) {
            return null;
        }
        return gson.fromJson(json, classOfT);
    }

    @Override
    public String toJson(Object value) {
        if(value == null) {
            return null;
        }
        return gson.toJson(value);
    }
}
