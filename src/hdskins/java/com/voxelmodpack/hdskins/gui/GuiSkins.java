package com.voxelmodpack.hdskins.gui;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.upload.IUploadCompleteCallback;
import com.voxelmodpack.hdskins.upload.ThreadMultipartPostUpload;
import com.voxelmodpack.hdskins.upload.awt.IOpenFileCallback;
import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFilePNG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Window.Type;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.Map;

import static net.minecraft.client.renderer.GlStateManager.*;

public class GuiSkins extends GuiScreen implements IUploadCompleteCallback, IOpenFileCallback {
    private static final int MAX_SKIN_DIMENSION = 8192;
    private static final String skinServerId = "7853dfddc358333843ad55a2c7485c4aa0380a51";
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
    protected EntityPlayerModel localPlayer;
    protected EntityPlayerModel remotePlayer;
    protected DoubleBuffer doubleBuffer;
    //    private String screenTitle;
    private String uploadError;
    private volatile String skinMessage = I18n.format("hdskins.choose");
    private String skinUploadMessage = I18n.format("hdskins.request");
    private volatile boolean fetchingSkin;
    private volatile boolean uploadingSkin;
    private volatile boolean pendingRemoteSkinRefresh;
    private volatile boolean throttledByMojang;
    private int refreshCounter = -1;
    private ThreadOpenFilePNG openFileThread;
    private ThreadMultipartPostUpload threadSkinUpload;
    private final Object skinLock = new Object();
    private File pendingSkinFile;
    private File selectedSkin;
    private BufferedImage pendingSkinImage;
    private float uploadOpacity = 0.0F;
    private float lastPartialTick;
    private JFrame fileDrop;

    // translations
    private final String screenTitle = I18n.format("hdskins.manager");
    private final String unreadable = I18n.format("hdskins.error.unreadable");
    private final String ext = I18n.format("hdskins.error.ext");
    private final String open = I18n.format("hdskins.error.open");
    private final String invalid = I18n.format("hdskins.error.invalid");
    private final String select = I18n.format("hdskins.error.select");
    private final String mojang = I18n.format("hdskins.error.mojang");
    private final String wait = I18n.format("hdskins.error.mojang.wait");
    private final String title = I18n.format("hdskins.open.title");
    private final String fetch = I18n.format("hdskins.fetch");
    private final String failed = I18n.format("hdskins.failed");
    private final String request = I18n.format("hdskins.request");
    private final String upload = I18n.format("hdskins.upload");
    private final String localSkin = I18n.format("hdskins.local");
    private final String serverSkin = I18n.format("hdskins.server");

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
                this.localPlayer.setLocalSkin(this.pendingSkinFile);
                this.selectedSkin = this.pendingSkinFile;
                this.pendingSkinFile = null;
                this.onSetLocalSkin(this.pendingSkinImage);
                this.pendingSkinImage = null;
                this.btnUpload.enabled = true;
            }
        }

        if (this.pendingRemoteSkinRefresh) {
            this.pendingRemoteSkinRefresh = false;
            this.fetchingSkin = true;
            this.btnClear.enabled = false;
            this.reloadRemoteSkin();
            this.onSetRemoteSkin();
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

    protected void onSetRemoteSkin() {
    }

    protected void onSetLocalSkin(BufferedImage skin) {
    }

    private void reloadRemoteSkin() {
        try {
            this.remotePlayer.reloadRemoteSkin();
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
        this.btnUpload.enabled = false;
        this.btnBrowse.enabled = !this.mc.isFullScreen();
    }

    /**
     * @wbp.parser.entryPoint
     */
    private void enableDnd() {
        if (fileDrop != null) {
            fileDrop.setVisible(true);
            return;
        }
        fileDrop = new JFrame("Skin Drop");
        fileDrop.setType(Type.UTILITY);
        fileDrop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        fileDrop.setResizable(false);
        fileDrop.setTitle("Skin Drop");
        fileDrop.setSize(256, 256);
        // fileDrop.setAlwaysOnTop(true);
        fileDrop.getContentPane().setLayout(null);
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        panel.setBounds(10, 11, 230, 205);
        fileDrop.getContentPane().add(panel);
        JLabel txtInst = new JLabel("Drop skin file here");
        txtInst.setHorizontalAlignment(SwingConstants.CENTER);
        txtInst.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(txtInst);

        DropTarget dt = new DropTarget();
        fileDrop.setDropTarget(dt);
        try {
            dt.addDropTargetListener((FileDropListener) files -> files.stream().findFirst().ifPresent(this::loadLocalFile));
            fileDrop.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPanoramaRenderer() {
        this.viewportTexture = this.mc.getTextureManager().getDynamicTextureLocation("skinpanorama", new DynamicTexture(256, 256));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (this.fileDrop != null)
            this.fileDrop.dispose();
        this.localPlayer.releaseTextures();
        this.remotePlayer.releaseTextures();
    }

    @Override
    public void onFileOpenDialogClosed(JFileChooser fileDialog, int dialogResult) {
        this.openFileThread = null;
        this.btnBrowse.enabled = true;
        if (dialogResult == 0) {
            this.loadLocalFile(fileDialog.getSelectedFile());
        }
    }

    private void loadLocalFile(File skinFile) {
        Minecraft.getMinecraft().addScheduledTask(localPlayer::releaseTextures);
        if (!skinFile.exists()) {
            this.skinMessage = unreadable;
        } else if (!FilenameUtils.isExtension(skinFile.getName(), new String[]{"png", "PNG"})) {
            this.skinMessage = ext;
        } else {
            BufferedImage chosenImage;
            try {
                chosenImage = ImageIO.read(skinFile);
            } catch (IOException var6) {
                this.skinMessage = open;
                var6.printStackTrace();
                return;
            }

            if (chosenImage == null) {
                this.skinMessage = open;
            } else if (isPowerOfTwo(chosenImage.getWidth())
                    && (chosenImage.getWidth() == chosenImage.getHeight() * 2
                    || chosenImage.getWidth() == chosenImage.getHeight())
                    && chosenImage.getWidth() <= MAX_SKIN_DIMENSION
                    && chosenImage.getHeight() <= MAX_SKIN_DIMENSION) {
                synchronized (this.skinLock) {
                    this.pendingSkinFile = skinFile;
                    this.pendingSkinImage = chosenImage;
                }
            } else {
                this.skinMessage = invalid;
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
                    this.openFileThread = new ThreadOpenFilePNG(this.mc, title, this);
                    this.openFileThread.start();
                    guiButton.enabled = false;
                }

                if (guiButton.id == this.btnUpload.id) {
                    if (this.selectedSkin != null) {
                        this.uploadSkin(this.mc.getSession(), this.selectedSkin);
                        this.btnUpload.enabled = false;
                    } else {
                        this.setUploadError(select);
                    }
                }

                if (guiButton.id == this.btnClear.id && this.remotePlayer.isTextureSetupComplete()) {
                    this.clearUploadedSkin(this.mc.getSession());
                    this.btnUpload.enabled = this.selectedSkin != null;
                }

                if (guiButton.id == this.btnBack.id) {
                    this.mc.displayGuiScreen(new GuiMainMenu());
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
                this.localPlayer.swingArm();
                this.remotePlayer.swingArm();
            }

        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        if (this.openFileThread == null && !this.uploadingSkin) {
            super.keyTyped(keyChar, keyCode);
        }
    }

    public void setupCubemapCamera() {
        matrixMode(GL11.GL_PROJECTION);
        pushMatrix();
        loadIdentity();
        GLU.gluPerspective(150.0F, 1.0F, 0.05F, 10.0F);
        matrixMode(GL11.GL_MODELVIEW);
        pushMatrix();
        loadIdentity();
    }

    public void revertPanoramaMatrix() {
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
        VertexBuffer vb = tessellator.getBuffer();

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
        VertexBuffer vb = tessellator.getBuffer();
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

    public void renderPanorama(float partialTicks) {
        viewport(0, 0, 256, 256);
        this.renderCubeMapTexture(partialTicks);
        disableTexture2D();
        enableTexture2D();

        for (int tessellator = 0; tessellator < 8; ++tessellator) {
            this.rotateAndBlurCubemap();
        }

        viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
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
        this.enableClipping(30, bottom);

        float yPos = this.height * 0.75F;
        float xPos1 = this.width * 0.25F;
        float xPos2 = this.width * 0.75F;
        float scale = this.height * 0.25F;

        mc.getTextureManager().bindTexture(this.localPlayer.getSkinTexture());
        this.renderPlayerModel(this.localPlayer, xPos1, yPos, scale, yPos - scale * 1.8F - mouseY, partialTick);

        mc.getTextureManager().bindTexture(this.remotePlayer.getSkinTexture());
        this.renderPlayerModel(this.remotePlayer, xPos2, yPos, scale, yPos - scale * 1.8F - mouseY, partialTick);

        this.disableClipping();

        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 10, 0xffffff);

        this.fontRendererObj.drawStringWithShadow(localSkin, 34, 34, 0xffffff);
        this.fontRendererObj.drawStringWithShadow(serverSkin, this.width / 2 + 34, 34, 0xffffff);

        disableDepth();
        enableBlend();
        depthMask(false);

        // this is here so the next few things get blended properly
        Gui.drawRect(0, 0, 1, 1, 0);
        this.drawGradientRect(30, this.height - 60, mid - 30, bottom, 1, 0xe0ffffff);
        this.drawGradientRect(mid + 30, this.height - 60, this.width - 30, bottom, 0, 0xE0FFFFFF);

        int labelwidth = (this.width / 2 - 80) / 2;
        if (!this.localPlayer.isUsingLocalTexture()) {
            int opacity = this.fontRendererObj.getStringWidth(this.skinMessage) / 2;
            Gui.drawRect(40, this.height / 2 - 12, this.width / 2 - 40, this.height / 2 + 12, 0xB0000000);
            this.fontRendererObj.drawStringWithShadow(this.skinMessage, (int) (xPos1 - opacity), this.height / 2 - 4, 0xffffff);
        }

        if (this.fetchingSkin) {
            String opacity1;
            if (this.throttledByMojang) {
                opacity1 = TextFormatting.RED + mojang;
                String stringWidth = wait;
                int stringWidth1 = this.fontRendererObj.getStringWidth(opacity1) / 2;
                int stringWidth2 = this.fontRendererObj.getStringWidth(stringWidth) / 2;

                Gui.drawRect((int) (xPos2 - labelwidth), this.height / 2 - 16, this.width - 40, this.height / 2 + 16, 0xB0000000);

                this.fontRendererObj.drawStringWithShadow(opacity1, (int) (xPos2 - stringWidth1), this.height / 2 - 10, 0xffffff);
                this.fontRendererObj.drawStringWithShadow(stringWidth, (int) (xPos2 - stringWidth2), this.height / 2 + 2, 0xffffff);
            } else {
                opacity1 = fetch;
                int stringWidth1 = this.fontRendererObj.getStringWidth(opacity1) / 2;
                Gui.drawRect((int) (xPos2 - labelwidth), this.height / 2 - 12, this.width - 40, this.height / 2 + 12, 0xB0000000);
                this.fontRendererObj.drawStringWithShadow(opacity1, (int) (xPos2 - stringWidth1), this.height / 2 - 4, 0xffffff);
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
                    this.drawCenteredString(this.fontRendererObj, this.skinUploadMessage, this.width / 2, this.height / 2, opacity << 24 | 0xffffff);
                }
            }
        }

        if (this.uploadError != null) {
            Gui.drawRect(0, 0, this.width, this.height, 0xB0000000);
            this.drawCenteredString(this.fontRendererObj, failed, this.width / 2, this.height / 2 - 10, 0xFFFFFF55);
            this.drawCenteredString(this.fontRendererObj, this.uploadError, this.width / 2, this.height / 2 + 2, 0xFFFF5555);
        }

        depthMask(true);
        enableDepth();
    }

    public void renderPlayerModel(EntityPlayerModel thePlayer, float xPosition, float yPosition, float scale, float mouseY, float partialTick) {
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
        rm.doRenderEntity(thePlayer, 0, 0, 0, 0, 1, false);

        popMatrix();
        RenderHelper.disableStandardItemLighting();
        disableColorMaterial();
    }

    protected final void enableClipping(int yTop, int yBottom) {
        if (this.doubleBuffer == null) {
            this.doubleBuffer = BufferUtils.createByteBuffer(32).asDoubleBuffer();
        }

        this.doubleBuffer.clear();
        this.doubleBuffer.put(0.0D).put(1.0D).put(0.0D).put((-yTop)).flip();

        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, this.doubleBuffer);
        this.doubleBuffer.clear();
        this.doubleBuffer.put(0.0D).put(-1.0D).put(0.0D).put(yBottom).flip();

        GL11.glClipPlane(GL11.GL_CLIP_PLANE1, this.doubleBuffer);
        GL11.glEnable(GL11.GL_CLIP_PLANE0);
        GL11.glEnable(GL11.GL_CLIP_PLANE1);
    }

    protected final void disableClipping() {
        GL11.glDisable(GL11.GL_CLIP_PLANE1);
        GL11.glDisable(GL11.GL_CLIP_PLANE0);
    }

    public static boolean isPowerOfTwo(int number) {
        return number != 0 && (number & number - 1) == 0;
    }

    private void clearUploadedSkin(Session session) {
        if (this.registerServerConnection(session, skinServerId)) {
            Map<String, ?> sourceData = getClearData(session);
            this.uploadError = null;
            this.uploadingSkin = true;
            this.skinUploadMessage = request;
            this.threadSkinUpload = new ThreadMultipartPostUpload(HDSkinManager.INSTANCE.getGatewayUrl(), sourceData, this);
            this.threadSkinUpload.start();
        }
    }

    private void uploadSkin(Session session, File skinFile) {
        if (this.registerServerConnection(session, skinServerId)) {
            Map<String, ?> sourceData = getUploadData(session, skinFile);
            this.uploadError = null;
            this.uploadingSkin = true;
            this.skinUploadMessage = upload;
            this.threadSkinUpload = new ThreadMultipartPostUpload(HDSkinManager.INSTANCE.getGatewayUrl(), sourceData, this);
            this.threadSkinUpload.start();
        }
    }

    private Map<String, ?> getData(Session session, String param, Object val) {
        return ImmutableMap.of(
                "user", session.getUsername(),
                "uuid", session.getPlayerID(),
                param, val);
    }

    private Map<String, ?> getClearData(Session session) {
        return getData(session, "clear", "1");
    }

    private Map<String, ?> getUploadData(Session session, File skinFile) {
        return getData(session, "skin", skinFile);
    }

    private void setUploadError(String error) {
        this.uploadError = error.startsWith("ERROR: ") ? error.substring(7) : error;
        this.btnUpload.enabled = true;
    }

    @Override
    public void onUploadComplete(String response) {
        LiteLoaderLogger.info("Upload completed with: %s", response);
        this.uploadingSkin = false;
        this.threadSkinUpload = null;
        if (!response.equalsIgnoreCase("OK")) {
            this.setUploadError(response);
        } else {
            this.pendingRemoteSkinRefresh = true;
        }
    }

    private boolean registerServerConnection(Session session, String serverId) {
        try {
            MinecraftSessionService service = Minecraft.getMinecraft().getSessionService();
            service.joinServer(session.getProfile(), session.getToken(), serverId);
            return true;
        } catch (AuthenticationException var4) {
            this.setUploadError(var4.toString());
            var4.printStackTrace();
            return false;
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
