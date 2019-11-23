package com.minelittlepony.client.model.part;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.client.util.render.Color;
import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.model.IPart;

import javax.annotation.Nullable;

import java.util.UUID;

import static com.mojang.blaze3d.platform.GlStateManager.*;

import static org.lwjgl.opengl.GL11.*;

public class UnicornHorn implements IPart {

    protected Part horn;
    protected Part glow;

    protected boolean isVisible = true;

    public <T extends Model & ICapitated<ModelPart>> UnicornHorn(T pony, float yOffset, float stretch) {
        this(pony, yOffset, stretch, 0, 0, 0);
    }

    public <T extends Model & ICapitated<ModelPart>> UnicornHorn(T pony, float yOffset, float stretch, int x, int y, int z) {
        horn = new Part(pony, 0, 3);
        glow = new Part(pony, 0, 3);

        horn.offset(HORN_X + x, HORN_Y + y, HORN_Z + z)
            .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .box(0, 0, 0, 1, 4, 1, stretch)
            .pitch = 0.5F;

        glow.offset(HORN_X + x, HORN_Y + y, HORN_Z + z)
            .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .cone(0, 0, 0, 1, 4, 1, stretch + 0.5F)
            .cone(0, 0, 0, 1, 3, 1, stretch + 0.8F);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, @Nullable UUID interpolatorId) {
        if (isVisible) {
            horn.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        }
    }

    public void renderMagic(MatrixStack stack, int tint) {
        if (isVisible) {
            horn.rotate(stack);

            VertexConsumer vertices = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getTranslucentNoCrumbling());
            glow.render(stack, vertices, OverlayTexture.DEFAULT_UV, 0x0F00F0, Color.r(tint), Color.g(tint), Color.b(tint), 0.4F);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
