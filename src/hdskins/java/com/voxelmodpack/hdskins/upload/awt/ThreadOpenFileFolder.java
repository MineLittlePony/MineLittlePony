package com.voxelmodpack.hdskins.upload.awt;

import net.minecraft.client.Minecraft;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Opens an awt "Open File" dialog that only accepts directories.
 */
public class ThreadOpenFileFolder extends ThreadOpenFile {

    public ThreadOpenFileFolder(Minecraft minecraft, String dialogTitle, IOpenFileCallback callback)
            throws IllegalStateException {
        super(minecraft, dialogTitle, callback);
    }

    @Override
    protected FileFilter getFileFilter() {
        return new FileFilter() {
            @Override
            public String getDescription() {
                return "Directories";
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
        };
    }
}
