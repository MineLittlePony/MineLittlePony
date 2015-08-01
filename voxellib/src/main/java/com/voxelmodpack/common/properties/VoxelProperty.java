package com.voxelmodpack.common.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;

/**
 * Property superclass
 *
 * @author Adam Mummery-Smith
 */
public abstract class VoxelProperty<PropertyType extends IVoxelPropertyProvider> extends Gui {
    /**
     * Font renderer reference
     */
    protected FontRenderer fontRenderer;

    /**
     * Underlying property provider for this property
     */
    protected PropertyType propertyProvider;

    /**
     * Name of the property this control is bound to
     */
    protected String propertyBinding;

    /**
     * Minecraft reference
     */
    protected Minecraft mc = Minecraft.getMinecraft();

    /**
     * Display text (if any) for this property
     */
    protected String displayText;

    /**
     * X position
     */
    protected int xPosition;

    /**
     * Y position
     */
    protected int yPosition;

    /**
     * Cursor flash counter
     */
    protected int cursorCounter;

    /**
     * True if this control is has focus
     */
    protected boolean focused;

    /**
     * True if this control is visible
     */
    protected boolean visible = true;

    /**
     * @param propertyProvider
     * @param binding
     * @param displayText
     * @param xPos
     * @param yPos
     */
    @SuppressWarnings("unchecked")
    public VoxelProperty(IVoxelPropertyProvider propertyProvider, String binding, String displayText, int xPos,
            int yPos) {
        try {
            this.propertyProvider = (PropertyType) propertyProvider;
        } catch (ClassCastException ex) {
            throw new RuntimeException(String.format("Can't create VoxelProperty for binding %s for panel %s", binding,
                    propertyProvider.getClass().getSimpleName()));
        }

        this.fontRenderer = this.mc.fontRendererObj;

        this.propertyBinding = binding;
        this.displayText = displayText;
        this.xPosition = xPos;
        this.yPosition = yPos;
    }

    /**
     *
     */
    public void updateCursorCounter() {
        this.cursorCounter++;
    }

    /**
     * Draw this property
     *
     * @param host
     * @param mouseX
     * @param mouseY
     */
    public abstract void draw(IExtendedGui host, int mouseX, int mouseY);

    /**
     * Handle mouse clicks on the property
     *
     * @param mouseX
     * @param mouseY
     */
    public abstract void mouseClicked(int mouseX, int mouseY);

    /**
     * Handle keystrokes
     *
     * @param keyChar
     * @param keyCode
     */
    public abstract void keyTyped(char keyChar, int keyCode);

    /**
     * Called when the containing GUI is closed
     */
    public void onClosed() {
        // stub
    }

    /**
     * Get whether this property is visible
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Set whether this property is visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return
     */
    public boolean isFocusable() {
        return false;
    }

    /**
     * @return
     */
    public boolean isFocused() {
        return false;
    }

    /**
     * @param focus
     */
    public void setFocused(boolean focus) {}

    /**
     * @param soundHandler
     */
    public void playClickSound(SoundHandler soundHandler) {
        soundHandler.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    @Override
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        super.drawString(fontRendererIn, text, x, y, color);
    }
}
