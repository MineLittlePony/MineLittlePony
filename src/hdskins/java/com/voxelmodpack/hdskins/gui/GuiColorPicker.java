package com.voxelmodpack.hdskins.gui;

import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Rectangle;

import org.apache.logging.log4j.core.helpers.Integers;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * Color picker flyout control, for use with the designable GUI properties
 * window. Code originally by Adam Mummery-Smith.
 */
public class GuiColorPicker extends GuiButton {

    public static final ResourceLocation COLORPICKER_PICKER = new ResourceLocation("hdskins",
            "textures/gui/picker.png");

    /** Indices into the HSB array. */
    private static final int H = 0, S = 1, B = 2;

    /** HSB values from Color.RGBtoHSB. */
    private float[] hsb;

    /** Original and altered RGB values. */
    private int rgb;

    /** Text boxes for manual entry. */
    private GuiTextField txtRed, txtGreen, txtBlue;

    // /** OK and cancel buttons. */
    // private GuiControl btnOk, btnCancel;

    /** Flags to track whether dragging a slider. */
    private boolean draggingHS, draggingB;

    /** Slider rectangles. */
    private Rectangle rectHSArea, rectBArea;

    /** Set when the user clicks ok or cancel. */
    // private DialogResult result = DialogResult.None;

    private FontRenderer fontRenderer;

    private Minecraft mc;

    public GuiColorPicker(Minecraft minecraft, int controlId, int xPos, int yPos, int initialColor, String displayText) {
        super(controlId, xPos, yPos, displayText);
        this.mc = minecraft;
        Color color = new Color(initialColor);
        this.hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        this.fontRenderer = minecraft.fontRendererObj;
        this.txtRed = new GuiTextField(0, this.fontRenderer, this.xPosition + 163, this.yPosition + 10, 32, 16);
        this.txtGreen = new GuiTextField(0, this.fontRenderer, this.xPosition + 163, this.yPosition + 30, 32, 16);
        this.txtBlue = new GuiTextField(0, this.fontRenderer, this.xPosition + 163, this.yPosition + 50, 32, 16);

        this.txtRed.setMaxStringLength(3);
        this.txtGreen.setMaxStringLength(3);
        this.txtBlue.setMaxStringLength(3);

        this.rectHSArea = new Rectangle(this.xPosition + 10, this.yPosition + 10, 128, 128);
        this.rectBArea = new Rectangle(this.xPosition + 143, this.yPosition + 10, 15, 128);

        // this.btnOk =
        // new GuiControl(minecraft, 0, this.xPosition + 9, this.yPosition +
        // 145, 55, 20,
        // I18n.format("config.picker.ok"));
        // this.btnCancel =
        // new GuiControl(minecraft, 1, this.xPosition + 70, this.yPosition +
        // 145, 65, 20,
        // I18n.format("config.picker.cancel"));

        this.updateColor();
    }

    // public DialogResult getDialogResult() {
    // return this.result;
    // }

    public int getColor() {
        int rgb = (0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B]));
        return rgb;
    }

    protected void drawColorPicker(Minecraft minecraft, int mouseX, int mouseY) {
        this.mouseDragged(minecraft, mouseX, mouseY);

        // Calculate coordinates for the selectors
        int hPos = this.xPosition + 10 + (int) (128F * this.hsb[H]);
        int sPos = this.yPosition + 10 + (128 - (int) (128F * this.hsb[S]));
        int bPos = this.yPosition + 10 + (128 - (int) (128F * this.hsb[B]));

        // Calculate B color
        int brightness = Color.HSBtoRGB(this.hsb[H], this.hsb[S], 1.0F) | 0xFF000000;

        // Draw backgrounds
        // Background
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0xAA000000);
        // HS background
        drawRect(this.xPosition + 9, this.yPosition + 9, this.xPosition + 139, this.yPosition + 139, 0xFFA0A0A0);
        // B background
        drawRect(this.xPosition + 142, this.yPosition + 9, this.xPosition + 159, this.yPosition + 139, 0xFFA0A0A0);
        // Preview background
        drawRect(this.xPosition + 162, this.yPosition + 105, this.xPosition + 196, this.yPosition + 139, 0xFFA0A0A0);

        // Draw color picker
        this.mc.getTextureManager().bindTexture(GuiColorPicker.COLORPICKER_PICKER);
        color(1.0F, 1.0F, 1.0F, 1.0F);
        drawScaledCustomSizeModalRect(this.xPosition + 10, this.yPosition + 10, 0, 0, 256, 256, 138, 138, 256, 256);
        this.drawCrossHair(hPos, sPos, 5, 1, 0xFF000000);

        // Draw brightness bar
        this.drawGradientRect(this.xPosition + 143, this.yPosition + 10, this.xPosition + 158, this.yPosition + 138, brightness, 0xFF000000);
        this.drawRotText(this.fontRenderer, "Luminosity", this.xPosition + 150, this.yPosition + 74, 0xFF000000, false);
        drawRect(this.xPosition + 142, bPos - 1, this.xPosition + 159, bPos + 1, 0xFFFFFFFF);

        // Draw preview
        drawRect(this.xPosition + 163, this.yPosition + 106, this.xPosition + 195, this.yPosition + 138, 0xFF000000 | this.rgb);

        // Draw text boxes
        this.txtRed.drawTextBox();
        this.txtGreen.drawTextBox();
        this.txtBlue.drawTextBox();

        // this.btnOk.drawButton(minecraft, mouseX, mouseY);
        // this.btnCancel.drawButton(minecraft, mouseX, mouseY);
    }

    private void drawCrossHair(int x, int y, int size, int width, int color) {
        float alpha = (color >> 24 & 0xff) / 255F;
        float red = (color >> 16 & 0xff) / 255F;
        float green = (color >> 8 & 0xff) / 255F;
        float blue = (color & 0xff) / 255F;

        GlStateManager.glLineWidth(mc.gameSettings.guiScale * width);
        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableBlend();
        disableTexture2D();
        disableLighting();
        color(red, green, blue, alpha);
        enableColorLogic();
        colorLogicOp(LogicOp.OR_REVERSE);

        // Draw the frame
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();

        vb.begin(GL_LINES, DefaultVertexFormats.POSITION);
        vb.pos(x - size, y, 0).endVertex();
        vb.pos(x + size, y, 0).endVertex();
        tessellator.draw();

        vb.begin(GL_LINES, DefaultVertexFormats.POSITION);
        vb.pos(x, y - size, 0).endVertex();
        vb.pos(x, y + size, 0).endVertex();
        tessellator.draw();

        disableColorLogic();
        enableTexture2D();
    }

    private void drawRotText(FontRenderer fontRenderer, String text, int xPosition, int yPosition,
            int color, boolean colorOrOp) {
        if (colorOrOp) {
            enableColorLogic();
            colorLogicOp(GL_OR_REVERSE);
        }

        int textWidth = fontRenderer.getStringWidth(text) / 2;

        pushMatrix();
        translate(xPosition, yPosition, 0);
        rotate(-90, 0, 0, 1);
        translate(-textWidth, -4, 0);

        fontRenderer.drawString(text, 0, 0, color);

        popMatrix();

        if (colorOrOp) {
            disableColorLogic();
            enableTexture2D();
        }
    }

    public void updateCursorCounter() {
        this.txtRed.updateCursorCounter();
        this.txtGreen.updateCursorCounter();
        this.txtBlue.updateCursorCounter();
    }

    protected void updateColor() {
        this.rgb = (0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B]));
        this.txtRed.setText(String.valueOf((this.rgb >> 16) & 0xFF));
        this.txtGreen.setText(String.valueOf((this.rgb >> 8) & 0xFF));
        this.txtBlue.setText(String.valueOf(this.rgb & 0xFF));
    }

    protected void updateColorFromTextEntry() {
        int currentRed = (this.rgb >> 16) & 0xFF;
        int currentGreen = (this.rgb >> 8) & 0xFF;
        int currentBlue = this.rgb & 0xFF;

        currentRed = (int) clamp(tryParseInt(this.txtRed.getText(), currentRed), 0, 255);
        currentGreen = (int) clamp(tryParseInt(this.txtGreen.getText(), currentGreen), 0, 255);
        currentBlue = (int) clamp(tryParseInt(this.txtBlue.getText(), currentBlue), 0, 255);

        this.hsb = Color.RGBtoHSB(currentRed, currentGreen, currentBlue, null);
        this.updateColor();
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
        super.mouseDragged(minecraft, mouseX, mouseY);

        if (this.draggingHS) {
            this.hsb[H] = clamp(mouseX - this.xPosition - 10, 0, 128) / 128F;
            this.hsb[S] = (128F - clamp(mouseY - this.yPosition - 10, 0, 128)) / 128F;
            this.updateColor();
        }

        if (this.draggingB) {
            this.hsb[B] = (128F - clamp(mouseY - this.yPosition - 10, 0, 128)) / 128F;
            this.updateColor();
        }

    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(minecraft, mouseX, mouseY)) {

            // if (this.btnOk.mousePressed(minecraft, mouseX, mouseY))
            // this.result = DialogResult.OK;
            //
            // if (this.btnCancel.mousePressed(minecraft, mouseX, mouseY))
            // this.result = DialogResult.Cancel;

            if (this.rectHSArea.contains(mouseX, mouseY))
                this.draggingHS = true;

            if (this.rectBArea.contains(mouseX, mouseY))
                this.draggingB = true;

            this.txtRed.mouseClicked(mouseX, mouseY, 0);
            this.txtGreen.mouseClicked(mouseX, mouseY, 0);
            this.txtBlue.mouseClicked(mouseX, mouseY, 0);

            return true;
        }

        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.draggingHS = false;
        this.draggingB = false;
    }

    public boolean textBoxKeyTyped(char keyChar, int keyCode) {
        this.txtRed.textboxKeyTyped(keyChar, keyCode);
        this.txtGreen.textboxKeyTyped(keyChar, keyCode);
        this.txtBlue.textboxKeyTyped(keyChar, keyCode);
        this.updateColorFromTextEntry();

        if (keyCode == Keyboard.KEY_TAB) {
            if (this.txtRed.isFocused()) {
                this.txtRed.setFocused(false);
                this.txtGreen.setFocused(true);
                this.txtBlue.setFocused(false);
            } else if (this.txtGreen.isFocused()) {
                this.txtRed.setFocused(false);
                this.txtGreen.setFocused(false);
                this.txtBlue.setFocused(true);
            } else if (this.txtBlue.isFocused()) {
                this.txtRed.setFocused(false);
                this.txtGreen.setFocused(false);
                this.txtBlue.setFocused(false);
            } else {
                this.txtRed.setFocused(true);
                this.txtGreen.setFocused(false);
                this.txtBlue.setFocused(false);
            }
        }

        return true;
    }

    private static int tryParseInt(String text, int defaultValue) {
        return "".equals(text) ? 0 : Integers.parseInt(text, defaultValue);
    }

    private static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}