package com.voxelmodpack.hdskins.upload.awt;

import java.io.File;

public interface IFileCallback {
    void onDialogClosed(File file, int dialogResults);
}
