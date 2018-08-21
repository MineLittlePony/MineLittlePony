package com.voxelmodpack.hdskins;

import net.minecraft.client.Minecraft;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFile;
import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFileFolder;
import com.voxelmodpack.hdskins.upload.awt.ThreadOpenFilePNG;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.UIManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkinChooser {
    public static final int MAX_SKIN_DIMENSION = 1024;

    public static final String ERR_UNREADABLE = "hdskins.error.unreadable";
    public static final String ERR_EXT = "hdskins.error.ext";
    public static final String ERR_OPEN = "hdskins.error.open";
    public static final String ERR_INVALID = "hdskins.error.invalid";

    public static final String MSG_CHOOSE = "hdskins.choose";

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isPowerOfTwo(int number) {
        return number != 0 && (number & number - 1) == 0;
    }

    private ThreadOpenFile openFileThread;

    private final SkinUploader uploader;


    private volatile String status = MSG_CHOOSE;


    public SkinChooser(SkinUploader uploader) {
        this.uploader = uploader;
    }

    public boolean pickingInProgress() {
        return openFileThread != null;
    }

    public String getStatus() {
        return status;
    }

    public void openBrowsePNG(Minecraft mc, String title, Runnable callback) {
        openFileThread = new ThreadOpenFilePNG(mc, title, (fileDialog, dialogResult) -> {
            openFileThread = null;
            if (dialogResult == 0) {
                selectFile(fileDialog.getSelectedFile());
                callback.run();
            } else {
                callback.run();
            }
        });
        openFileThread.start();
    }

    public void openSavePNG(Minecraft mc, String title, Runnable callback) {
        uploader.downloadSkin().thenAccept(response -> {
            openFileThread = new ThreadOpenFileFolder(mc, title, (fileDialog, dialogResult) -> {
                openFileThread = null;
                callback.run();
                if (dialogResult == 0) {
                    File out = fileDialog.getSelectedFile();

                    try {
                        out.createNewFile();

                        FileUtils.copyInputStreamToFile(response.getInputStream(), out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            openFileThread.start();
        });
    }

    public void selectFile(File skinFile) {
        status = evaluateAndSelect(skinFile);
    }

    @Nullable
    private String evaluateAndSelect(File skinFile) {
        if (!skinFile.exists()) {
            return ERR_UNREADABLE;
        }

        if (!FilenameUtils.isExtension(skinFile.getName(), new String[]{"png", "PNG"})) {
            return ERR_EXT;
        }

        try {
            BufferedImage chosenImage = ImageIO.read(skinFile);

            if (chosenImage == null) {
                return ERR_OPEN;
            }

            if (!acceptsSkinDimensions(chosenImage.getWidth(), chosenImage.getHeight())) {
                return ERR_INVALID;
            }

            uploader.setLocalSkin(skinFile);

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ERR_OPEN;
    }

    protected boolean acceptsSkinDimensions(int w, int h) {
        return isPowerOfTwo(w) && w == h * 2 || w == h && w <= MAX_SKIN_DIMENSION && h <= MAX_SKIN_DIMENSION;
    }
}
