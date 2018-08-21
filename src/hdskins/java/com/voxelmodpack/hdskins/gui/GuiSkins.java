package com.voxelmodpack.hdskins.gui;

import com.minelittlepony.gui.Button;
import com.minelittlepony.gui.GameGui;
import com.minelittlepony.gui.IconicButton;
import com.minelittlepony.gui.Label;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.SkinChooser;
import com.voxelmodpack.hdskins.SkinUploader;
import com.voxelmodpack.hdskins.SkinUploader.ISkinUploadHandler;
import com.voxelmodpack.hdskins.skins.CallableFutures;
import com.voxelmodpack.hdskins.skins.SkinServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.*;

public class GuiSkins extends GameGui implements ISkinUploadHandler {

    private int updateCounter = 0;

    private Button btnUpload;
    private Button btnDownload;
    private Button btnClear;

    private Button btnModeSteve;
    private Button btnModeAlex;

    private Button btnModeSkin;
    private Button btnModeElytra;

    protected EntityPlayerModel localPlayer;
    protected EntityPlayerModel remotePlayer;

    private DoubleBuffer doubleBuffer;

    private float msgFadeOpacity = 0;

    private int lastMouseX = 0;

    protected final SkinUploader uploader;
    protected final SkinChooser chooser;

    protected final CubeMap panorama;

    public GuiSkins(List<SkinServer> servers) {
        mc = Minecraft.getMinecraft();
        GameProfile profile = mc.getSession().getProfile();

        localPlayer = getModel(profile);
        remotePlayer = getModel(profile);

        RenderManager rm = mc.getRenderManager();
        rm.renderEngine = mc.getTextureManager();
        rm.options = mc.gameSettings;
        rm.renderViewEntity = localPlayer;

        uploader = new SkinUploader(servers, localPlayer, remotePlayer, this);
        chooser = new SkinChooser(uploader);
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
        uploader.update();

        btnClear.enabled = uploader.canClear();
        btnDownload.enabled = uploader.canClear();
    }

    @Override
    public void initGui() {
        GLWindow.current().setDropTargetListener(files -> {
            files.stream().findFirst().ifPresent(file -> {
                chooser.selectFile(file);
                updateButtons();
            });
        });

        panorama.init();

        addButton(new Label(width / 2, 10, "hdskins.manager", 0xffffff, true));
        addButton(new Label(34, 34, "hdskins.local", 0xffffff));
        addButton(new Label(width / 2 + 34, 34, "hdskins.server", 0xffffff));

        addButton(new Button(width / 2 - 150, height - 27, 90, 20, "hdskins.options.browse", sender -> {
            sender.enabled = false;
            chooser.openBrowsePNG(mc, format("hdskins.open.title"), () -> {
                sender.enabled = true;
                updateButtons();
            });
        })).setEnabled(!mc.isFullScreen());

        addButton(btnUpload = new Button(width / 2 - 24, height / 2 - 20, 48, 20, "hdskins.options.chevy", sender -> {
            if (uploader.canUpload()) {
                punchServer("hdskins.upload");
            }
        })).setEnabled(uploader.canUpload()).setTooltip("hdskins.options.chevy.title");

        addButton(btnDownload = new Button(width / 2 - 24, height / 2 + 20, 48, 20, "hdskins.options.download", sender -> {
            if (uploader.canClear()) {
                sender.enabled = false;
                chooser.openSavePNG(mc, format("hdskins.open.title"), () -> {
                    sender.enabled = true;
                });
            }
        })).setEnabled(uploader.canClear()).setTooltip("hdskins.options.download.title");

        addButton(btnClear = new Button(width / 2 + 60, height - 27, 90, 20, "hdskins.options.clear", sender -> {
            if (uploader.canClear()) {
                punchServer("hdskins.request");
            }
        })).setEnabled(uploader.canClear());

        addButton(new Button(width / 2 - 50, height - 25, 100, 20, "hdskins.options.close", sender -> {
            mc.displayGuiScreen(new GuiMainMenu());
        }));

        addButton(btnModeSteve = new IconicButton(width - 25, 32, sender -> {
            switchSkinMode("default");
        }).setIcon(new ItemStack(Items.LEATHER_LEGGINGS), 0x3c5dcb)).setEnabled("slim".equals(uploader.getMetadataField("model"))).setTooltip("hdskins.mode.steve").setTooltipOffset(0, 10);

        addButton(btnModeAlex = new IconicButton(width - 25, 51, sender -> {
            switchSkinMode("slim");
        }).setIcon(new ItemStack(Items.LEATHER_LEGGINGS), 0xfff500)).setEnabled("default".equals(uploader.getMetadataField("model"))).setTooltip("hdskins.mode.alex").setTooltipOffset(0, 10);


        addButton(btnModeSkin = new IconicButton(width - 25, 75, sender -> {
            uploader.setSkinType(Type.SKIN);
        }).setIcon(new ItemStack(Items.LEATHER_CHESTPLATE))).setEnabled(uploader.getSkinType() == Type.ELYTRA).setTooltip(format("hdskins.mode.skin", toTitleCase(Type.SKIN.name()))).setTooltipOffset(0, 10);

        addButton(btnModeElytra = new IconicButton(width - 25, 94, sender -> {
            uploader.setSkinType(Type.ELYTRA);
        }).setIcon(new ItemStack(Items.ELYTRA))).setEnabled(uploader.getSkinType() == Type.SKIN).setTooltip(format("hdskins.mode.skin", toTitleCase(Type.ELYTRA.name()))).setTooltipOffset(0, 10);

        addButton(new Button(width - 25, height - 65, 20, 20, "?", sender -> {
            uploader.cycleGateway();
            playSound(SoundEvents.ENTITY_VILLAGER_YES);
            sender.setTooltip(uploader.getGateway());
        })).setTooltip(uploader.getGateway()).setTooltipOffset(0, 10);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        try {
            uploader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HDSkinManager.INSTANCE.clearSkinCache();

        GLWindow.current().clearDropTargetListener();
    }

    @Override
    public void onSkinTypeChanged(Type newType) {
        playSound(SoundEvents.BLOCK_BREWING_STAND_BREW);

        btnModeSkin.enabled = newType == Type.ELYTRA;
        btnModeElytra.enabled = newType == Type.SKIN;
    }

    protected void switchSkinMode(String model) {
        playSound(SoundEvents.BLOCK_BREWING_STAND_BREW);

        boolean thinArmType = model == "slim";

        btnModeSteve.enabled = thinArmType;
        btnModeAlex.enabled = !thinArmType;

        uploader.setMetadataField("model", model);
        localPlayer.setPreviewThinArms(thinArmType);
        remotePlayer.setPreviewThinArms(thinArmType);
    }

    protected boolean canTakeEvents() {
        return !chooser.pickingInProgress() && uploader.tryClearStatus() && msgFadeOpacity == 0;
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (canTakeEvents()) {
            super.actionPerformed(guiButton);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (canTakeEvents()) {
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
        if (canTakeEvents()) {
            updateCounter -= (lastMouseX - mouseX);
        }
        lastMouseX = mouseX;
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        if (canTakeEvents()) {
            if (keyCode == Keyboard.KEY_LEFT) {
                updateCounter -= 5;
            } else if (keyCode == Keyboard.KEY_RIGHT) {
                updateCounter += 5;
            }

            if (!chooser.pickingInProgress() && !uploader.uploadInProgress()) {
                super.keyTyped(keyChar, keyCode);
            }
        }
    }

    @Override
    protected void drawContents(int mouseX, int mouseY, float partialTick) {
        boolean sneak = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        localPlayer.setSneaking(sneak);
        remotePlayer.setSneaking(sneak);

        boolean jump = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        localPlayer.setJumping(jump);
        remotePlayer.setJumping(jump);

        float deltaTime = panorama.getDelta(partialTick);
        panorama.render(partialTick, zLevel);

        int bottom = height - 40;
        int mid = width / 2;
        int horizon = height / 2 + height / 5;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        drawRect(30, 30, mid - 30, bottom, Integer.MIN_VALUE);
        drawRect(mid + 30, 30, width - 30, bottom, Integer.MIN_VALUE);

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

        if (chooser.getStatus() != null && !uploader.canUpload()) {
            drawRect(40, height / 2 - 12, width / 2 - 40, height / 2 + 12, 0xB0000000);
            drawCenteredString(fontRenderer, format(chooser.getStatus()), (int) xPos1, height / 2 - 4, 0xffffff);
        }

        if (uploader.downloadInProgress() || uploader.isThrottled() || uploader.isOffline()) {

            int lineHeight = uploader.isThrottled() ? 18 : 12;

            drawRect((int) (xPos2 - width / 4 + 40), height / 2 - lineHeight, width - 40, height / 2 + lineHeight, 0xB0000000);

            if (uploader.isThrottled()) {
                drawCenteredString(fontRenderer, format(SkinUploader.ERR_MOJANG), (int) xPos2, height / 2 - 10, 0xff5555);
                drawCenteredString(fontRenderer, format(SkinUploader.ERR_WAIT, uploader.getRetries()), (int) xPos2, height / 2 + 2, 0xff5555);
            } else if (uploader.isOffline()) {
                drawCenteredString(fontRenderer, format(SkinUploader.ERR_OFFLINE), (int) xPos2, height / 2 - 4, 0xff5555);
            } else {
                drawCenteredString(fontRenderer, format(SkinUploader.STATUS_FETCH), (int) xPos2, height / 2 - 4, 0xffffff);
            }
        }

        boolean uploadInProgress = uploader.uploadInProgress();
        boolean showError = uploader.hasStatus();

        if (uploadInProgress || showError || msgFadeOpacity > 0) {
            if (!uploadInProgress && !showError) {
                msgFadeOpacity -= deltaTime / 10;
            } else if (msgFadeOpacity < 1) {
                msgFadeOpacity += deltaTime / 10;
            }

            msgFadeOpacity = MathHelper.clamp(msgFadeOpacity, 0, 1);
        }

        if (msgFadeOpacity > 0) {
            int opacity = (Math.min(180, (int) (msgFadeOpacity * 180)) & 255) << 24;

            drawRect(0, 0, width, height, opacity);

            String errorMsg = uploader.getStatusMessage();

            if (uploadInProgress) {
                drawCenteredString(fontRenderer, errorMsg, width / 2, height / 2, 0xffffff);
            } else if (showError) {
                int blockHeight = (height - fontRenderer.getWordWrappedHeight(errorMsg, width - 10)) / 2;

                drawCenteredString(fontRenderer, format("hdskins.failed"), width / 2, blockHeight - fontRenderer.FONT_HEIGHT * 2, 0xffff55);
                fontRenderer.drawSplitString(errorMsg, 5, blockHeight, width - 10, 0xff5555);
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

        scale(scale, scale, scale);
        rotate(-15, 1, 0, 0);

        RenderHelper.enableStandardItemLighting();

        rotate(((updateCounter + partialTick) * 2.5F) % 360, 0, 1, 0);

        thePlayer.rotationYawHead = (float) Math.atan(mouseX / 20) * 30;
        thePlayer.rotationPitch = (float) Math.atan(mouseY / 40) * -20;

        mc.getRenderManager().renderEntity(thePlayer, 0, 0, 0, 0, 1, false);

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

    private void punchServer(String uploadMsg) {
        uploader.uploadSkin(format(uploadMsg)).handle(CallableFutures.callback(this::updateButtons));

        updateButtons();
    }

    private void updateButtons() {
        btnUpload.enabled = uploader.canUpload();
        btnDownload.enabled = uploader.canUpload();
    }
}
