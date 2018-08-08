package com.voxelmodpack.hdskins.upload.awt;

import net.minecraft.client.Minecraft;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

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

    private static String lastChosenFile = null;

    protected ThreadOpenFile(Minecraft minecraft, String dialogTitle, IOpenFileCallback callback) throws IllegalStateException {
        if (minecraft.isFullScreen()) {
            throw new IllegalStateException("Cannot open an awt window whilst minecraft is in full screen mode!");
        }

        this.parentScreen = callback;
        this.dialogTitle = dialogTitle;
    }

    @Override
    public void run() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle(dialogTitle);

        if (!StringUtils.isBlank(lastChosenFile)) {
            fileDialog.setSelectedFile(new File(lastChosenFile));
        }
        fileDialog.setFileFilter(getFileFilter());

        int dialogResult = fileDialog.showOpenDialog(InternalDialog.getAWTContext());

        File f = fileDialog.getSelectedFile();

        if (f != null) {
            lastChosenFile = f.getAbsolutePath();
        }

        parentScreen.onFileOpenDialogClosed(fileDialog, dialogResult);
    }

    /**
     * Subclasses should override this to return a file filter
     */
    protected abstract FileFilter getFileFilter();
}
