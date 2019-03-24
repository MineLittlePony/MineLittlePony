package com.minelittlepony.client.render.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerEyeGlow<T extends EntityLiving> extends AbstractPonyLayer<T> {

    private final ResourceLocation eyeTexture;

    public <V extends RenderLiving<T> & IGlowingRenderer> LayerEyeGlow(V renderer) {
        super(renderer);
        eyeTexture = renderer.getEyeTexture();
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        getRenderer().bindTexture(eyeTexture);

        enableBlend();
        disableAlphaTest();
        blendFunc(SourceFactor.ONE, DestFactor.ONE);

        disableLighting();
        depthMask(!entity.isInvisible());
        OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, 61680, 0);
        enableLighting();

        color4f(1, 1, 1, 1);

        Minecraft.getInstance().entityRenderer.setupFogColor(true);

        getMainModel().render(entity, move, swing, ticks, headYaw, headPitch, scale);

        Minecraft.getInstance().entityRenderer.setupFogColor(false);

        ((RenderLiving<T>)getRenderer()).setLightmap(entity);

        depthMask(true);

        blendFunc(SourceFactor.ONE, DestFactor.ZERO);
        disableBlend();
        enableAlphaTest();
    }

    public interface IGlowingRenderer {
        ResourceLocation getEyeTexture();
    }
}
