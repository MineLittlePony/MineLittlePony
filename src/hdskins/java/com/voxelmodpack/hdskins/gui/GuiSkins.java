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
    private float uploadOpacity = 0;

    private int lastMouseX = 0;

    private static GuiSkins instance;

    protected CubeMap panorama;

    private MinecraftProfileTexture.Type textureType = SKIN;
    private boolean thinArmType = false;

    public GuiSkins() {
        Minecraft minecraft = Minecraft.getMinecraft();
        //        this.screenTitle = manager;
        GameProfile profile = minecraft.getSession().getProfile();

        localPlayer = getModel(profile);
        remotePlayer = getModel(profile);
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.renderEngine = minecraft.getTextureManager();
        rm.options = minecraft.gameSettings;
        rm.renderViewEntity = localPlayer;
        reloadRemoteSkin();
        fetchingSkin = true;

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

        localPlayer.updateModel();
        remotePlayer.updateModel();
        if (fetchingSkin && remotePlayer.isTextureSetupComplete()) {
            fetchingSkin = false;
            btnClear.enabled = true;
        }

        synchronized (skinLock) {
            if (pendingSkinFile != null) {
                System.out.println("Set " + textureType + " " + pendingSkinFile);
                localPlayer.setLocalTexture(pendingSkinFile, textureType);
                selectedSkin = pendingSkinFile;
                pendingSkinFile = null;
                onSetLocalSkin(textureType);
                btnUpload.enabled = true;
            }
        }

        if (pendingRemoteSkinRefresh) {
            pendingRemoteSkinRefresh = false;
            fetchingSkin = true;
            btnClear.enabled = false;
            reloadRemoteSkin();
        }

        if (throttledByMojang) {
            if (refreshCounter == -1) {
                refreshCounter = 200;
            } else if (refreshCounter > 0) {
                --refreshCounter;
            } else {
                refreshCounter = -1;
                throttledByMojang = false;
                reloadRemoteSkin();
            }
        }

    }

    protected void onSetRemoteSkin(MinecraftProfileTexture.Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture) {
    }

    protected void onSetLocalSkin(MinecraftProfileTexture.Type type) {
    }

    private void reloadRemoteSkin() {
        try {
            remotePlayer.reloadRemoteSkin(this::onSetRemoteSkin);
        } catch (Exception var2) {
            var2.printStackTrace();
            throttledByMojang = true;
        }

    }

    @Override
    public void initGui() {
        enableDnd();

        panorama.init();

        buttonList.clear();
        buttonList.add(btnBrowse = new GuiButton(0, 30, height - 36, 60, 20, "Browse..."));
        buttonList.add(btnUpload = new GuiButton(1, width / 2 - 24, height / 2 - 10, 48, 20, ">>"));
        buttonList.add(btnClear = new GuiButton(2, width - 90, height - 36, 60, 20, "Clear"));
        buttonList.add(btnBack = new GuiButton(3, width / 2 - 50, height - 36, 100, 20, "Close"));

        ItemStack skin = new ItemStack(Items.LEATHER_LEGGINGS);
        Items.LEATHER_LEGGINGS.setColor(skin, 0x3c5dcb);
        buttonList.add(btnModeSkin = new GuiItemStackButton(4, 2, 2, skin));
        skin = new ItemStack(Items.LEATHER_LEGGINGS);
        Items.LEATHER_LEGGINGS.setColor(skin, 0xfff500);
        buttonList.add(btnModeElytra = new GuiItemStackButton(5, 2, 52, new ItemStack(Items.ELYTRA)));
        buttonList.add(btnModeSkinnySkin = new GuiItemStackButton(6, 2, 21, skin));

        buttonList.add(btnAbout = new GuiButton(-1, width - 25, height - 25, 20, 20, "?"));

        btnUpload.enabled = false;
        btnBrowse.enabled = !mc.isFullScreen();

        btnModeSkin.enabled = thinArmType;
        btnModeSkinnySkin.enabled = !thinArmType;
        btnModeElytra.enabled = textureType == SKIN;

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
        openFileThread = null;
        btnBrowse.enabled = true;
        if (dialogResult == 0) {
            loadLocalFile(fileDialog.getSelectedFile());
        }
    }

    private void loadLocalFile(File skinFile) {
        Minecraft.getMinecraft().addScheduledTask(localPlayer::releaseTextures);
        if (!skinFile.exists()) {
            skinMessage = I18n.format("hdskins.error.unreadable");
        } else if (!FilenameUtils.isExtension(skinFile.getName(), new String[]{"png", "PNG"})) {
            skinMessage = I18n.format("hdskins.error.ext");
        } else {
            BufferedImage chosenImage;
            try {
                chosenImage = ImageIO.read(skinFile);
            } catch (IOException var6) {
                skinMessage = I18n.format("hdskins.error.open");
                var6.printStackTrace();
                return;
            }

            if (chosenImage == null) {
                skinMessage = I18n.format("hdskins.error.open");
            } else if (isPowerOfTwo(chosenImage.getWidth())
                    && (chosenImage.getWidth() == chosenImage.getHeight() * 2
                    || chosenImage.getWidth() == chosenImage.getHeight())
                    && chosenImage.getWidth() <= MAX_SKIN_DIMENSION
                    && chosenImage.getHeight() <= MAX_SKIN_DIMENSION) {
                synchronized (skinLock) {
                    pendingSkinFile = skinFile;
                }
            } else {
                skinMessage = I18n.format("hdskins.error.invalid");
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (openFileThread == null && !uploadingSkin) {
            if (uploadError != null) {
                uploadError = null;
            } else {
                if (guiButton.id == btnBrowse.id) {
                    selectedSkin = null;
                    localPlayer.releaseTextures();
                    openFileThread = new ThreadOpenFilePNG(mc, I18n.format("hdskins.open.title"), this::onFileOpenDialogClosed);
                    openFileThread.start();
                    guiButton.enabled = false;
                }

                if (guiButton.id == btnUpload.id) {
                    if (selectedSkin != null) {
                        uploadSkin(mc.getSession(), selectedSkin);
                        btnUpload.enabled = false;
                    } else {
                        setUploadError(I18n.format("hdskins.error.select"));
                    }
                }

                if (guiButton.id == btnClear.id && remotePlayer.isTextureSetupComplete()) {
                    clearUploadedSkin(mc.getSession());
                    btnUpload.enabled = selectedSkin != null;
                }

                if (guiButton.id == btnBack.id) {
                    mc.displayGuiScreen(new GuiMainMenu());
                }

                if (guiButton.id == btnModeSkin.id || guiButton.id == btnModeElytra.id || guiButton.id == btnModeSkinnySkin.id) {
                    ItemStack stack;
                    if (guiButton.id == btnModeSkin.id) {
                        thinArmType = false;
                        textureType = SKIN;
                        stack = ItemStack.EMPTY;
                    } else if (guiButton.id == btnModeSkinnySkin.id) {
                        thinArmType = true;
                        textureType = SKIN;
                        stack = ItemStack.EMPTY;
                    } else {
                        textureType = ELYTRA;
                        stack = new ItemStack(Items.ELYTRA);
                    }

                    btnModeSkin.enabled = thinArmType;
                    btnModeSkinnySkin.enabled = !thinArmType;
                    btnModeElytra.enabled = textureType == SKIN;

                    guiButton.enabled = false;
                    // clear currently selected skin
                    selectedSkin = null;
                    localPlayer.releaseTextures();

                    // put on or take off the elytra
                    localPlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
                    remotePlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);

                    localPlayer.setPreviewThinArms(thinArmType);
                    remotePlayer.setPreviewThinArms(thinArmType);
                }

            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (uploadError != null) {
            uploadError = null;
        } else {
            super.mouseClicked(mouseX, mouseY, button);
            byte top = 30;
            int bottom = height - 40;
            int mid = width / 2;
            if ((mouseX > 30 && mouseX < mid - 30 || mouseX > mid + 30 && mouseX < width - 30) && mouseY > top && mouseY < bottom) {
                localPlayer.swingArm(EnumHand.MAIN_HAND);
                remotePlayer.swingArm(EnumHand.MAIN_HAND);
            }
        }

        lastMouseX = mouseX;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {

        updateCounter -= lastMouseX - mouseX;

        lastMouseX = mouseX;
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        if (openFileThread == null && !uploadingSkin) {

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
        drawGradientRect(mid + 30, horizon, width - 30, bottom, 0x80FFFFFF, 0xffffff);

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

        drawCenteredString(fontRenderer, I18n.format("hdskins.manager"), width / 2, 10, 0xffffff);

        fontRenderer.drawStringWithShadow(I18n.format("hdskins.local"), 34, 34, 0xffffff);
        fontRenderer.drawStringWithShadow(I18n.format("hdskins.server"), width / 2 + 34, 34, 0xffffff);

        disableDepth();
        enableBlend();
        depthMask(false);

        // this is here so the next few things get blended properly
        //Gui.drawRect(0, 0, 1, 1, 0);
        //this.drawGradientRect(30, this.height - 60, mid - 30, bottom, 1, 0xe0ffffff);
        //this.drawGradientRect(mid + 30, this.height - 60, this.width - 30, bottom, 0, 0xE0FFFFFF);

        int labelwidth = (width / 2 - 80) / 2;
        if (!localPlayer.isUsingLocalTexture()) {
            int opacity = fontRenderer.getStringWidth(skinMessage) / 2;
            Gui.drawRect(40, height / 2 - 12, width / 2 - 40, height / 2 + 12, 0xB0000000);
            fontRenderer.drawStringWithShadow(skinMessage, (int) (xPos1 - opacity), height / 2 - 4, 0xffffff);
        }
        if (btnModeSkin.isMouseOver() || btnModeElytra.isMouseOver() || btnModeSkinnySkin.isMouseOver()) {
            int y = Math.max(mouseY, 16);
            String text;
            if (btnModeSkin.isMouseOver()) {
                text = "hdskins.mode.skin";
            } else if (btnModeSkinnySkin.isMouseOver()) {
                text = "hdskins.mode.skinny";
            } else {
                text = "hdskins.mode.elytra";
            }
            this.drawHoveringText(I18n.format(text), mouseX, y);
        }
        if (btnAbout.isMouseOver()) {
            SkinServer gateway = HDSkinManager.INSTANCE.getGatewayServer();
            this.drawHoveringText(Splitter.on("\r\n").splitToList(gateway.toString()), mouseX, mouseY);
        }

        if (fetchingSkin) {
            String opacity1;
            if (throttledByMojang) {
                opacity1 = TextFormatting.RED + I18n.format("hdskins.error.mojang");
                String stringWidth = I18n.format("hdskins.error.mojang.wait");
                int stringWidth1 = fontRenderer.getStringWidth(opacity1) / 2;
                int stringWidth2 = fontRenderer.getStringWidth(stringWidth) / 2;

                Gui.drawRect((int) (xPos2 - labelwidth), height / 2 - 16, width - 40, height / 2 + 16, 0xB0000000);

                fontRenderer.drawStringWithShadow(opacity1, (int) (xPos2 - stringWidth1), height / 2 - 10, 0xffffff);
                fontRenderer.drawStringWithShadow(stringWidth, (int) (xPos2 - stringWidth2), height / 2 + 2, 0xffffff);
            } else {
                opacity1 = I18n.format("hdskins.fetch");
                int stringWidth1 = fontRenderer.getStringWidth(opacity1) / 2;
                Gui.drawRect((int) (xPos2 - labelwidth), height / 2 - 12, width - 40, height / 2 + 12, 0xB0000000);
                fontRenderer.drawStringWithShadow(opacity1, (int) (xPos2 - stringWidth1), height / 2 - 4, 0xffffff);
            }
        }

        if (uploadingSkin || uploadOpacity > 0) {
            if (!uploadingSkin) {
                uploadOpacity -= deltaTime * 0.05F;
            } else if (uploadOpacity < 1) {
                uploadOpacity += deltaTime * 0.1F;
            }

            if (uploadOpacity > 1) {
                uploadOpacity = 1;
            }

            int opacity = Math.min(180, (int) (uploadOpacity * 180)) & 255;
            if (uploadOpacity > 0) {
                Gui.drawRect(0, 0, width, height, opacity << 24);
                if (uploadingSkin) {
                    drawCenteredString(fontRenderer, skinUploadMessage, width / 2, height / 2, opacity << 24 | 0xffffff);
                }
            }
        }

        if (uploadError != null) {
            Gui.drawRect(0, 0, width, height, 0xB0000000);
            drawCenteredString(fontRenderer, I18n.format("hdskins.failed"), width / 2, height / 2 - 10, 0xFFFFFF55);
            drawCenteredString(fontRenderer, uploadError, width / 2, height / 2 + 2, 0xFFFF5555);
        }

        depthMask(true);
        enableDepth();
    }

    private void renderPlayerModel(EntityPlayerModel thePlayer, float xPosition, float yPosition, float scale, float mouseY, float mouseX,
            float partialTick) {
        enableColorMaterial();
        pushMatrix();
        translate(xPosition, yPosition, 300);

        scale(-scale, scale, scale);
        rotate(180, 0, 0, 1);
        rotate(135, 0, 1, 0);

        RenderHelper.enableStandardItemLighting();

        rotate(-135, 0, 1, 0);
        rotate(15, 1, 0, 0);

        float rot = (updateCounter + partialTick) * 2.5F % 360;

        rotate(rot, 0, 1, 0);

        thePlayer.rotationYawHead = (float) Math.atan(mouseX / 20) * 30;

        thePlayer.rotationPitch = -((float) Math.atan(mouseY / 40)) * 20;
        translate(0, thePlayer.getYOffset(), 0);

        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.playerViewY = 180;
        rm.renderEntity(thePlayer, 0, 0, 0, 0, 1, false);

        popMatrix();
        RenderHelper.disableStandardItemLighting();
        disableColorMaterial();
    }

    private void enableClipping(int yBottom) {
        if (doubleBuffer == null) {
            doubleBuffer = BufferUtils.createByteBuffer(32).asDoubleBuffer();
        }

        doubleBuffer.clear();
        doubleBuffer.put(0).put(1).put(0).put(-30).flip();

        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, doubleBuffer);
        doubleBuffer.clear();
        doubleBuffer.put(0).put(-1).put(0).put(yBottom).flip();

        GL11.glClipPlane(GL11.GL_CLIP_PLANE1, doubleBuffer);
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
        uploadingSkin = true;
        skinUploadMessage = I18n.format("hdskins.request");
        HDSkinManager.INSTANCE.getGatewayServer()
                .uploadSkin(session, null, textureType, getMetadata())
                .thenAccept(this::onUploadComplete)
                .exceptionally(this::onFailure);
    }

    private void uploadSkin(Session session, @Nullable File skinFile) {
        uploadingSkin = true;
        skinUploadMessage = I18n.format("hdskins.upload");
        URI path = skinFile == null ? null : skinFile.toURI();
        HDSkinManager.INSTANCE.getGatewayServer()
                .uploadSkin(session, path, textureType, getMetadata())
                .thenAccept(this::onUploadComplete)
                .exceptionally(this::onFailure);
    }

    private Map<String, String> getMetadata() {
        return ImmutableMap.of("model", thinArmType ? "slim" : "default");
    }

    private void setUploadError(@Nullable String error) {
        uploadError = error;
        btnUpload.enabled = true;
    }


    private Void onFailure(Throwable t) {
        t = Throwables.getRootCause(t);
        LogManager.getLogger().warn("Upload failed", t);
        setUploadError(t.toString());
        uploadingSkin = false;

        return null;
    }

    private void onUploadComplete(SkinUploadResponse response) {
        LiteLoaderLogger.info("Upload completed with: %s", response);
        uploadingSkin = false;
        pendingRemoteSkinRefresh = true;
    }

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
