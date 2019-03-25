package com.minelittlepony.hdskins.upload;

import java.io.File;

@FunctionalInterface
public interface IFileCallback {
    void onDialogClosed(File file, int dialogResults);
}
