package com.voxelmodpack.hdskins.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;


public class SkinServerSerializer implements JsonSerializer<SkinServer>, JsonDeserializer<SkinServer> {

    public static final SkinServerSerializer instance = new SkinServerSerializer();

    private final BiMap<String, Class<? extends SkinServer>> types = HashBiMap.create(2);

    public SkinServerSerializer() {
        // register default skin server types
        addSkinServerType(ValhallaSkinServer.class);
        addSkinServerType(YggdrasilSkinServer.class);
        addSkinServerType(BethlehemSkinServer.class);
        addSkinServerType(LegacySkinServer.class);
    }

    public void addSkinServerType(Class<? extends SkinServer> type) {
        Preconditions.checkArgument(!type.isInterface(), "type cannot be an interface");
        Preconditions.checkArgument(!Modifier.isAbstract(type.getModifiers()), "type cannot be abstract");

        ServerType st = type.getAnnotation(ServerType.class);

        if (st == null) {
            throw new IllegalArgumentException("class is not annotated with @ServerType");
        }

        types.put(st.value(), type);
    }

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

        return context.deserialize(json, types.get(type));
    }
}
