package com.voxelmodpack.hdskins.gui;

import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA;
import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN;
import static net.minecraft.client.renderer.GlStateManager.*;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.skins.SkinUploadResponse;
import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFilePNG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.file.Path;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GuiSkins extends GuiScreen implements FutureCallback<SkinUploadResponse> {
    private static final int MAX_SKIN_DIMENSION = 1024;
    private int updateCounter = 0;
    private ResourceLocation viewportTexture;
    private static final ResourceLocation[] cubemapTextures = {
            new ResourceLocation("hdskins", "textures/cubemaps/cubemap0_0.png"),
            new ResourceLocation("hdskins", "textures/cubemaps/cubemap0_1.png"),
            new ResourceLocation("hdskins", "textures/cubemaps/cubemap0_2.png"),
            new ResourceLocation("hdskins", "textures/cubemaps/cubemap0_3.png"),
            new ResourceLocation("hdskins", "textures/cubemaps/cubemap0_4.png"),
            new ResourceLocation("hdskins", "textures/cubemaps/cubemap0_5.png")};

    private GuiButton btnBrowse;
    private GuiButton btnUpload;
    private GuiButton btnClear;
    private GuiButton btnBack;
    private GuiButton btnModeSkin;
    private GuiButton btnModeSkinnySkin;
    private GuiButton btnModeElytra;

    protected EntityPlayerModel localPlayer;
    protected EntityPlayerModel remotePlayer;

    private DoubleBuffer doubleBuffer;

    @Nullable
    private String uploadError;
    private volatile String skinMessage = I18n.format("hdskins.choose");
    private String skinUploadMessage = I18n.format("hdskins.request");
    private volatile boolean fetchingSkin;
    private volatile boolean uploadingSkin;
    private volatile boolean pendingRemoteSkinRefresh;
    private volatile boolean throttledByMojang;
    private int refreshCounter = -1;
    private ThreadOpenFilePNG openFileThread;
    private final Object skinLock = new Object();
    private File pendingSkinFile;
    private File selectedSkin;
    private float uploadOpacity = 0.0F;
    private float lastPartialTick;

    private static GuiSkins instance;

    private MinecraftProfileTexture.Type textureType = SKIN;
    private boolean thinArmType = false;

    public GuiSkins() {
        Minecraft minecraft = Minecraft.getMinecraft();
//        this.screenTitle = manager;
        GameProfile profile = minecraft.getSession().getProfile();
        this.localPlayer = getModel(profile);
        this.remotePlayer = getModel(profile);
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.renderEngine = minecraft.getTextureManager();
        rm.options = minecraft.gameSettings;
        rm.renderViewEntity = this.localPlayer;
        this.reloadRemoteSkin();
        this.fetchingSkin = true;

        instance = this;
    }

    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPlayerModel(profile);
    }

    @Override
    public void updateScreen() {
        ++this.updateCounter;
        this.localPlayer.updateModel();
        this.remotePlayer.updateModel();
        if (this.fetchingSkin && this.remotePlayer.isTextureSetupComplete()) {
            this.fetchingSkin = false;
            this.btnClear.enabled = true;
        }

        synchronized (this.skinLock) {
            if (this.pendingSkinFile != null) {
                System.out.println("Set " + textureType + " " + this.pendingSkinFile);
                this.localPlayer.setLocalTexture(this.pendingSkinFile, textureType);
                this.selectedSkin = this.pendingSkinFile;
                this.pendingSkinFile = null;
                this.onSetLocalSkin(textureType);
                this.btnUpload.enabled = true;
            }
        }

        if (this.pendingRemoteSkinRefresh) {
            this.pendingRemoteSkinRefresh = false;
            this.fetchingSkin = true;
            this.btnClear.enabled = false;
            this.reloadRemoteSkin();
        }

        if (this.throttledByMojang) {
            if (this.refreshCounter == -1) {
                this.refreshCounter = 200;
            } else if (this.refreshCounter > 0) {
                --this.refreshCounter;
            } else {
                this.refreshCounter = -1;
                this.throttledByMojang = false;
                this.reloadRemoteSkin();
            }
        }

    }

    protected void onSetRemoteSkin(MinecraftProfileTexture.Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture) {
    }

    protected void onSetLocalSkin(MinecraftProfileTexture.Type type) {
    }

    private void reloadRemoteSkin() {
        try {
            this.remotePlayer.reloadRemoteSkin(this::onSetRemoteSkin);
        } catch (Exception var2) {
            var2.printStackTrace();
            this.throttledByMojang = true;
        }

    }

    @Override
    public void initGui() {
        enableDnd();

        this.initPanoramaRenderer();
        this.buttonList.clear();
        this.buttonList.add(this.btnBrowse = new GuiButton(0, 30, this.height - 36, 60, 20, "Browse..."));
        this.buttonList.add(this.btnUpload = new GuiButton(1, this.width / 2 - 24, this.height / 2 - 10, 48, 20, ">>"));
        this.buttonList.add(this.btnClear = new GuiButton(2, this.width - 90, this.height - 36, 60, 20, "Clear"));
        this.buttonList.add(this.btnBack = new GuiButton(3, this.width / 2 - 50, this.height - 36, 100, 20, "Close"));

        ItemStack skin = new ItemStack(Items.LEATHER_LEGGINGS);
        Items.LEATHER_LEGGINGS.setColor(skin, 0x3c5dcb);
        this.buttonList.add(this.btnModeSkin = new GuiItemStackButton(4, 2, 2, skin));
        skin = new ItemStack(Items.LEATHER_LEGGINGS);
        Items.LEATHER_LEGGINGS.setColor(skin, 0xfff500);
        this.buttonList.add(this.btnModeSkinnySkin = new GuiItemStackButton(6, 2, 24, skin));
        this.buttonList.add(this.btnModeElytra = new GuiItemStackButton(5, 2, 46, new ItemStack(Items.ELYTRA)));

        this.btnUpload.enabled = false;
        this.btnBrowse.enabled = !this.mc.isFullScreen();

        this.btnModeSkin.enabled = this.thinArmType || this.textureType != SKIN;
        this.btnModeSkinnySkin.enabled = !this.thinArmType || this.textureType != SKIN;
        this.btnModeElytra.enabled = this.textureType == SKIN;

    }

    private void enableDnd() {
        GLWindow.current().setDropTargetListener((FileDropListener) files -> {
            files.stream().findFirst().ifPresent(instance::loadLocalFile);
        });
    }

    private void initPanoramaRenderer() {
        this.viewportTexture = this.mc.getTextureManager().getDynamicTextureLocation("skinpanorama", new DynamicTexture(256, 256));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        localPlayer.releaseTextures();
        remotePlayer.releaseTextures();
        HDSkinManager.clearSkinCache();

        GLWindow.current().clearDropTargetListener();
    }

    private void onFileOpenDialogClosed(JFileChooser fileDialog, int dialogResult) {
        this.openFileThread = null;
        this.btnBrowse.enabled = true;
        if (dialogResult == 0) {
            this.loadLocalFile(fileDialog.getSelectedFile());
        }
    }

    private void loadLocalFile(File skinFile) {
        Minecraft.getMinecraft().addScheduledTask(localPlayer::releaseTextures);
        if (!skinFile.exists()) {
            this.skinMessage = I18n.format("hdskins.error.unreadable");
        } else if (!FilenameUtils.isExtension(skinFile.getName(), new String[]{"png", "PNG"})) {
            this.skinMessage = I18n.format("hdskins.error.ext");
        } else {
            BufferedImage chosenImage;
            try {
                chosenImage = ImageIO.read(skinFile);
            } catch (IOException var6) {
                this.skinMessage = I18n.format("hdskins.error.open");
                var6.printStackTrace();
                return;
            }

            if (chosenImage == null) {
                this.skinMessage = I18n.format("hdskins.error.open");
            } else if (isPowerOfTwo(chosenImage.getWidth())
                    && (chosenImage.getWidth() == chosenImage.getHeight() * 2
                    || chosenImage.getWidth() == chosenImage.getHeight())
                    && chosenImage.getWidth() <= MAX_SKIN_DIMENSION
                    && chosenImage.getHeight() <= MAX_SKIN_DIMENSION) {
                synchronized (this.skinLock) {
                    this.pendingSkinFile = skinFile;
                }
            } else {
                this.skinMessage = I18n.format("hdskins.error.invalid");
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (this.openFileThread == null && !this.uploadingSkin) {
            if (this.uploadError != null) {
                this.uploadError = null;
            } else {
                if (guiButton.id == this.btnBrowse.id) {
                    this.selectedSkin = null;
                    this.localPlayer.releaseTextures();
                    this.openFileThread = new ThreadOpenFilePNG(this.mc, I18n.format("hdskins.open.title"), this::onFileOpenDialogClosed);
                    this.openFileThread.setParent(GLWindow.current().getFrame()).start();
                    guiButton.enabled = false;
                }

                if (guiButton.id == this.btnUpload.id) {
                    if (this.selectedSkin != null) {
                        this.uploadSkin(this.mc.getSession(), this.selectedSkin);
                        this.btnUpload.enabled = false;
                    } else {
                        this.setUploadError(I18n.format("hdskins.error.select"));
                    }
                }

                if (guiButton.id == this.btnClear.id && this.remotePlayer.isTextureSetupComplete()) {
                    this.clearUploadedSkin(this.mc.getSession());
                    this.btnUpload.enabled = this.selectedSkin != null;
                }

                if (guiButton.id == this.btnBack.id) {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                }

                if (guiButton.id == this.btnModeSkin.id || guiButton.id == this.btnModeElytra.id || guiButton.id == this.btnModeSkinnySkin.id) {
                    ItemStack stack;
                    if (guiButton.id == this.btnModeSkin.id) {
                        this.thinArmType = false;
                        this.textureType = SKIN;
                        this.btnModeElytra.enabled = true;
                        this.btnModeSkinnySkin.enabled = true;
                        stack = ItemStack.EMPTY;
                    } else if (guiButton.id == this.btnModeSkinnySkin.id) {
                        this.thinArmType = true;
                        this.textureType = SKIN;
                        this.btnModeSkin.enabled = true;
                        this.btnModeElytra.enabled = true;
                        stack = ItemStack.EMPTY;
                    } else {
                        this.textureType = ELYTRA;
                        this.btnModeSkin.enabled = true;
                        this.btnModeSkinnySkin.enabled = true;
                        stack = new ItemStack(Items.ELYTRA);
                    }
                    guiButton.enabled = false;
                    // clear currently selected skin
                    this.selectedSkin = null;
                    this.localPlayer.releaseTextures();

                    // put on or take off the elytra
                    this.localPlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
                    this.remotePlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);

                    this.localPlayer.setPreviewThinArms(thinArmType);
                    this.remotePlayer.setPreviewThinArms(thinArmType);
                }

            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (this.uploadError != null) {
            this.uploadError = null;
        } else {
            super.mouseClicked(mouseX, mouseY, button);
            byte top = 30;
            int bottom = this.height - 40;
            int mid = this.width / 2;
            if ((mouseX > 30 && mouseX < mid - 30 || mouseX > mid + 30 && mouseX < this.width - 30) && mouseY > top && mouseY < bottom) {
                this.localPlayer.swingArm(EnumHand.MAIN_HAND);
                this.remotePlayer.swingArm(EnumHand.MAIN_HAND);
            }

        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        if (this.openFileThread == null && !this.uploadingSkin) {
            super.keyTyped(keyChar, keyCode);
        }
    }

    private void setupCubemapCamera() {
        matrixMode(GL11.GL_PROJECTION);
        pushMatrix();
        loadIdentity();
        GLU.gluPerspective(150.0F, 1.0F, 0.05F, 10.0F);
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
        color(1.0F, 1.0F, 1.0F, 1.0F);
        rotate(180.0F, 1.0F, 0.0F, 0.0F);
        enableBlend();
        disableAlpha();
        disableCull();
        depthMask(false);
        blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        byte blendIterations = 8;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();

        for (int blendPass = 0; blendPass < blendIterations * blendIterations; ++blendPass) {
            pushMatrix();
            float offsetX = ((float) (blendPass % blendIterations) / (float) blendIterations - 0.5F) / 64.0F;
            float offsetY = ((float) (blendPass / blendIterations) / (float) blendIterations - 0.5F) / 64.0F;
            float offsetZ = 0.0F;
            translate(offsetX, offsetY, offsetZ);
            rotate(MathHelper.sin((this.updateCounter + 200 + partialTick) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            rotate(-(this.updateCounter + 200 + partialTick) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int cubeSide = 0; cubeSide < 6; ++cubeSide) {
                pushMatrix();
                if (cubeSide == 1) {
                    rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (cubeSide == 2) {
                    rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (cubeSide == 3) {
                    rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (cubeSide == 4) {
                    rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (cubeSide == 5) {
                    rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(cubemapTextures[cubeSide]);
                // wr.setColorRGBA_I(0xffffff, 255 / (blendPass + 1));
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                vb.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).endVertex();
                vb.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).endVertex();
                vb.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).endVertex();
                vb.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).endVertex();
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
        this.mc.getTextureManager().bindTexture(this.viewportTexture);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        enableBlend();
        blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        byte blurPasses = 4;

        for (int blurPass = 0; blurPass < blurPasses; ++blurPass) {
            float var7 = (blurPass - blurPasses / 2) / 256.0F;
            vb.pos(this.width, this.height, this.zLevel).tex(0.0F + var7, 0.0D).endVertex();
            vb.pos(this.width, 0.0D, this.zLevel).tex(1.0F + var7, 0.0D).endVertex();
            vb.pos(0.0D, 0.0D, this.zLevel).tex(1.0F + var7, 1.0D).endVertex();
            vb.pos(0.0D, this.height, this.zLevel).tex(0.0F + var7, 1.0D).endVertex();
        }

        tessellator.draw();
        colorMask(true, true, true, true);
        disableBlend();
    }

    private void renderPanorama(float partialTicks) {
        viewport(0, 0, 256, 256);
        this.renderCubeMapTexture(partialTicks);
        disableTexture2D();
        enableTexture2D();

        for (int tessellator = 0; tessellator < 8; ++tessellator) {
            this.rotateAndBlurCubemap();
        }

        viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        float aspect = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
        float uSample = this.height * aspect / 256.0F;
        float vSample = this.width * aspect / 256.0F;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        // wr.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        vb.pos(0.0D, this.height, this.zLevel).tex(0.5F - uSample, 0.5F + vSample).endVertex();
        vb.pos(this.width, this.height, this.zLevel).tex(0.5F - uSample, 0.5F - vSample).endVertex();
        vb.pos(this.width, 0.0D, this.zLevel).tex(0.5F + uSample, 0.5F - vSample).endVertex();
        vb.pos(0.0D, 0.0D, this.zLevel).tex(0.5F + uSample, 0.5F + vSample).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        float deltaTime = this.updateCounter + partialTick - this.lastPartialTick;
        this.lastPartialTick = this.updateCounter + partialTick;

        disableFog();
        this.mc.entityRenderer.disableLightmap();
        this.renderPanorama(partialTick);

        int top = 30;
        int bottom = this.height - 40;
        int mid = this.width / 2;
        int horizon = this.height / 2 + this.height / 5;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        Gui.drawRect(30, top, mid - 30, bottom, Integer.MIN_VALUE);
        Gui.drawRect(mid + 30, top, this.width - 30, bottom, Integer.MIN_VALUE);

        this.drawGradientRect(30, horizon, mid - 30, bottom, 0x80FFFFFF, 0xffffff);
        this.drawGradientRect(mid + 30, horizon, this.width - 30, bottom, 0x80FFFFFF, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTick);

        popAttrib();
        this.enableClipping(bottom);

        float yPos = this.height * 0.75F;
        float xPos1 = this.width * 0.25F;
        float xPos2 = this.width * 0.75F;
        float scale = this.height * 0.25F;

        mc.getTextureManager().bindTexture(this.localPlayer.getSkinTexture());
        this.renderPlayerModel(this.localPlayer, xPos1, yPos, scale, yPos - scale * 1.8F - mouseY, partialTick);

        mc.getTextureManager().bindTexture(this.remotePlayer.getSkinTexture());
        this.renderPlayerModel(this.remotePlayer, xPos2, yPos, scale, yPos - scale * 1.8F - mouseY, partialTick);

        this.disableClipping();

        this.drawCenteredString(this.fontRenderer, I18n.format("hdskins.manager"), this.width / 2, 10, 0xffffff);

        this.fontRenderer.drawStringWithShadow(I18n.format("hdskins.local"), 34, 34, 0xffffff);
        this.fontRenderer.drawStringWithShadow(I18n.format("hdskins.server"), this.width / 2 + 34, 34, 0xffffff);

        disableDepth();
        enableBlend();
        depthMask(false);

        // this is here so the next few things get blended properly
        Gui.drawRect(0, 0, 1, 1, 0);
        this.drawGradientRect(30, this.height - 60, mid - 30, bottom, 1, 0xe0ffffff);
        this.drawGradientRect(mid + 30, this.height - 60, this.width - 30, bottom, 0, 0xE0FFFFFF);

        int labelwidth = (this.width / 2 - 80) / 2;
        if (!this.localPlayer.isUsingLocalTexture()) {
            int opacity = this.fontRenderer.getStringWidth(this.skinMessage) / 2;
            Gui.drawRect(40, this.height / 2 - 12, this.width / 2 - 40, this.height / 2 + 12, 0xB0000000);
            this.fontRenderer.drawStringWithShadow(this.skinMessage, (int) (xPos1 - opacity), this.height / 2 - 4, 0xffffff);
        }
        if (this.btnModeSkin.isMouseOver() || this.btnModeElytra.isMouseOver() || this.btnModeSkinnySkin.isMouseOver()) {
            int y = Math.max(mouseY, 16);
            String text;
            if (this.btnModeSkin.isMouseOver()) {
                text = "hdskins.mode.skin";
            } else if (this.btnModeSkinnySkin.isMouseOver()) {
                text = "hdskins.mode.skinny";
            } else {
                text = "hdskins.mode.elytra";
            }
            this.drawHoveringText(I18n.format(text), mouseX, y);
        }

        if (this.fetchingSkin) {
            String opacity1;
            if (this.throttledByMojang) {
                opacity1 = TextFormatting.RED + I18n.format("hdskins.error.mojang");
                String stringWidth = I18n.format("hdskins.error.mojang.wait");
                int stringWidth1 = this.fontRenderer.getStringWidth(opacity1) / 2;
                int stringWidth2 = this.fontRenderer.getStringWidth(stringWidth) / 2;

                Gui.drawRect((int) (xPos2 - labelwidth), this.height / 2 - 16, this.width - 40, this.height / 2 + 16, 0xB0000000);

                this.fontRenderer.drawStringWithShadow(opacity1, (int) (xPos2 - stringWidth1), this.height / 2 - 10, 0xffffff);
                this.fontRenderer.drawStringWithShadow(stringWidth, (int) (xPos2 - stringWidth2), this.height / 2 + 2, 0xffffff);
            } else {
                opacity1 = I18n.format("hdskins.fetch");
                int stringWidth1 = this.fontRenderer.getStringWidth(opacity1) / 2;
                Gui.drawRect((int) (xPos2 - labelwidth), this.height / 2 - 12, this.width - 40, this.height / 2 + 12, 0xB0000000);
                this.fontRenderer.drawStringWithShadow(opacity1, (int) (xPos2 - stringWidth1), this.height / 2 - 4, 0xffffff);
            }
        }

        if (this.uploadingSkin || this.uploadOpacity > 0.0F) {
            if (!this.uploadingSkin) {
                this.uploadOpacity -= deltaTime * 0.05F;
            } else if (this.uploadOpacity < 1.0F) {
                this.uploadOpacity += deltaTime * 0.1F;
            }

            if (this.uploadOpacity > 1.0F) {
                this.uploadOpacity = 1.0F;
            }

            int opacity = Math.min(180, (int) (this.uploadOpacity * 180.0F)) & 255;
            if (this.uploadOpacity > 0.0F) {
                Gui.drawRect(0, 0, this.width, this.height, opacity << 24);
                if (this.uploadingSkin) {
                    this.drawCenteredString(this.fontRenderer, this.skinUploadMessage, this.width / 2, this.height / 2, opacity << 24 | 0xffffff);
                }
            }
        }

        if (this.uploadError != null) {
            Gui.drawRect(0, 0, this.width, this.height, 0xB0000000);
            this.drawCenteredString(this.fontRenderer, I18n.format("hdskins.failed"), this.width / 2, this.height / 2 - 10, 0xFFFFFF55);
            this.drawCenteredString(this.fontRenderer, this.uploadError, this.width / 2, this.height / 2 + 2, 0xFFFF5555);
        }

        depthMask(true);
        enableDepth();
    }

    private void renderPlayerModel(EntityPlayerModel thePlayer, float xPosition, float yPosition, float scale, float mouseY, float partialTick) {
        enableColorMaterial();
        pushMatrix();
        translate(xPosition, yPosition, 300.0F);

        scale(-scale, scale, scale);
        rotate(180.0F, 0.0F, 0.0F, 1.0F);
        rotate(135.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        rotate(15.0F, 1.0F, 0.0F, 0.0F);
        rotate((this.updateCounter + partialTick) * 2.5F, 0.0F, 1.0F, 0.0F);
        thePlayer.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        translate(0.0D, thePlayer.getYOffset(), 0.0D);

        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.playerViewY = 180.0F;
        rm.renderEntity(thePlayer, 0, 0, 0, 0, 1, false);

        popMatrix();
        RenderHelper.disableStandardItemLighting();
        disableColorMaterial();
    }

    private void enableClipping(int yBottom) {
        if (this.doubleBuffer == null) {
            this.doubleBuffer = BufferUtils.createByteBuffer(32).asDoubleBuffer();
        }

        this.doubleBuffer.clear();
        this.doubleBuffer.put(0.0D).put(1.0D).put(0.0D).put((-30)).flip();

        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, this.doubleBuffer);
        this.doubleBuffer.clear();
        this.doubleBuffer.put(0.0D).put(-1.0D).put(0.0D).put(yBottom).flip();

        GL11.glClipPlane(GL11.GL_CLIP_PLANE1, this.doubleBuffer);
        GL11.glEnable(GL11.GL_CLIP_PLANE0);
        GL11.glEnable(GL11.GL_CLIP_PLANE1);
    }

    private void disableClipping() {
        GL11.glDisable(GL11.GL_CLIP_PLANE1);
        GL11.glDisable(GL11.GL_CLIP_PLANE0);
    }

    private static boolean isPowerOfTwo(int number) {
        return number != 0 && (number & number - 1) == 0;
    }

    private void clearUploadedSkin(Session session) {
        this.uploadingSkin = true;
        this.skinUploadMessage = I18n.format("hdskins.request");
        Futures.addCallback(HDSkinManager.INSTANCE.getGatewayServer().uploadSkin(session, null, this.textureType, this.thinArmType), this);
    }

    private void uploadSkin(Session session, @Nullable File skinFile) {
        this.uploadingSkin = true;
        this.skinUploadMessage = I18n.format("hdskins.upload");
        Path path = skinFile == null ? null : skinFile.toPath();
        Futures.addCallback(HDSkinManager.INSTANCE.getGatewayServer().uploadSkin(session, path, this.textureType, this.thinArmType), this);
    }

    private void setUploadError(@Nullable String error) {
        this.uploadError = error != null && error.startsWith("ERROR: ") ? error.substring(7) : error;
        this.btnUpload.enabled = true;
    }

    @Override
    public void onSuccess(@Nullable SkinUploadResponse result) {
        if (result != null)
            onUploadComplete(result);
    }

    @Override
    public void onFailure(Throwable t) {
        LogManager.getLogger().warn("Upload failed", t);
        this.setUploadError(t.toString());
        this.uploadingSkin = false;
    }

    private void onUploadComplete(SkinUploadResponse response) {
        LiteLoaderLogger.info("Upload completed with: %s", response);
        this.uploadingSkin = false;
        if (!"OK".equalsIgnoreCase(response.getMessage())) {
            this.setUploadError(response.getMessage());
        } else {
            this.pendingRemoteSkinRefresh = true;
        }
    }

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
