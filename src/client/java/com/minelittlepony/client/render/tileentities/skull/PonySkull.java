package com.minelittlepony.client.render.tileentities.skull;

import net.minecraft.client.Minecraft;

import com.minelittlepony.client.model.components.ModelPonyHead;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.pony.IPony;

public abstract class PonySkull implements ISkull {

    private static ModelPonyHead ponyHead = new ModelPonyHead();

    @Override
    public void preRender(boolean transparency) {

    }

    @Override
    public void bindPony(IPony pony) {
        ponyHead.metadata = pony.getMetadata();
    }

    @Override
    public void render(float animateTicks, float rotation, float scale) {
        ponyHead.render(Minecraft.getInstance().player, animateTicks, 0, 0, rotation, 0, scale);
    }
}