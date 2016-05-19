package com.voxelmodpack.hdskins.gui.color;

import static net.minecraft.client.renderer.GlStateManager.*;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

/**
 * Colour picker flyout control, for use with the designable GUI properties
 * window
 *
 * @author Adam Mummery-Smith
 */
public class GuiColorPicker extends GuiControl {
    public static final ResourceLocation COLOURPICKER_CHECKER = new ResourceLocation("hdskins", "textures/gui/checker.png");
    public static final ResourceLocation COLOURPICKER_PICKER = new ResourceLocation("hdskins", "textures/gui/picker.png");

    /**
     * Indices into the hsb array
     */
    private static final int H = 0, S = 1, B = 2;

    /**
     * HSB values from Colour.RGBtoHSB, combined with opacity this is the
     * authoritative version of the colour we are editing
     */
    private float[] hsb;

    /**
     * Original and altered RGB values
     */
    private int rgb;

    /**
     * Current opacity, stored as an offset byte in the usual position, eg. 0xFF
     * << 24
     */
    private int opacity;

    /**
     * Text boxes for manual entry
     */
    private GuiTextField txtRed, txtGreen, txtBlue, txtAlpha;

    /**
     * OK and cancel buttons
     */
    private GuiControl btnOk, btnCancel;

    /**
     * Flags to track whether dragging a slider
     */
    private boolean draggingHS, draggingB, draggingA;

    /**
     * Slider rects
     */
    private Rectangle rectHSArea, rectBArea, rectAArea;

    /**
     * Set when the user clicks ok or cancel
     */
    private DialogResult result = DialogResult.None;

    private FontRenderer fontRenderer;

    public GuiColorPicker(Minecraft minecraft, int controlId, int xPos, int yPos, int initialColour, String displayText) {
        super(minecraft, controlId, xPos, yPos, 231, 173, displayText);

        Color colour = new Color(initialColour);
        this.hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
        this.opacity = initialColour & 0xFF000000;
        if (this.opacity == 0x01000000)
            this.opacity = 0;

        this.fontRenderer = minecraft.fontRendererObj;
        this.txtRed = new GuiTextField(0, this.fontRenderer, this.xPosition + 188, this.yPosition + 10, 32, 16);
        this.txtGreen = new GuiTextField(1, this.fontRenderer, this.xPosition + 188, this.yPosition + 30, 32, 16);
        this.txtBlue = new GuiTextField(2, this.fontRenderer, this.xPosition + 188, this.yPosition + 50, 32, 16);
        this.txtAlpha = new GuiTextField(3, this.fontRenderer, this.xPosition + 188, this.yPosition + 70, 32, 16);

        this.txtRed.setMaxStringLength(3);
        this.txtGreen.setMaxStringLength(3);
        this.txtBlue.setMaxStringLength(3);
        this.txtAlpha.setMaxStringLength(3);

        this.rectHSArea = new Rectangle(this.xPosition + 10, this.yPosition + 10, 128, 128);
        this.rectBArea = new Rectangle(this.xPosition + 143, this.yPosition + 10, 15, 128);
        this.rectAArea = new Rectangle(this.xPosition + 163, this.yPosition + 10, 15, 128);

        this.btnOk = new GuiControl(minecraft, 0, this.xPosition + 9, this.yPosition + 145, 55, 20, I18n.format("gui.ok"));
        this.btnCancel = new GuiControl(minecraft, 1, this.xPosition + 70, this.yPosition + 145, 65, 20, I18n.format("gui.cancel"));

        this.updateColour();
    }

    public DialogResult getDialogResult() {
        return this.result;
    }

    public int getColour() {
        int opacity = this.opacity == 0 ? 0x01000000 : this.opacity;
        int rgb = opacity | (0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B]));
        return rgb;
    }

    @Override
    protected void drawControl(Minecraft minecraft, int mouseX, int mouseY) {
        this.mouseDragged(minecraft, mouseX, mouseY);

        // Calculate coordinates for the selectors
        int hPos = this.xPosition + 10 + (int) (128F * this.hsb[H]);
        int sPos = this.yPosition + 10 + (128 - (int) (128F * this.hsb[S]));
        int bPos = this.yPosition + 10 + (128 - (int) (128F * this.hsb[B]));
        int aPos = this.yPosition + 10 + ((256 - ((this.opacity >> 24) & 0xFF)) / 2);

        // Calculate B colour
        int brightness = Color.HSBtoRGB(this.hsb[H], this.hsb[S], 1.0F) | 0xFF000000;

        // Draw backgrounds
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0xAA000000); // Background
        drawRect(this.xPosition + 9, this.yPosition + 9, this.xPosition + 139, this.yPosition + 139, 0xFFA0A0A0); // HS
                                                                                                                  // background
        drawRect(this.xPosition + 142, this.yPosition + 9, this.xPosition + 159, this.yPosition + 139, 0xFFA0A0A0); // B
                                                                                                                    // background
        drawRect(this.xPosition + 162, this.yPosition + 9, this.xPosition + 179, this.yPosition + 139, 0xFFA0A0A0); // A
                                                                                                                    // background
        drawRect(this.xPosition + 187, this.yPosition + 105, this.xPosition + 221, this.yPosition + 139, 0xFFA0A0A0); // Preview
                                                                                                                      // background

        // Draw colour picker
        this.mc.getTextureManager().bindTexture(GuiColorPicker.COLOURPICKER_PICKER);
        color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.xPosition + 10, this.yPosition + 10, this.xPosition + 138, this.yPosition + 138, 0, 0, 256, 256);
        this.drawCrossHair(hPos, sPos, 5, 1, 0xFF000000);

        // Draw brightness bar
        this.drawGradientRect(this.xPosition + 143, this.yPosition + 10, this.xPosition + 158, this.yPosition + 138, brightness, 0xFF000000);
        this.drawRotText(this.fontRenderer, "Luminosity", this.xPosition + 150, this.yPosition + 74, 0xFF000000, false);
        drawRect(this.xPosition + 142, bPos - 1, this.xPosition + 159, bPos + 1, 0xFFFFFFFF);

        // Draw opacity bar
        this.drawGradientRect(this.xPosition + 163, this.yPosition + 10, this.xPosition + 178, this.yPosition + 138, 0xFFFFFFFF, 0xFF000000);
        this.drawRotText(this.fontRenderer, "Opacity", this.xPosition + 170, this.yPosition + 74, 0xFF000000, false);
        drawRect(this.xPosition + 162, aPos - 1, this.xPosition + 179, aPos + 1, 0xFFFFFFFF);

        // Draw preview
        this.mc.getTextureManager().bindTexture(GuiColorPicker.COLOURPICKER_CHECKER);
        color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.xPosition + 188, this.yPosition + 106, this.xPosition + 220, this.yPosition + 138, 0, 0, 1024, 1024);
        drawRect(this.xPosition + 188, this.yPosition + 106, this.xPosition + 220, this.yPosition + 138, this.rgb);

        // Draw text boxes
        this.txtRed.drawTextBox();
        this.txtGreen.drawTextBox();
        this.txtBlue.drawTextBox();
        this.txtAlpha.drawTextBox();

        this.btnOk.drawButton(minecraft, mouseX, mouseY);
        this.btnCancel.drawButton(minecraft, mouseX, mouseY);
    }

    public void updateCursorCounter() {
        this.txtRed.updateCursorCounter();
        this.txtGreen.updateCursorCounter();
        this.txtBlue.updateCursorCounter();
        this.txtAlpha.updateCursorCounter();
    }

    protected void updateColour() {
        this.rgb = this.opacity | (0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B]));
        this.txtRed.setText(String.valueOf((this.rgb >> 16) & 0xFF));
        this.txtGreen.setText(String.valueOf((this.rgb >> 8) & 0xFF));
        this.txtBlue.setText(String.valueOf(this.rgb & 0xFF));
        this.txtAlpha.setText(String.valueOf((this.opacity >> 24) & 0xFF));
    }

    protected void updateColourFromTextEntry() {
        int currentRed = (this.rgb >> 16) & 0xFF;
        int currentGreen = (this.rgb >> 8) & 0xFF;
        int currentBlue = this.rgb & 0xFF;
        int currentOpacity = (this.opacity >> 24) & 0xFF;

        currentRed = (int) clamp(this.tryParseInt(this.txtRed.getText(), currentRed), 0, 255);
        currentGreen = (int) clamp(this.tryParseInt(this.txtGreen.getText(), currentGreen), 0, 255);
        currentBlue = (int) clamp(this.tryParseInt(this.txtBlue.getText(), currentBlue), 0, 255);
        currentOpacity = (int) clamp(this.tryParseInt(this.txtAlpha.getText(), currentOpacity), 0, 255);

        this.hsb = Color.RGBtoHSB(currentRed, currentGreen, currentBlue, null);
        this.opacity = (currentOpacity << 24) & 0xFF000000;
        this.updateColour();
    }

    protected int tryParseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return "".equals(text) ? 0 : defaultValue;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * net.minecraft.src.GuiButton#mouseDragged(net.minecraft.src.Minecraft,
     * int, int)
     */
    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
        super.mouseDragged(minecraft, mouseX, mouseY);

        if (this.draggingHS) {
            this.hsb[H] = clamp(mouseX - this.xPosition - 10, 0, 128) / 128F;
            this.hsb[S] = (128F - clamp(mouseY - this.yPosition - 10, 0, 128)) / 128F;
            this.updateColour();
        }

        if (this.draggingB) {
            this.hsb[B] = (128F - clamp(mouseY - this.yPosition - 10, 0, 128)) / 128F;
            this.updateColour();
        }

        if (this.draggingA) {
            this.opacity = (mouseY - this.yPosition < 11) ? 0xFF000000 : (((128 - (int) clamp(mouseY - this.yPosition - 10, 0, 128)) << 25) & 0xFF000000);
            this.updateColour();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * net.minecraft.src.GuiButton#mousePressed(net.minecraft.src.Minecraft,
     * int, int)
     */
    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(minecraft, mouseX, mouseY)) {

            if (this.btnOk.mousePressed(minecraft, mouseX, mouseY))
                this.result = DialogResult.OK;

            if (this.btnCancel.mousePressed(minecraft, mouseX, mouseY))
                this.result = DialogResult.Cancel;

            if (this.rectHSArea.contains(mouseX, mouseY))
                this.draggingHS = true;

            if (this.rectBArea.contains(mouseX, mouseY))
                this.draggingB = true;

            if (this.rectAArea.contains(mouseX, mouseY))
                this.draggingA = true;

            this.txtRed.mouseClicked(mouseX, mouseY, 0);
            this.txtGreen.mouseClicked(mouseX, mouseY, 0);
            this.txtBlue.mouseClicked(mouseX, mouseY, 0);
            this.txtAlpha.mouseClicked(mouseX, mouseY, 0);

            return true;
        } else if (this.enabled) {
            this.result = DialogResult.Cancel;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.src.GuiButton#mouseReleased(int, int)
     */
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.draggingHS = false;
        this.draggingB = false;
        this.draggingA = false;
    }

    public boolean textBoxKeyTyped(char keyChar, int keyCode) {
        this.txtRed.textboxKeyTyped(keyChar, keyCode);
        this.txtGreen.textboxKeyTyped(keyChar, keyCode);
        this.txtBlue.textboxKeyTyped(keyChar, keyCode);
        this.txtAlpha.textboxKeyTyped(keyChar, keyCode);
        this.updateColourFromTextEntry();

        if (keyCode == Keyboard.KEY_TAB) {
            if (this.txtRed.isFocused()) {
                this.txtRed.setFocused(false);
                this.txtGreen.setFocused(true);
                this.txtBlue.setFocused(false);
                this.txtAlpha.setFocused(false);
            } else if (this.txtGreen.isFocused()) {
                this.txtRed.setFocused(false);
                this.txtGreen.setFocused(false);
                this.txtBlue.setFocused(true);
                this.txtAlpha.setFocused(false);
            } else if (this.txtBlue.isFocused()) {
                this.txtRed.setFocused(false);
                this.txtGreen.setFocused(false);
                this.txtBlue.setFocused(false);
                this.txtAlpha.setFocused(true);
            } else {
                this.txtRed.setFocused(true);
                this.txtGreen.setFocused(false);
                this.txtBlue.setFocused(false);
                this.txtAlpha.setFocused(false);
            }
        }

        return true;
    }

    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}