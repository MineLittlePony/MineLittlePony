package com.voxelmodpack.hdskins.gui;

import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA;
import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN;
import static net.minecraft.client.renderer.GlStateManager.*;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.minelittlepony.gui.Button;
import com.minelittlepony.gui.GameGui;
import com.minelittlepony.gui.Label;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.skins.SkinUpload;
import com.voxelmodpack.hdskins.skins.SkinUploadResponse;
import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFilePNG;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
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
import javax.swing.UIManager;

public class GuiSkins extends GameGui {

    private static final int MAX_SKIN_DIMENSION = 1024;
    private int updateCounter = 0;

    private Button btnUpload;
    private Button btnClear;

    private Button btnModeSkin;
    private Button btnModeSkinnySkin;
    private Button btnModeElytra;

    protected EntityPlayerModel localPlayer;
    protected EntityPlayerModel remotePlayer;

    private DoubleBuffer doubleBuffer;

    @Nullable
    private String uploadError;
    private volatile String skinMessage = format("hdskins.choose");
    private String skinUploadMessage = format("hdskins.request");

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
        instance = this;

        Minecraft minecraft = Minecraft.getMinecraft();
        GameProfile profile = minecraft.getSession().getProfile();

        localPlayer = getModel(profile);
        remotePlayer = getModel(profile);

        RenderManager rm = minecraft.getRenderManager();
        rm.renderEngine = minecraft.getTextureManager();
        rm.options = minecraft.gameSettings;
        rm.renderViewEntity = localPlayer;

        reloadRemoteSkin();

        fetchingSkin = true;

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
        GLWindow.current().setDropTargetListener((FileDropListener) files -> {
            files.stream().findFirst().ifPresent(instance::loadLocalFile);
        });

        panorama.init();

        addButton(new Label(width / 2, 10, "hdskins.manager", 0xffffff, true));

        addButton(new Button(width / 2 - 150, height - 27, 90, 20, "Browse...", sender ->{
            selectedSkin = null;
            localPlayer.releaseTextures();
            openFileThread = new ThreadOpenFilePNG(mc, format("hdskins.open.title"), (fileDialog, dialogResult) -> {
                openFileThread = null;
                sender.enabled = true;
                if (dialogResult == 0) {
                    loadLocalFile(fileDialog.getSelectedFile());
                }
            });
            openFileThread.start();
            sender.enabled = false;
        })).setEnabled(!mc.isFullScreen());

        addButton(btnUpload = new Button(width / 2 - 24, height / 2 - 10, 48, 20, ">>", sender -> {
            if (selectedSkin != null) {
                punchServer("hdskins.upload", selectedSkin.toURI());
                sender.enabled = false;
            } else {
                setUploadError(format("hdskins.error.select"));
            }
        })).setEnabled(false);

        addButton(btnClear = new Button(width / 2 + 60, height - 27, 90, 20, "Clear", sender -> {
            if (remotePlayer.isTextureSetupComplete()) {
                punchServer("hdskins.request", null);
                btnUpload.enabled = selectedSkin != null;
            }
        }));

        addButton(new Button(width / 2 - 50, height - 25, 100, 20, "Close", sender -> {
            mc.displayGuiScreen(new GuiMainMenu());
        }));

        addButton(btnModeSkin = new GuiItemStackButton(width - 25, 32, new ItemStack(Items.LEATHER_LEGGINGS), 0x3c5dcb, sender -> {
            switchSkinMode(sender, false, SKIN, ItemStack.EMPTY);
        })).setEnabled(thinArmType).setTooltip("hdskins.mode.skin");

        addButton(btnModeElytra = new GuiItemStackButton(width - 25, 82, new ItemStack(Items.ELYTRA), sender -> {
            switchSkinMode(sender, thinArmType, ELYTRA, new ItemStack(Items.ELYTRA));
        })).setEnabled(textureType == SKIN).setTooltip("hdskins.mode.elytra");

        addButton(btnModeSkinnySkin = new GuiItemStackButton(width - 25, 51, new ItemStack(Items.LEATHER_LEGGINGS), 0xfff500, sender -> {
            switchSkinMode(sender, true, SKIN, ItemStack.EMPTY);
        })).setEnabled(!thinArmType).setTooltip("hdskins.mode.skinny");

        addButton(new Button(width - 25, height - 65, 20, 20, "?", sender -> {

        })).setTooltip(Splitter.on("\r\n").splitToList(HDSkinManager.INSTANCE.getGatewayServer().toString()));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        localPlayer.releaseTextures();
        remotePlayer.releaseTextures();
        HDSkinManager.INSTANCE.clearSkinCache();

        GLWindow.current().clearDropTargetListener();
    }

    private void loadLocalFile(File skinFile) {
        Minecraft.getMinecraft().addScheduledTask(localPlayer::releaseTextures);

        if (!skinFile.exists()) {
            skinMessage = format("hdskins.error.unreadable");
        } else if (!FilenameUtils.isExtension(skinFile.getName(), new String[]{"png", "PNG"})) {
            skinMessage = format("hdskins.error.ext");
        } else {
            try {
                BufferedImage chosenImage = ImageIO.read(skinFile);

                if (chosenImage == null) {
                    skinMessage = format("hdskins.error.open");
                } else if (!acceptsSkinDimensions(chosenImage.getWidth(), chosenImage.getHeight())) {
                    skinMessage = format("hdskins.error.invalid");
                } else {
                    synchronized (skinLock) {
                        pendingSkinFile = skinFile;
                    }
                }
            } catch (IOException var6) {
                skinMessage = format("hdskins.error.open");
                var6.printStackTrace();
            }
        }
    }

    protected boolean acceptsSkinDimensions(int w, int h) {
        return isPowerOfTwo(w) && w == h * 2 || w == h && w <= MAX_SKIN_DIMENSION && h <= MAX_SKIN_DIMENSION;
    }

    protected void switchSkinMode(Button sender, boolean thin, Type newType, ItemStack stack) {
        thinArmType = thin;
        textureType = newType;

        btnModeSkin.enabled = thinArmType;
        btnModeSkinnySkin.enabled = !thinArmType;
        btnModeElytra.enabled = textureType == SKIN;

        sender.enabled = false;

        // clear currently selected skin
        selectedSkin = null;
        localPlayer.releaseTextures();

        // put on or take off the elytra
        localPlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
        remotePlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);

        localPlayer.setPreviewThinArms(thinArmType);
        remotePlayer.setPreviewThinArms(thinArmType);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (openFileThread == null && !uploadingSkin) {
            if (uploadError != null) {
                uploadError = null;
            } else {
                super.actionPerformed(guiButton);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (uploadError != null) {
            uploadError = null;
        } else {
            super.mouseClicked(mouseX, mouseY, button);

            int bottom = height - 40;
            int mid = width / 2;

            if ((mouseX > 30 && mouseX < mid - 30 || mouseX > mid + 30 && mouseX < width - 30) && mouseY > 30 && mouseY < bottom) {
                localPlayer.swingArm(EnumHand.MAIN_HAND);
                remotePlayer.swingArm(EnumHand.MAIN_HAND);
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

        int bottom = height - 40;
        int mid = width / 2;
        int horizon = height / 2 + height / 5;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        Gui.drawRect(30, 30, mid - 30, bottom, Integer.MIN_VALUE);
        Gui.drawRect(mid + 30, 30, width - 30, bottom, Integer.MIN_VALUE);

        drawGradientRect(30, horizon, mid - 30, bottom, 0x80FFFFFF, 0xffffff);
        drawGradientRect(mid + 30, horizon, width - 30, bottom, 0x80FFFFFF, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTick);

        popAttrib();
        enableClipping(bottom);

        float yPos = height * 0.75F;
        float xPos1 = width / 4;
        float xPos2 = width * 0.75F;
        float scale = height / 4;
        float lookX = mid - mouseX;

        mc.getTextureManager().bindTexture(localPlayer.getLocal(Type.SKIN).getTexture());

        renderPlayerModel(localPlayer, xPos1, yPos, scale, horizon - mouseY, lookX, partialTick);

        mc.getTextureManager().bindTexture(remotePlayer.getLocal(Type.SKIN).getTexture());

        renderPlayerModel(remotePlayer, xPos2, yPos, scale, horizon - mouseY, lookX, partialTick);

        disableClipping();

        fontRenderer.drawStringWithShadow(format("hdskins.local"), 34, 34, 0xffffff);
        fontRenderer.drawStringWithShadow(format("hdskins.server"), width / 2 + 34, 34, 0xffffff);

        disableDepth();
        enableBlend();
        depthMask(false);

        if (!localPlayer.isUsingLocalTexture()) {
            int opacity = fontRenderer.getStringWidth(skinMessage) / 2;

            Gui.drawRect(40, height / 2 - 12, width / 2 - 40, height / 2 + 12, 0xB0000000);
            fontRenderer.drawStringWithShadow(skinMessage, (int) (xPos1 - opacity), height / 2 - 4, 0xffffff);
        }

        if (fetchingSkin) {

            int lineHeight = throttledByMojang ? 16 : 12;

            Gui.drawRect((int) (xPos2 - width / 4 + 40), height / 2 - lineHeight, width - 40, height / 2 + lineHeight, 0xB0000000);

            if (throttledByMojang) {
                drawCenteredString(fontRenderer, format("hdskins.error.mojang"), (int)xPos2, height / 2 - 10, 0xffffff);
                drawCenteredString(fontRenderer, format("hdskins.error.mojang.wait"), (int)xPos2, height / 2 + 2, 0xffffff);
            } else {
                drawCenteredString(fontRenderer, format("hdskins.fetch"), (int)xPos2, height / 2 - 4, 0xffffff);
            }
        }

        if (uploadingSkin || uploadOpacity > 0) {
            if (!uploadingSkin) {
                uploadOpacity -= deltaTime / 20;
            } else if (uploadOpacity < 1) {
                uploadOpacity += deltaTime / 10;
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
            drawCenteredString(fontRenderer, format("hdskins.failed"), width / 2, height / 2 - 10, 0xFFFFFF55);
            drawCenteredString(fontRenderer, uploadError, width / 2, height / 2 + 2, 0xFFFF5555);
        }

        depthMask(true);
        enableDepth();
    }

    private void renderPlayerModel(EntityPlayerModel thePlayer, float xPosition, float yPosition, float scale, float mouseY, float mouseX, float partialTick) {
        enableColorMaterial();
        pushMatrix();
        translate(xPosition, yPosition, 300);

        scale(-scale, scale, scale);
        rotate(180, 0, 0, 1);
        rotate(135, 0, 1, 0);

        RenderHelper.enableStandardItemLighting();

        rotate(-135, 0, 1, 0);
        rotate(15, 1, 0, 0);

        rotate(((updateCounter + partialTick) * 2.5F) % 360, 0, 1, 0);

        thePlayer.rotationYawHead = (float)Math.atan(mouseX / 20) * 30;
        thePlayer.rotationPitch = (float)Math.atan(mouseY / 40) * -20;

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

    private void punchServer(String uploadMsg, @Nullable URI path) {
        uploadingSkin = true;
        skinUploadMessage = format(uploadMsg);

        HDSkinManager.INSTANCE.getGatewayServer()
                .uploadSkin(mc.getSession(), new SkinUpload(textureType, path, getMetadata()))
                .thenAccept(this::onUploadComplete)
                .exceptionally(this::onUploadFailure);
    }

    private Map<String, String> getMetadata() {
        return ImmutableMap.of("model", thinArmType ? "slim" : "default");
    }

    private void setUploadError(@Nullable String error) {
        uploadError = error;
        btnUpload.enabled = true;
    }

    private Void onUploadFailure(Throwable t) {
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
