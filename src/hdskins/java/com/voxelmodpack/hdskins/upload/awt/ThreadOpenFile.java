package com.voxelmodpack.hdskins.upload.awt;

import net.minecraft.client.Minecraft;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

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

    private static String lastChosenFile = null;

    protected ThreadOpenFile(Minecraft minecraft, String dialogTitle, IOpenFileCallback callback)
            throws IllegalStateException {
        if (minecraft.isFullScreen()) {
            throw new IllegalStateException("Cannot open an awt window whilst minecraft is in full screen mode!");
        }

        this.parentScreen = callback;
        this.dialogTitle = dialogTitle;
    }

    public ThreadOpenFile setParent(JFrame parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public void run() {
        fileDialog = new JFileChooser();
        fileDialog.setDialogTitle(this.dialogTitle);

        if (lastChosenFile != null) {
            fileDialog.setSelectedFile(new File(lastChosenFile));
        }
        fileDialog.setFileFilter(this.getFileFilter());

        int dialogResult = fileDialog.showOpenDialog(parent);

        lastChosenFile = fileDialog.getSelectedFile().getAbsolutePath();

        this.parentScreen.onFileOpenDialogClosed(fileDialog, dialogResult);
    }

    /**
     * Subclasses should override this to return a file filter
     */
    protected abstract FileFilter getFileFilter();
}
