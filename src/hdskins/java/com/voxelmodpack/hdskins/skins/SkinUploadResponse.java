package com.voxelmodpack.hdskins.skins;

import com.google.common.base.MoreObjects;

public class SkinUploadResponse {

    private final boolean success;
    private final String message;

    public SkinUploadResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("success", success)
                .add("message", message)
                .toString();
    }
}
