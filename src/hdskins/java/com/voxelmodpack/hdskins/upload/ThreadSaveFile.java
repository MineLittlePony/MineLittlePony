package com.voxelmodpack.hdskins.upload;

import net.minecraft.client.Minecraft;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.io.File;

/**
 * Opens an awt "Save File" dialog
 */
public abstract class ThreadSaveFile extends ThreadOpenFile {

    protected String filename;

    protected ThreadSaveFile(Minecraft minecraft, String dialogTitle, String initialFilename, IFileCallback callback) throws IllegalStateException {
        super(minecraft, dialogTitle, callback);
        this.filename = initialFilename;
    }

    @Override
    protected int showDialog(JFileChooser chooser) {


        do {
            chooser.setSelectedFile(new File(filename));

            int result = chooser.showSaveDialog(InternalDialog.getAWTContext());

            File f = chooser.getSelectedFile();
            if (result == 0 && f != null && f.exists()) {

                if (JOptionPane.showConfirmDialog(chooser,
                        "A file named \"" + f.getName() + "\" already exists. Do you want to replace it?", "Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                    continue;
                }

                f.delete();
            }


            return result;
        } while (true);
    }
}
