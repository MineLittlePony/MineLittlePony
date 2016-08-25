package com.voxelmodpack.hdskins.resource;

import java.util.List;
import java.util.UUID;

import net.minecraft.util.ResourceLocation;

class SkinData {

    List<Skin> skins;
}

class Skin {

    String name;
    UUID uuid;
    private String skin;

    public ResourceLocation getTexture() {
        return new ResourceLocation("hdskins", String.format("textures/skins/%s.png", skin));
    }
}
