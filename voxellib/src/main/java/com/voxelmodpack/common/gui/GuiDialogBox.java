package com.voxelmodpack.common.gui;

import java.awt.Point;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * Base class for GUI Screens which behave like dialog boxes
 *
 * @author Adam Mummery-Smith
 */
public abstract class GuiDialogBox extends GuiScreenEx {
    public static int lastScreenWidth, lastScreenHeight;

    public enum DialogResult {
        /**
         * No result (maybe the dialog was not closed yet?)
         */
        None,

        /**
         * Dialog result OK (user clicked OK or pressed RETURN)
         */
        OK,

        /**
         * Dialog result Cancel (user clicked Cancel or pressed ESCAPE)
         */
        Cancel,

        Yes,

        No
    }

    private GuiScreen parentScreen;

    /**
     * Parent screen which will be notified when this dialog is closed
     */
    // protected GuiScreenEx parentScreenEx;

    /**
     * Dialog box buttons
     */
    protected GuiButton btnOk, btnCancel;

    /**
     * Dialog box position and size
     */
    protected int dialogX, dialogY, dialogWidth, dialogHeight;

    /**
     * Title to display at the top of the dialog box
     */
    protected String dialogTitle;

    /**
     * True to centre the title
     */
    protected boolean centreTitle = true;

    /**
     * Colour for the window title
     */
    protected int dialogTitleColour = 0xFFFFFF00;

    /**
     * Dialog result based on the user's action
     */
    public DialogResult dialogResult = DialogResult.None;

    /**
     * This dialog box can be moved
     */
    protected boolean movable = false;

    protected boolean dragging = false;

    protected Point dragOffset = new Point(0, 0);

    @SuppressWarnings("unused")
    private boolean generateMouseDragEvents;

    /**
     * Constructor, create a new GuiDialogBox
     *
     * @param parentScreen Screen which owns this dialog
     * @param width
     * @param height
     * @param windowTitle
     */
    public GuiDialogBox(GuiScreen parentScreen, int width, int height, String windowTitle) {
        this.parentScreen = parentScreen;
        this.dialogWidth = width;
        this.dialogHeight = height;
        this.dialogTitle = windowTitle;

        this.generateMouseDragEvents = true;
    }

    /**
     * Close the dialog box and display the parent screen (if it is available,
     * otherwise clear the gui)
     */
    protected void closeDialog() {
        this.mc.displayGuiScreen(this.getParentScreen());
    }

    /**
     * Handle a button event
     *
     * @param guibutton Button or control which sourced the event
     */
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == this.btnCancel.id) {
            this.dialogResult = DialogResult.Cancel;
            this.closeDialog();
        }
        if (guibutton.id == this.btnOk.id) {
            if (this.validateDialog()) {
                this.dialogResult = DialogResult.OK;
                this.onSubmit();
                this.closeDialog();
            }
        }
    }

    /**
     * Handle a keyboard event
     */
    @Override
    protected final void keyTyped(char keyChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.actionPerformed(this.btnCancel);
        } else if (keyCode == Keyboard.KEY_RETURN) {
            this.actionPerformed(this.btnOk);
        } else {
            this.onKeyTyped(keyChar, keyCode);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.eq2online.macros.gui.shared.GuiScreenEx#mouseClicked(int, int,
     * int)
     */
    @Override
    protected final void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (button == 0 && this.movable && mouseX > this.dialogX && mouseX < this.dialogX + this.dialogWidth
                && mouseY > this.dialogY - 18 && mouseY < this.dialogY) {
            this.dragOffset = new Point(mouseX - this.dialogX, mouseY - this.dialogY);
            this.dragging = true;
        } else {
            this.mouseClickedEx(mouseX, mouseY, button);
        }
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param button
     * @throws IOException
     */
    protected void mouseClickedEx(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#mouseReleased(int, int, int)
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        if (this.dragging) {
            this.dialogX = mouseX - this.dragOffset.x;
            this.dialogY = mouseY - this.dragOffset.y;
            this.initGui();
        }

        if (button == 0 && this.dragging) {
            if (this.dialogX < 0)
                this.dialogX = 0;
            if (this.dialogX > this.width)
                this.dialogX = this.width - this.dialogWidth;
            if (this.dialogY < 9)
                this.dialogY = 18;
            if (this.dialogY > this.height)
                this.dialogY = this.height - this.dialogHeight;
            this.initGui();
            this.dragging = false;
            return;
        }

        super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Initialise the dialog box
     */
    @Override
    public final void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        // Call to owner since the owner is still drawn whilst we are being
        // displayed
        if (this.getParentScreen() != null)
            this.getParentScreen().initGui();

        if (!this.dragging) {
            // Draw the dialog centered unless moved by the parent
            this.dialogX = (this.width - this.dialogWidth) / 2;
            this.dialogY = (this.height - this.dialogHeight) / 2;
        }

        this.btnOk = new GuiButton(-1, this.dialogX + this.dialogWidth - 62, this.dialogY + this.dialogHeight - 22, 60,
                20, I18n.format("gui.done"));
        this.btnCancel = new GuiButton(-2, this.dialogX + this.dialogWidth - 124, this.dialogY + this.dialogHeight - 22,
                60, 20, I18n.format("gui.cancel"));

        this.buttonList.clear();
        this.buttonList.add(this.btnOk);
        this.buttonList.add(this.btnCancel);

        lastScreenWidth = this.width;
        lastScreenHeight = this.height;

        this.onInitDialog();
    }

    @Override
    public final void onGuiClosed() {
        this.onDialogClosed();

        Keyboard.enableRepeatEvents(false);

        super.onGuiClosed();
    }

    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
        super.setWorldAndResolution(minecraft, width, height);

        if (this.getParentScreen() != null) {
            this.getParentScreen().setWorldAndResolution(minecraft, width, height);
        }
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawParentScreen(mouseX, mouseY, partialTick);

        int backColour = 0xAA000000;
        int backColour2 = 0xCC333333;

        // Header
        drawRect(this.dialogX, this.dialogY - 18, this.dialogX + this.dialogWidth, this.dialogY, backColour2);

        if (this.centreTitle)
            this.drawCenteredString(this.mc.fontRendererObj, this.dialogTitle, this.dialogX + (this.dialogWidth / 2),
                    this.dialogY - 13, this.dialogTitleColour);
        else
            this.drawString(this.mc.fontRendererObj, this.dialogTitle, this.dialogX + 5, this.dialogY - 13,
                    this.dialogTitleColour);

        // Dialog body
        drawRect(this.dialogX, this.dialogY, this.dialogX + this.dialogWidth, this.dialogY + this.dialogHeight,
                backColour);

        // Subclasses
        this.drawDialog(mouseX, mouseY, partialTick);

        // Superclass (buttons etc.)
        super.drawScreen(mouseX, mouseY, partialTick);

        this.postRender(mouseX, mouseY, partialTick);
    }

    @Override
    public void updateScreen() {
        if (this.getParentScreen() != null) {
            this.getParentScreen().updateScreen();
        }

        super.updateScreen();
    }

    /**
     * @param partialTick
     */
    public void drawParentScreen(int mouseX, int mouseY, float partialTick) {
        if (this.getParentScreen() != null) {
            this.getParentScreen().drawScreen(0, 0, partialTick);
            drawRect(0, 0, this.width, this.height, 0xAA000000);
        } else if (this.mc.theWorld == null) {
            this.drawDefaultBackground();
        }
    }

    /**
     * Stub for subclasses, draw any additional dialog features
     *
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param f
     */
    protected void drawDialog(int mouseX, int mouseY, float f) {

    }

    /**
     * Stub for subclasses, draw any additional dialog features
     *
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param f
     */
    protected void postRender(int mouseX, int mouseY, float f) {

    }

    /**
     * Stub for subclasses, perform any tasks required when the dialog is
     * validated and about to be submitted
     */
    public abstract void onSubmit();

    /**
     * Stub for subclasses, return true if the dialog can be submitted in its
     * current state, or false to prevent submission
     *
     * @return
     */
    public abstract boolean validateDialog();

    /**
     * Stub for subclasses, handle a key typed event
     *
     * @param keyChar
     * @param keyCode
     */
    protected void onKeyTyped(char keyChar, int keyCode) {}

    /**
     * Stub for subclasses, perform any required initialisation
     */
    protected void onInitDialog() {}

    /**
     * Stub for subclasses, the gui was closed
     */
    protected void onDialogClosed() {}

    public GuiScreen getParentScreen() {
        return this.parentScreen;
    }
}
