package com.minelittlepony;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;

import java.lang.reflect.Type;

public class PonyDataSerialzier implements IMetadataSectionSerializer<IPonyData> {

    public static final String NAME = "pony";

    @Override
    public String getSectionName() {
        return NAME;
    }

    @Override
    public PonyData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, PonyData.class);
    }

}

