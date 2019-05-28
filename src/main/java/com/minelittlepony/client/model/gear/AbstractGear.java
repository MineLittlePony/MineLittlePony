package com.minelittlepony.client.model.gear;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.model.gear.IGear;

public abstract class AbstractGear extends Model implements IGear {

    public AbstractGear() {
        textureWidth = 64;
        textureHeight = 64;

        init(0, 0);
    }

    @Override
    public void renderSeparately(Entity entity, float scale) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        TextureManager tex = MinecraftClient.getInstance().getEntityRenderManager().textureManager;
        tex.bindTexture(getTexture(entity));

        renderPart(scale, entity.getUuid());

        GL11.glPopAttrib();
    }
}
