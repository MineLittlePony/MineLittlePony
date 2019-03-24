package com.minelittlepony.client.pony;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resources.data.IMetadataSectionSerializer;

class PonyDataSerialiser implements IMetadataSectionSerializer<PonyData> {

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public String getSectionName() {
        return "pony";
    }

    @Override
    public PonyData deserialize(JsonObject json) {
        return gson.fromJson(json, PonyData.class);
    }
}

