package com.minelittlepony.model.components;

import com.minelittlepony.model.capabilities.ICapitated;
import com.minelittlepony.render.HornGlowRenderer;
import com.minelittlepony.render.PonyRenderer;

import net.minecraft.client.model.ModelBase;

import static org.lwjgl.opengl.GL11.*;
import static net.minecraft.client.renderer.GlStateManager.*;
import static com.minelittlepony.model.PonyModelConstants.*;

public class UnicornHorn {

    private PonyRenderer horn;
    private HornGlowRenderer glow;

    public <T extends ModelBase & ICapitated> UnicornHorn(T pony, float yOffset, float stretch) {
        this(pony, yOffset, stretch, 0, 0, 0);
    }

    public <T extends ModelBase & ICapitated> UnicornHorn(T pony, float yOffset, float stretch, int x, int y, int z) {
        horn = new PonyRenderer(pony, 0, 3);
        glow = new HornGlowRenderer(pony, 0, 3);

        horn.offset(HORN_X + x, HORN_Y + y, HORN_Z + z)
                .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                               .box(0, 0, 0, 1, 4, 1, stretch).rotateAngleX = 0.5F;

        glow.offset(HORN_X + x, HORN_Y + y, HORN_Z + z)
                .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .setAlpha(0.4f).box(0, 0, 0, 1, 4, 1, stretch + 0.5F)
                .setAlpha(0.2f).box(0, 0, 0, 1, 3, 1, stretch + 0.8F);
    }

    public void render(float scale) {
        horn.render(scale);
    }

    public void renderMagic(int tint, float scale) {
        glPushAttrib(24577);
        disableTexture2D();
        disableLighting();
        enableBlend();
        blendFunc(GL_SRC_ALPHA, GL_ONE);

        horn.postRender(scale);

        glow.setTint(tint).render(scale);

        enableTexture2D();
        enableLighting();
        disableBlend();
        popAttrib();
    }
}
