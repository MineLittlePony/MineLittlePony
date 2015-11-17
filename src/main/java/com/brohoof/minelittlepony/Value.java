package com.brohoof.minelittlepony;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mumfrey.liteloader.core.LiteLoader;

public class Value<T> {

    private T value;

    public Value(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T val) {
        this.value = val;
        LiteLoader.getInstance().writeConfig(MineLittlePony.getConfig());
    }

    public static class Serializer implements JsonSerializer<Value<?>>, JsonDeserializer<Value<?>> {

        @Override
        public Value<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Type type = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
            return new Value<Object>(context.deserialize(json, type));
        }

        @Override
        public JsonElement serialize(Value<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.get());
        }
    }
}
