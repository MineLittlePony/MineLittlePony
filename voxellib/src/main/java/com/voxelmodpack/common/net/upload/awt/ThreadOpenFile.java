package com.voxelmodpack.common.net.upload.awt;

import java.awt.Frame;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.minecraft.client.Minecraft;

/**
 * Base class for "open file" dialog threads
 * 
 * @author Adam Mummery-Smith
 */
public abstract class ThreadOpenFile extends Thread {
    /**
     * Minecraft's AWT parent frame
     */
    protected Frame parentFrame;

    protected String dialogTitle;

    /**
     * Delegate to call back when the dialog box is closed
     */
    protected final IOpenFileCallback parentScreen;

    /**
     * @param minecraft
     * @param callback
     */
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
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle(this.dialogTitle);
        fileDialog.setFileFilter(this.getFileFilter());

        int dialogResult = fileDialog.showOpenDialog(this.parentFrame);

        this.parentScreen.onFileOpenDialogClosed(fileDialog, dialogResult);
    }

    /**
     * Subclasses should override this to return a file filter
     * 
     * @return
     */
    protected abstract FileFilter getFileFilter();
}