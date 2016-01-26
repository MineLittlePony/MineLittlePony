package com.voxelmodpack.hdskins.upload.awt;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.minecraft.client.Minecraft;

/**
 * Opens an awt "Open File" dialog with a PNG file filter
 * 
 * @author Adam Mummery-Smith
 */
public class ThreadOpenFilePNG extends ThreadOpenFile {
    /**
     * @param minecraft
     * @param dialogTitle
     * @param callback
     * @throws IllegalStateException
     */
    public ThreadOpenFilePNG(Minecraft minecraft, String dialogTitle, IOpenFileCallback callback)
            throws IllegalStateException {
        super(minecraft, dialogTitle, callback);
    }

    /**
     * @return
     */
    @Override
    protected FileFilter getFileFilter() {
        return new FileFilter() {
            @Override
            public String getDescription() {
                return "PNG Files (*.png)";
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
            }
        };
    }
}
