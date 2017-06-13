package com.voxelmodpack.hdskins.upload;

@FunctionalInterface
public interface IUploadCompleteCallback {
    void onUploadComplete(String response);
}
