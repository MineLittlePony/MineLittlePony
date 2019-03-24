package com.minelittlepony.client.model.gear;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.model.PonyModelConstants;

public abstract class AbstractGear extends ModelBase implements IGear, PonyModelConstants {

    public AbstractGear() {
        textureWidth = 64;
        textureHeight = 64;

        init(0, 0);
    }

    @Override
    public ModelRenderer getOriginBodyPart(AbstractPonyModel model) {
        switch (getGearLocation()) {
            default:
            case HEAD: return model.getHead();
            case NECK: return model.neck;
            case TAIL:
            case LEGS:
            case BODY: return model.getBody();
        }
    }

    @Override
    public void renderSeparately(Entity entity, float scale) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        TextureManager tex = Minecraft.getInstance().getRenderManager().textureManager;
        tex.bindTexture(getTexture(entity));

        renderPart(scale, entity.getUniqueID());

        GL11.glPopAttrib();
    }
}
