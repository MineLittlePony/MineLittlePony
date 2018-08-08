package com.voxelmodpack.hdskins.gui;

import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA;
import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN;
import static net.minecraft.client.renderer.GlStateManager.*;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.minelittlepony.gui.Button;
import com.minelittlepony.gui.GameGui;
import com.minelittlepony.gui.IconicButton;
import com.minelittlepony.gui.Label;
import com.minelittlepony.util.math.MathUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.skins.SkinUpload;
import com.voxelmodpack.hdskins.skins.SkinUploadResponse;
import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFilePNG;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

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

    private Button btnModeSteve;
    private Button btnModeAlex;

    private Button btnModeSkin;
    private Button btnModeElytra;

    protected EntityPlayerModel localPlayer;
    protected EntityPlayerModel remotePlayer;

    private DoubleBuffer doubleBuffer;

    private boolean showMessage;
    private float uploadOpacity = 0;

    private String uploadError = "";
    private String uploadMessage = format("hdskins.request");

    private volatile String localMessage = format("hdskins.choose");

    private volatile boolean fetchingSkin;
    private volatile boolean uploadingSkin;
    private volatile boolean pendingRemoteSkinRefresh;
    private volatile boolean throttledByMojang;

    private int refreshCounter = 0;

    private ThreadOpenFilePNG openFileThread;

    private final Object skinLock = new Object();

    private File pendingSkinFile;
    private File selectedSkin;



    private int lastMouseX = 0;

    private static GuiSkins instance;

    protected CubeMap panorama;

    private MinecraftProfileTexture.Type textureType = SKIN;
    private boolean thinArmType = false;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                btnUpload.enabled = true;
                onSetLocalSkin(textureType);
            }
        }

        if (pendingRemoteSkinRefresh) {
            pendingRemoteSkinRefresh = false;
            fetchingSkin = true;
            btnClear.enabled = false;
            reloadRemoteSkin();
        }

        if (throttledByMojang) {
            refreshCounter = (refreshCounter + 1) % 200;
            if (refreshCounter == 0) {
                reloadRemoteSkin();
            }
        }

    }

    protected void onSetRemoteSkin(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
    }

    protected void onSetLocalSkin(Type type) {
    }

    private void reloadRemoteSkin() {
        throttledByMojang = false;

        try {
            remotePlayer.reloadRemoteSkin(this::onSetRemoteSkin);
        } catch (Exception e) {
            e.printStackTrace();
            throttledByMojang = true;
        }

    }

    @Override
    public void initGui() {
        GLWindow.current().setDropTargetListener(files -> {
            files.stream().findFirst().ifPresent(instance::loadLocalFile);
        });

        panorama.init();

        addButton(new Label(width / 2, 10, "hdskins.manager", 0xffffff, true));
        addButton(new Label(34, 34, "hdskins.local", 0xffffff));
        addButton(new Label(width / 2 + 34, 34, "hdskins.server", 0xffffff));

        addButton(new Button(width / 2 - 150, height - 27, 90, 20, "hdskins.options.browse", sender ->{
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

        addButton(btnUpload = new Button(width / 2 - 24, height / 2 - 10, 48, 20, "hdskins.options.chevy", sender -> {
            punchServer("hdskins.upload", selectedSkin.toURI());
        })).setEnabled(canUpload()).setTooltip("hdskins.options.chevy.title");

        addButton(btnClear = new Button(width / 2 + 60, height - 27, 90, 20, "hdskins.options.clear", sender -> {
            if (remotePlayer.isTextureSetupComplete()) {
                punchServer("hdskins.request", null);
            }
        })).setEnabled(!fetchingSkin);

        addButton(new Button(width / 2 - 50, height - 25, 100, 20, "hdskins.options.close", sender -> {
            mc.displayGuiScreen(new GuiMainMenu());
        }));

        addButton(btnModeSteve = new IconicButton(width - 25, 32, sender -> {
            switchSkinMode(false);
        }).setIcon(new ItemStack(Items.LEATHER_LEGGINGS), 0x3c5dcb)).setEnabled(thinArmType).setTooltip("hdskins.mode.steve");

        addButton(btnModeAlex = new IconicButton(width - 25, 51, sender -> {
            switchSkinMode(true);
        }).setIcon(new ItemStack(Items.LEATHER_LEGGINGS), 0xfff500)).setEnabled(!thinArmType).setTooltip("hdskins.mode.alex");


        addButton(btnModeSkin = new IconicButton(width - 25, 75, sender -> {
            switchSkinType(sender, SKIN);
        }).setIcon(new ItemStack(Items.LEATHER_CHESTPLATE))).setEnabled(textureType == ELYTRA).setTooltip(format("hdskins.mode.skin", toTitleCase(SKIN.name())));

        addButton(btnModeElytra = new IconicButton(width - 25, 94, sender -> {
            switchSkinType(sender, ELYTRA);
        }).setIcon(new ItemStack(Items.ELYTRA))).setEnabled(textureType == SKIN).setTooltip(format("hdskins.mode.skin", toTitleCase(ELYTRA.name())));


        addButton(new Button(width - 25, height - 65, 20, 20, "?", sender -> {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_VILLAGER_YES, 1));
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
        mc.addScheduledTask(localPlayer::releaseTextures);

        if (!skinFile.exists()) {
            localMessage = format("hdskins.error.unreadable");
        } else if (!FilenameUtils.isExtension(skinFile.getName(), new String[]{"png", "PNG"})) {
            localMessage = format("hdskins.error.ext");
        } else {
            try {
                BufferedImage chosenImage = ImageIO.read(skinFile);

                if (chosenImage == null) {
                    localMessage = format("hdskins.error.open");
                } else if (!acceptsSkinDimensions(chosenImage.getWidth(), chosenImage.getHeight())) {
                    localMessage = format("hdskins.error.invalid");
                } else {
                    synchronized (skinLock) {
                        pendingSkinFile = skinFile;
                    }
                }
            } catch (IOException e) {
                localMessage = format("hdskins.error.open");
                e.printStackTrace();
            }
        }
    }

    protected boolean acceptsSkinDimensions(int w, int h) {
        return isPowerOfTwo(w) && w == h * 2 || w == h && w <= MAX_SKIN_DIMENSION && h <= MAX_SKIN_DIMENSION;
    }

    protected void switchSkinType(Button sender, Type newType) {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_BREWING_STAND_BREW, 1));

        textureType = newType;

        btnModeSkin.enabled = textureType == ELYTRA;
        btnModeElytra.enabled = textureType == SKIN;

        ItemStack stack = newType == ELYTRA ? new ItemStack(Items.ELYTRA) : ItemStack.EMPTY;
        // put on or take off the elytra
        localPlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
        remotePlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
    }

    protected void switchSkinMode(boolean thin) {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_BREWING_STAND_BREW, 1));

        thinArmType = thin;

        btnModeSteve.enabled = thinArmType;
        btnModeAlex.enabled = !thinArmType;

        // clear currently selected skin
        selectedSkin = null;
        localPlayer.releaseTextures();

        localPlayer.setPreviewThinArms(thinArmType);
        remotePlayer.setPreviewThinArms(thinArmType);
    }

    protected boolean clearMessage() {
        showMessage = false;

        return uploadOpacity == 0;
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (openFileThread == null && !uploadingSkin && clearMessage()) {
            super.actionPerformed(guiButton);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (clearMessage()) {
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
        if (clearMessage()) {
            updateCounter -= (lastMouseX - mouseX);
        }
        lastMouseX = mouseX;
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        if (clearMessage()) {
            if (keyCode == Keyboard.KEY_LEFT) {
                updateCounter -= 5;
            } else if (keyCode == Keyboard.KEY_RIGHT) {
                updateCounter += 5;
            }

            if (openFileThread == null && !uploadingSkin) {
                super.keyTyped(keyChar, keyCode);
            }
        }
    }

    @Override
    protected void drawContents(int mouseX, int mouseY, float partialTick) {
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

        super.drawContents(mouseX, mouseY, partialTick);

        enableClipping(bottom);

        float yPos = height * 0.75F;
        float xPos1 = width / 4;
        float xPos2 = width * 0.75F;
        float scale = height / 4;
        float lookX = mid - mouseX;

        renderPlayerModel(localPlayer, xPos1, yPos, scale, horizon - mouseY, lookX, partialTick);
        renderPlayerModel(remotePlayer, xPos2, yPos, scale, horizon - mouseY, lookX, partialTick);

        disableClipping();

        if (!localPlayer.isUsingLocalTexture()) {
            Gui.drawRect(40, height / 2 - 12, width / 2 - 40, height / 2 + 12, 0xB0000000);
            drawCenteredString(fontRenderer, localMessage, (int)xPos1, height / 2 - 4, 0xffffff);
        }

        if (fetchingSkin) {

            int lineHeight = throttledByMojang ? 18 : 12;

            Gui.drawRect((int) (xPos2 - width / 4 + 40), height / 2 - lineHeight, width - 40, height / 2 + lineHeight, 0xB0000000);

            if (throttledByMojang) {
                drawCenteredString(fontRenderer, format("hdskins.error.mojang"), (int)xPos2, height / 2 - 10, 0xffffff);
                drawCenteredString(fontRenderer, format("hdskins.error.mojang.wait"), (int)xPos2, height / 2 + 2, 0xffffff);
            } else {
                drawCenteredString(fontRenderer, format("hdskins.fetch"), (int)xPos2, height / 2 - 4, 0xffffff);
            }
        }

        if (uploadingSkin || showMessage || uploadOpacity > 0) {
            if (!uploadingSkin && !showMessage) {
                uploadOpacity -= deltaTime / 10;
            } else if (uploadOpacity < 1) {
                uploadOpacity += deltaTime / 10;
            }

            uploadOpacity = MathHelper.clamp(uploadOpacity, 0, 1);
        }

        if (uploadOpacity > 0) {
            int opacity = (Math.min(180, (int) (uploadOpacity * 180)) & 255) << 24;

            Gui.drawRect(0, 0, width, height, opacity);

            if (uploadingSkin) {
                drawCenteredString(fontRenderer, uploadMessage, width / 2, height / 2, 0xffffff);
            } else if (showMessage) {
                int blockHeight = (height - fontRenderer.getWordWrappedHeight(uploadError, width - 10)) / 2;

                drawCenteredString(fontRenderer, format("hdskins.failed"), width / 2, blockHeight - fontRenderer.FONT_HEIGHT * 2, 0xffff55);
                fontRenderer.drawSplitString(uploadError, 5, blockHeight, width - 10, 0xff5555);
            }
        }

        depthMask(true);
        enableDepth();
    }

    private void renderPlayerModel(EntityPlayerModel thePlayer, float xPosition, float yPosition, float scale, float mouseY, float mouseX, float partialTick) {
        mc.getTextureManager().bindTexture(thePlayer.getLocal(Type.SKIN).getTexture());

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
        popAttrib();

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

        disableDepth();
        enableBlend();
        depthMask(false);
    }

    private static boolean isPowerOfTwo(int number) {
        return number != 0 && (number & number - 1) == 0;
    }

    private void punchServer(String uploadMsg, @Nullable URI path) {
        uploadingSkin = true;
        uploadMessage = format(uploadMsg);
        btnUpload.enabled = canUpload();

        HDSkinManager.INSTANCE.getGatewayServer()
                .uploadSkin(mc.getSession(), new SkinUpload(textureType, path, getMetadata()))
                .thenAccept(this::onUploadComplete)
                .exceptionally(this::onUploadFailure);
    }

    private Map<String, String> getMetadata() {
        return ImmutableMap.of("model", thinArmType ? "slim" : "default");
    }

    private Void onUploadFailure(Throwable t) {
        t = Throwables.getRootCause(t);
        LogManager.getLogger().warn("Upload failed", t);
        uploadError = t.toString();
        showMessage = true;
        uploadingSkin = false;
        btnUpload.enabled = canUpload();

        return null;
    }

    private void onUploadComplete(SkinUploadResponse response) {
        LiteLoaderLogger.info("Upload completed with: %s", response);
        pendingRemoteSkinRefresh = true;
        uploadingSkin = false;
        btnUpload.enabled = canUpload();
    }

    protected boolean canUpload() {
        return selectedSkin != null && !uploadingSkin && !pendingRemoteSkinRefresh;
    }
}
