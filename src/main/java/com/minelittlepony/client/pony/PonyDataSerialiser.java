package com.minelittlepony.client.pony;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.minelittlepony.api.pony.IPonyData;

import net.minecraft.resource.metadata.ResourceMetadataReader;

class PonyDataSerialiser implements ResourceMetadataReader<IPonyData> {

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public String getKey() {
        return "pony";
    }

    @Override
    public IPonyData fromJson(JsonObject json) {
        return gson.fromJson(json, PonyData.class);
    }
}

