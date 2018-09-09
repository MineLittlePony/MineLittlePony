package com.minelittlepony.render.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.model.gear.Muffin;
import com.minelittlepony.model.gear.SaddleBags;
import com.minelittlepony.model.gear.WitchHat;

import java.util.List;

public class LayerGear<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    private static List<IGear> gears = Lists.newArrayList(
            new SaddleBags(),
            new WitchHat(),
            new Muffin()
    );

    public LayerGear(RenderLivingBase<T> renderer) {
        super(renderer);
    }

    @Override
    protected void doPonyRender(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        AbstractPonyModel model = getPlayerModel();

        for (IGear gear : gears) {
            if (gear.canRender(model, entity)) {
                renderGear(model, entity, gear, move, swing, scale, ticks);
            }
        }
    }

    private void renderGear(AbstractPonyModel model, T entity, IGear gear, float move, float swing, float scale, float ticks) {
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        model.transform(gear.getGearLocation());
        gear.getOriginBodyPart(model).postRender(scale);

        ResourceLocation texture = gear.getTexture(entity);
        if (texture != null) {
            getRenderer().bindTexture(texture);
        }

        gear.setLivingAnimations(model, entity);
        gear.setRotationAndAngles(model.isGoingFast(), move, swing, model.getWobbleAmount(), ticks);
        gear.renderPart(scale);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
}
