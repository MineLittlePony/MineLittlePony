package com.voxelmodpack.hdskins.gui;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.colorMask;
import static net.minecraft.client.renderer.GlStateManager.depthMask;
import static net.minecraft.client.renderer.GlStateManager.disableAlpha;
import static net.minecraft.client.renderer.GlStateManager.disableCull;
import static net.minecraft.client.renderer.GlStateManager.disableFog;
import static net.minecraft.client.renderer.GlStateManager.enableAlpha;
import static net.minecraft.client.renderer.GlStateManager.enableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableCull;
import static net.minecraft.client.renderer.GlStateManager.enableDepth;
import static net.minecraft.client.renderer.GlStateManager.glTexParameteri;
import static net.minecraft.client.renderer.GlStateManager.loadIdentity;
import static net.minecraft.client.renderer.GlStateManager.matrixMode;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.rotate;
import static net.minecraft.client.renderer.GlStateManager.translate;
import static net.minecraft.client.renderer.GlStateManager.tryBlendFuncSeparate;
import static net.minecraft.client.renderer.GlStateManager.viewport;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class CubeMap {

    private int updateCounter = 0;

    private float lastPartialTick;

    private float zLevel;

    private ResourceLocation viewportTexture;

    private ResourceLocation[] cubemapTextures;

    private Minecraft mc;

    private final GuiScreen owner;

    public CubeMap(GuiScreen owner) {
        this.owner = owner;
        mc = Minecraft.getMinecraft();
    }

    public float getDelta(float partialTick) {
        return updateCounter + partialTick - lastPartialTick;
    }

    public void setSource(String source) {
        setSource(CubeMapRegistry.generatePanoramaResources(source));
    }

    public void setSource(ResourceLocation[] source) {
        cubemapTextures = source;
    }

    public void init() {
        viewportTexture = mc.getTextureManager().getDynamicTextureLocation("skinpanorama", new DynamicTexture(256, 256));
    }

    public void update() {
        updateCounter++;
    }

    public void render(float partialTick, float z) {
        zLevel = z;
        lastPartialTick = updateCounter + partialTick;

        disableFog();
        mc.entityRenderer.disableLightmap();
        disableAlpha();
        renderPanorama(partialTick);
        enableAlpha();
    }

    private void setupCubemapCamera() {
        matrixMode(GL11.GL_PROJECTION);
        pushMatrix();
        loadIdentity();
        Project.gluPerspective(120, 1, 0.05F, 10);
        matrixMode(GL11.GL_MODELVIEW);
        pushMatrix();
        loadIdentity();
    }

    private void revertPanoramaMatrix() {
        matrixMode(GL11.GL_PROJECTION);
        popMatrix();
        matrixMode(GL11.GL_MODELVIEW);
        popMatrix();
    }

    private void renderCubeMapTexture(float partialTick) {
        this.setupCubemapCamera();
        color(1, 1, 1, 1);
        rotate(180, 1, 0, 0);

        enableBlend();
        disableAlpha();
        disableCull();
        depthMask(false);
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        byte blendIterations = 8;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();

        for (int blendPass = 0; blendPass < blendIterations * blendIterations; ++blendPass) {
            pushMatrix();
            float offsetX = ((float) (blendPass % blendIterations) / (float) blendIterations - 0.5F) / 64;
            float offsetY = ((float) (blendPass / blendIterations) / (float) blendIterations - 0.5F) / 64;

            translate(offsetX, offsetY, 0);
            rotate(MathHelper.sin(lastPartialTick / 400) * 25 + 20, 1, 0, 0);
            rotate(-lastPartialTick / 10, 0, 1, 0);

            for (int cubeSide = 0; cubeSide < 6; ++cubeSide) {
                pushMatrix();
                if (cubeSide == 1) {
                    rotate(90, 0, 1, 0);
                }

                if (cubeSide == 2) {
                    rotate(180, 0, 1, 0);
                }

                if (cubeSide == 3) {
                    rotate(-90, 0, 1, 0);
                }

                if (cubeSide == 4) {
                    rotate(90, 1, 0, 0);
                }

                if (cubeSide == 5) {
                    rotate(-90, 1, 0, 0);
                }

                mc.getTextureManager().bindTexture(cubemapTextures[cubeSide]);

                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

                int l = 255 / (blendPass + 1);

                vb.pos(-1, -1, 1).tex(0, 0).color(255, 255, 255, l).endVertex();
                vb.pos(1, -1, 1).tex(1, 0).color(255, 255, 255, l).endVertex();
                vb.pos(1, 1, 1).tex(1, 1).color(255, 255, 255, l).endVertex();
                vb.pos(-1, 1, 1).tex(0, 1).color(255, 255, 255, l).endVertex();

                tessellator.draw();
                popMatrix();
            }

            popMatrix();
            colorMask(true, true, true, false);
        }

        vb.setTranslation(0.0D, 0.0D, 0.0D);
        colorMask(true, true, true, true);
        depthMask(true);
        enableCull();
        enableAlpha();
        enableDepth();
        this.revertPanoramaMatrix();
    }

    private void rotateAndBlurCubemap() {
        mc.getTextureManager().bindTexture(viewportTexture);

        glTexParameteri(3553, 10241, 9729);
        glTexParameteri(3553, 10240, 9729);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        enableBlend();
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        colorMask(true, true, true, false);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        disableAlpha();

        byte blurPasses = 3;

        for (int blurPass = 0; blurPass < blurPasses; ++blurPass) {
            float f = 1 / (float)(blurPass + 1);
            float var7 = (blurPass - 1) / 256F;

            vb.pos(owner.width, owner.height, zLevel).tex(var7, 1).color(1, 1, 1, f).endVertex();
            vb.pos(owner.width, 0, zLevel).tex(1 + var7, 1).color(1, 1, 1, f).endVertex();
            vb.pos(0, 0, zLevel).tex(1 + var7, 0).color(1, 1, 1, f).endVertex();
            vb.pos(0, owner.height, zLevel).tex(var7, 0).color(1, 1, 1, f).endVertex();
        }

        tessellator.draw();
        enableAlpha();
        colorMask(true, true, true, true);
    }

    private void renderPanorama(float partialTicks) {
        mc.getFramebuffer().unbindFramebuffer();

        viewport(0, 0, 256, 256);
        renderCubeMapTexture(partialTicks);

        for (int tessellator = 0; tessellator < 8; ++tessellator) {
            rotateAndBlurCubemap();
        }

        mc.getFramebuffer().bindFramebuffer(true);

        viewport(0, 0, mc.displayWidth, mc.displayHeight);

        float aspect = owner.width > owner.height ? 120F / owner.width : 120F / owner.height;
        float uSample = owner.height * aspect / 256F;
        float vSample = owner.width * aspect / 256F;

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0, owner.height, zLevel).tex(0.5F - uSample, 0.5F + vSample).endVertex();
        vb.pos(owner.width, owner.height, zLevel).tex(0.5F - uSample, 0.5F - vSample).endVertex();
        vb.pos(owner.width, 0, zLevel).tex(0.5F + uSample, 0.5F - vSample).endVertex();
        vb.pos(0, 0, zLevel).tex(0.5F + uSample, 0.5F + vSample).endVertex();
        tessellator.draw();
    }
}
