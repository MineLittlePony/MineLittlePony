package com.minelittlepony.hdskins.upload;

import net.minecraft.client.Minecraft;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

import com.minelittlepony.hdskins.HDSkins;

/**
 * Base class for "open file" dialog threads
 *
 * @author Adam Mummery-Smith
 */
public abstract class ThreadOpenFile extends Thread implements IFileDialog {

    protected String dialogTitle;

    /**
     * Delegate to call back when the dialog box is closed
     */
    protected final IFileCallback parentScreen;

    protected ThreadOpenFile(Minecraft minecraft, String dialogTitle, IFileCallback callback) throws IllegalStateException {
        if (minecraft.mainWindow.isFullscreen()) {
            throw new IllegalStateException("Cannot open an awt window whilst minecraft is in full screen mode!");
        }

        this.parentScreen = callback;
        this.dialogTitle = dialogTitle;
    }

    @Override
    public void run() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle(dialogTitle);

        String last = HDSkins.getInstance().lastChosenFile;
        if (!StringUtils.isBlank(last)) {
            fileDialog.setSelectedFile(new File(last));
        }
        fileDialog.setFileFilter(getFileFilter());

        int dialogResult = showDialog(fileDialog);

        File f = fileDialog.getSelectedFile();

        if (f != null) {
            HDSkins.getInstance().lastChosenFile = f.getAbsolutePath();
            HDSkins.getInstance().saveConfig();

            if (!f.exists() && f.getName().indexOf('.') == -1) {
                f = appendMissingExtension(f);
            }
        }

        parentScreen.onDialogClosed(f, dialogResult);
    }

    protected int showDialog(JFileChooser chooser) {
        return chooser.showOpenDialog(InternalDialog.getAWTContext());
    }

    /**
     * Subclasses should override this to return a file filter
     */
    protected abstract FileFilter getFileFilter();

    protected File appendMissingExtension(File file) {
        return file;
    }
}
