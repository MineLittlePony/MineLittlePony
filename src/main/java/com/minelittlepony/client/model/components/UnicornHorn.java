package com.minelittlepony.client.model.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

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

    public <T extends Model & ICapitated<Cuboid>> UnicornHorn(T pony, float yOffset, float stretch) {
        this(pony, yOffset, stretch, 0, 0, 0);
    }

    public <T extends Model & ICapitated<Cuboid>> UnicornHorn(T pony, float yOffset, float stretch, int x, int y, int z) {
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
    public void renderPart(float scale, @Nullable UUID interpolatorId) {
        if (isVisible) {
            horn.render(scale);
        }
    }

    public void renderMagic(int tint, float scale) {
        if (isVisible) {
            glPushAttrib(24577);
            disableTexture();
            disableLighting();
            enableBlend();
            blendFunc(GL_SRC_ALPHA, GL_ONE);

            horn.applyTransform(scale);

            MinecraftClient.getInstance().gameRenderer.disableLightmap();
            Color.glColor(tint, 0.4F);

            glow.render(scale);

            MinecraftClient.getInstance().gameRenderer.enableLightmap();

            enableTexture();
            enableLighting();
            disableBlend();
            glPopAttrib();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
