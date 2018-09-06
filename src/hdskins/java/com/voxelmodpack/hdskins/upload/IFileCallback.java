package com.voxelmodpack.hdskins.upload;

import java.io.File;

@FunctionalInterface
public interface IFileCallback {
    void onDialogClosed(File file, int dialogResults);
}
