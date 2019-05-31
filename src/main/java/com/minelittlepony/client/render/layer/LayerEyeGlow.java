package com.minelittlepony.client.render.layer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.IPonyModel;
import com.mojang.blaze3d.platform.GLX;

import static com.mojang.blaze3d.platform.GlStateManager.*;

public class LayerEyeGlow<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    private final Identifier eyeTexture;

    public <V extends IPonyRender<T, M> & IGlowingRenderer> LayerEyeGlow(V renderer) {
        super(renderer);
        eyeTexture = renderer.getEyeTexture();
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        getContext().bindTexture(eyeTexture);

        enableBlend();
        disableAlphaTest();
        blendFunc(SourceFactor.ONE, DestFactor.ONE);

        disableLighting();
        depthMask(!entity.isInvisible());
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680, 0);
        enableLighting();

        color4f(1, 1, 1, 1);

        MinecraftClient.getInstance().gameRenderer.setFogBlack(true);

        getModel().render(entity, move, swing, ticks, headYaw, headPitch, scale);

        MinecraftClient.getInstance().gameRenderer.setFogBlack(false);

        getContext().applyLightmapCoordinates(entity);

        depthMask(true);

        blendFunc(SourceFactor.ONE, DestFactor.ZERO);
        disableBlend();
        enableAlphaTest();
    }

    public interface IGlowingRenderer {
        Identifier getEyeTexture();
    }
}
