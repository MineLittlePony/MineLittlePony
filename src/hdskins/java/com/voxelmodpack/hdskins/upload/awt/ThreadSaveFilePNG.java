package com.voxelmodpack.hdskins.upload.awt;

import net.minecraft.client.Minecraft;

import javax.swing.filechooser.FileFilter;

import java.io.File;

public class ThreadSaveFilePNG extends ThreadSaveFile {

    public ThreadSaveFilePNG(Minecraft minecraft, String dialogTitle, String filename, IFileCallback callback) {
        super(minecraft, dialogTitle, filename, callback);
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

    @Override
    protected File appendMissingExtension(File file) {
        return new File(file.getParentFile(), file.getName() + ".png");
    }
}
