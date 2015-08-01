package com.voxelmodpack.common.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * Confirmation dialog box, displays a message and yes/no buttons
 * 
 * @author Adam Mummery-Smith
 * @param <T>
 */
public class GuiDialogBoxConfirm<T> extends GuiDialogBox {
    /**
     * Message text to display
     */
    private String messageText1, messageText2;

    /**
     * Metadata for this confirmation, so that the calling class knows what's
     * being confirmed!
     */
    private T metaData;

    /**
     * Constructor with metadata
     * 
     * @param parentScreen Screen which owns this dialog
     * @param windowTitle Dialog title
     * @param line1 Label line 1
     * @param line2 Label line 2
     * @param metaData Metadata supplied by the owning gui
     */
    public GuiDialogBoxConfirm(GuiScreen parentScreen, String windowTitle, String line1, String line2, T metaData) {
        this(parentScreen, windowTitle, line1, line2);
        this.metaData = metaData;
    }

    /**
     * Constructor with no metadata
     * 
     * @param parentScreen Screen which owns this dialog
     * @param windowTitle Dialog title
     * @param line1 Label line 1
     * @param line2 Label line 2
     */
    public GuiDialogBoxConfirm(GuiScreen parentScreen, String windowTitle, String line1, String line2) {
        super(parentScreen, 320, 80, windowTitle);

        // Set local labels
        this.messageText1 = line1;
        this.messageText2 = line2;
    }

    /**
     * Get the meta data passed to this dialog when it was created
     * 
     * @return Meta data (if any)
     */
    public T GetMetaData() {
        return this.metaData;
    }

    @Override
    protected void onInitDialog() {
        this.btnOk.displayString = I18n.format("gui.yes");
        this.btnCancel.displayString = I18n.format("gui.no");
    }

    @Override
    public void onSubmit() {}

    @Override
    public boolean validateDialog() {
        return true;
    }

    @Override
    protected void drawDialog(int mouseX, int mouseY, float f) {
        // Label
        drawCenteredString(this.fontRendererObj, this.messageText1, this.dialogX + (this.dialogWidth / 2),
                this.dialogY + 18, 0xFFFFAA00);
        drawCenteredString(this.fontRendererObj, this.messageText2, this.dialogX + (this.dialogWidth / 2),
                this.dialogY + 32, 0xFFFFAA00);
    }

    /*
     * (non-Javadoc)
     * @see net.eq2online.macros.gui.shared.GuiDialogBox#KeyTyped(char, int)
     */
    @Override
    protected void onKeyTyped(char keyChar, int keyCode) {
        if (keyChar == 'y' || keyChar == 'Y')
            actionPerformed(this.btnOk);
        if (keyChar == 'n' || keyChar == 'N')
            actionPerformed(this.btnCancel);
    }
}
