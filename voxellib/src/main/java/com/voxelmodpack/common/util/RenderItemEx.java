package com.voxelmodpack.common.util;

import static com.mumfrey.liteloader.gl.GL.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class RenderItemEx extends RenderItem {
    public RenderItemEx() {
        super(Minecraft.getMinecraft().getTextureManager(),
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager());
    }

    /**
     * Render the item's icon or block into the GUI, including the glint effect.
     */
    @Override
    public void renderItemOverlays(FontRenderer fontRenderer, ItemStack stack, int xPos, int yPos) {
        if (stack != null) {
            this.drawRect(xPos, yPos, 16, 16, 1, 0, 0, 1);
            this.drawRect(xPos + 1, yPos + 1, 14, 14, 0.6F, 0, 0, 1);
            super.renderItemOverlays(fontRenderer, stack, xPos, yPos);
        }
    }

    @SuppressWarnings("cast")
    private void drawRect(int x, int y, int width, int height, float r, float g, float b, float a) {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glDisableDepthTest();
        glDepthFunc(GL_GREATER);
        glDisableLighting();
        glDepthMask(false);
        glEnableBlend();
        glDisableTexture2D();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(r, g, b, a);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRender = tessellator.getWorldRenderer();
        worldRender.startDrawingQuads();
        worldRender.addVertex((double) (x + 0), (double) (y + 0), 0.0D);
        worldRender.addVertex((double) (x + 0), (double) (y + height), 0.0D);
        worldRender.addVertex((double) (x + width), (double) (y + height), 0.0D);
        worldRender.addVertex((double) (x + width), (double) (y + 0), 0.0D);
        tessellator.draw();
        glDepthMask(true);
        glPopAttrib();
    }
}
