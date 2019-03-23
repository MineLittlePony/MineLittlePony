package com.minelittlepony.hdskins.server;

import net.minecraft.util.Session;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SkinUpload {

    private final Session session;
    private final URI image;
    private final Map<String, String> metadata;
    private final Type type;

    public SkinUpload(Session session, Type type, @Nullable URI image, Map<String, String> metadata) {
        this.session = session;
        this.image = image;
        this.metadata = metadata;
        this.type = type;
    }

    public Session getSession() {
        return session;
    }

    @Nullable
    public URI getImage() {
        return image;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public MinecraftProfileTexture.Type getType() {
        return type;
    }

    public String getSchemaAction() {
        return image == null ? "none" : image.getScheme();
    }
}
