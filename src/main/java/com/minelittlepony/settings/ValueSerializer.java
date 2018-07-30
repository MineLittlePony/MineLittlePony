package com.minelittlepony.settings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ValueSerializer implements JsonSerializer<Value<?>>, JsonDeserializer<Value<?>> {

    @Override
    public Value<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ParameterizedType type = (ParameterizedType) typeOfT;
        return Value.of(context.deserialize(json, type.getActualTypeArguments()[0]));
    }

    @Override
    public JsonElement serialize(Value<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.get());
    }
}
