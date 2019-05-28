package com.minelittlepony.client.pony;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.resource.metadata.ResourceMetadataReader;

class PonyDataSerialiser implements ResourceMetadataReader<PonyData> {

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public String getKey() {
        return "pony";
    }

    @Override
    public PonyData fromJson(JsonObject json) {
        return gson.fromJson(json, PonyData.class);
    }
}

