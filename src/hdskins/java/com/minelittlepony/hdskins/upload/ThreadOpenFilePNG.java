package com.minelittlepony.hdskins.upload;

import net.minecraft.client.Minecraft;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Opens an awt "Open File" dialog with a PNG file filter
 *
 * @author Adam Mummery-Smith
 */
public class ThreadOpenFilePNG extends ThreadOpenFile {

    public ThreadOpenFilePNG(Minecraft minecraft, String dialogTitle, IFileCallback callback) throws IllegalStateException {
        super(minecraft, dialogTitle, callback);
    }

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
