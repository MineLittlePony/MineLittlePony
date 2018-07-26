package com.voxelmodpack.hdskins.skins;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.voxelmodpack.hdskins.HDSkinManager;

import java.lang.reflect.Type;

public class SkinServerSerializer implements JsonSerializer<SkinServer>, JsonDeserializer<SkinServer> {

    @Override
    public JsonElement serialize(SkinServer src, Type typeOfSrc, JsonSerializationContext context) {
        ServerType serverType = src.getClass().getAnnotation(ServerType.class);

        if (serverType == null) {
            throw new JsonIOException("Skin server class did not have a type: " + typeOfSrc);
        }

        JsonObject obj = context.serialize(src).getAsJsonObject();
        obj.addProperty("type", serverType.value());

        return obj;
    }

    @Override
    public SkinServer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String type = json.getAsJsonObject().get("type").getAsString();

        SkinServer server = context.deserialize(json, HDSkinManager.INSTANCE.getSkinServerClass(type));

        server.validate();

        return server;
    }
}
