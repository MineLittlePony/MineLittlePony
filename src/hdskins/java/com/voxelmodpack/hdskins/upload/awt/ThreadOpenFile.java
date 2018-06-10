package com.voxelmodpack.hdskins.upload.awt;

import net.minecraft.client.Minecraft;

import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Base class for "open file" dialog threads
 *
 * @author Adam Mummery-Smith
 */
public abstract class ThreadOpenFile extends Thread {

    protected String dialogTitle;

    /**
     * Delegate to call back when the dialog box is closed
     */
    protected final IOpenFileCallback parentScreen;

    private JFileChooser fileDialog;

    private JFrame parent = null;

    protected ThreadOpenFile(Minecraft minecraft, String dialogTitle, IOpenFileCallback callback)
            throws IllegalStateException {
        if (minecraft.isFullScreen()) {
            throw new IllegalStateException("Cannot open an awt window whilst minecraft is in full screen mode!");
        }

        this.parentScreen = callback;
        this.dialogTitle = dialogTitle;
    }

    @Override
    public void run() {
        if (parent == null) {
            parent = new JFrame("InternalDialog");
            parent.setAlwaysOnTop(true);
        }

        parent.requestFocusInWindow();
        parent.setVisible(false);

        fileDialog = new JFileChooser();
        fileDialog.setDialogTitle(this.dialogTitle);
        fileDialog.setFileFilter(this.getFileFilter());

        int dialogResult = fileDialog.showOpenDialog(parent);

        parent.setVisible(true);
        this.parentScreen.onFileOpenDialogClosed(fileDialog, dialogResult);
    }

    /**
     * Subclasses should override this to return a file filter
     */
    protected abstract FileFilter getFileFilter();
}
