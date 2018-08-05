package com.voxelmodpack.hdskins.gui;

import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA;
import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN;
import static net.minecraft.client.renderer.GlStateManager.*;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.skins.SkinServer;
import com.voxelmodpack.hdskins.skins.SkinUpload;
import com.voxelmodpack.hdskins.skins.SkinUploadResponse;
import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFilePNG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.DoubleBuffer;
import java.util.Map;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class GuiSkins extends GuiScreen {

    private static final int MAX_SKIN_DIMENSION = 1024;
    private int updateCounter = 0;

    private GuiButton btnBrowse;
    private GuiButton btnUpload;
    private GuiButton btnClear;
    private GuiButton btnBack;
    private GuiButton btnModeSkin;
    private GuiButton btnModeSkinnySkin;
    private GuiButton btnModeElytra;

    private GuiButton btnAbout;

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

    private int lastMouseX = 0;

    private static GuiSkins instance;

    protected CubeMap panorama;

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

        panorama = new CubeMap(this);
        initPanorama();
    }

    protected void initPanorama() {
        panorama.setSource("hdskins:textures/cubemaps/cubemap0_%d.png");
    }

    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPlayerModel(profile);
    }

    @Override
    public void updateScreen() {

        if (!(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))) {
            updateCounter++;
        }
        panorama.update();

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

        panorama.init();

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
        this.buttonList.add(this.btnModeElytra = new GuiItemStackButton(5, 2, 52, new ItemStack(Items.ELYTRA)));
        this.buttonList.add(this.btnModeSkinnySkin = new GuiItemStackButton(6, 2, 21, skin));

        this.buttonList.add(this.btnAbout = new GuiButton(-1, this.width - 25, this.height - 25, 20, 20, "?"));

        this.btnUpload.enabled = false;
        this.btnBrowse.enabled = !this.mc.isFullScreen();

        this.btnModeSkin.enabled = this.thinArmType;
        this.btnModeSkinnySkin.enabled = !this.thinArmType;
        this.btnModeElytra.enabled = this.textureType == SKIN;

    }

    private void enableDnd() {
        GLWindow.current().setDropTargetListener((FileDropListener) files -> {
            files.stream().findFirst().ifPresent(instance::loadLocalFile);
        });
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
                    this.openFileThread.start();
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
                        stack = ItemStack.EMPTY;
                    } else if (guiButton.id == this.btnModeSkinnySkin.id) {
                        this.thinArmType = true;
                        this.textureType = SKIN;
                        stack = ItemStack.EMPTY;
                    } else {
                        this.textureType = ELYTRA;
                        stack = new ItemStack(Items.ELYTRA);
                    }

                    this.btnModeSkin.enabled = thinArmType;
                    this.btnModeSkinnySkin.enabled = !thinArmType;
                    this.btnModeElytra.enabled = this.textureType == SKIN;

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

        lastMouseX = mouseX;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {

        updateCounter -= (lastMouseX - mouseX);

        lastMouseX = mouseX;
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        if (this.openFileThread == null && !this.uploadingSkin) {

            if (keyCode == Keyboard.KEY_LEFT) {
                updateCounter -= 5;
            } else if (keyCode == Keyboard.KEY_RIGHT) {
                updateCounter += 5;
            }

            super.keyTyped(keyChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        float deltaTime = panorama.getDelta(partialTick);
        panorama.render(partialTick, zLevel);


        int top = 30;
        int bottom = height - 40;
        int mid = width / 2;
        int horizon = height / 2 + height / 5;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        Gui.drawRect(30, top, mid - 30, bottom, Integer.MIN_VALUE);
        Gui.drawRect(mid + 30, top, width - 30, bottom, Integer.MIN_VALUE);

        drawGradientRect(30, horizon, mid - 30, bottom, 0x80FFFFFF, 0xffffff);
        drawGradientRect(mid + 30, horizon, this.width - 30, bottom, 0x80FFFFFF, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTick);

        popAttrib();
        enableClipping(bottom);

        float yPos = height * 0.75F;
        float xPos1 = width * 0.25F;
        float xPos2 = width * 0.75F;
        float scale = height * 0.25F;
        float lookX = mid - mouseX;

        mc.getTextureManager().bindTexture(localPlayer.getSkinTexture());

        renderPlayerModel(localPlayer, xPos1, yPos, scale, horizon - mouseY, lookX, partialTick);

        mc.getTextureManager().bindTexture(remotePlayer.getSkinTexture());

        renderPlayerModel(remotePlayer, xPos2, yPos, scale, horizon - mouseY, lookX, partialTick);

        disableClipping();

        drawCenteredString(this.fontRenderer, I18n.format("hdskins.manager"), width / 2, 10, 0xffffff);

        fontRenderer.drawStringWithShadow(I18n.format("hdskins.local"), 34, 34, 0xffffff);
        fontRenderer.drawStringWithShadow(I18n.format("hdskins.server"), width / 2 + 34, 34, 0xffffff);

        disableDepth();
        enableBlend();
        depthMask(false);

        // this is here so the next few things get blended properly
        //Gui.drawRect(0, 0, 1, 1, 0);
        //this.drawGradientRect(30, this.height - 60, mid - 30, bottom, 1, 0xe0ffffff);
        //this.drawGradientRect(mid + 30, this.height - 60, this.width - 30, bottom, 0, 0xE0FFFFFF);

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
        if (this.btnAbout.isMouseOver()) {
            SkinServer gateway = HDSkinManager.INSTANCE.getGatewayServer();
            this.drawHoveringText(Splitter.on("\r\n").splitToList(gateway.toString()), mouseX, mouseY);
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

    private void renderPlayerModel(EntityPlayerModel thePlayer, float xPosition, float yPosition, float scale, float mouseY, float mouseX,
            float partialTick) {
        enableColorMaterial();
        pushMatrix();
        translate(xPosition, yPosition, 300.0F);

        scale(-scale, scale, scale);
        rotate(180.0F, 0.0F, 0.0F, 1.0F);
        rotate(135.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        rotate(15.0F, 1.0F, 0.0F, 0.0F);

        float rot = ((updateCounter + partialTick) * 2.5F) % 360;

        rotate(rot, 0, 1, 0);

        thePlayer.rotationYawHead = ((float) Math.atan(mouseX / 20)) * 30;

        thePlayer.rotationPitch = -((float) Math.atan(mouseY / 40)) * 20;
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
        HDSkinManager.INSTANCE.getGatewayServer()
                .uploadSkin(session, new SkinUpload(this.textureType, null, getMetadata()))
                .thenAccept(this::onUploadComplete)
                .exceptionally(this::onFailure);
    }

    private void uploadSkin(Session session, @Nullable File skinFile) {
        this.uploadingSkin = true;
        this.skinUploadMessage = I18n.format("hdskins.upload");
        URI path = skinFile == null ? null : skinFile.toURI();
        HDSkinManager.INSTANCE.getGatewayServer()
                .uploadSkin(session, new SkinUpload(this.textureType, path, getMetadata()))
                .thenAccept(this::onUploadComplete)
                .exceptionally(this::onFailure);
    }

    private Map<String, String> getMetadata() {
        return ImmutableMap.of("model", this.thinArmType ? "slim" : "default");
    }

    private void setUploadError(@Nullable String error) {
        this.uploadError = error;
        this.btnUpload.enabled = true;
    }

    private Void onFailure(Throwable t) {
        t = Throwables.getRootCause(t);
        LogManager.getLogger().warn("Upload failed", t);
        this.setUploadError(t.toString());
        this.uploadingSkin = false;

        return null;
    }

    private void onUploadComplete(SkinUploadResponse response) {
        LiteLoaderLogger.info("Upload completed with: %s", response);
        this.uploadingSkin = false;
        this.pendingRemoteSkinRefresh = true;
    }

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
