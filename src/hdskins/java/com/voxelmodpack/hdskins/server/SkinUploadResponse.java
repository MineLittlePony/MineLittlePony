package com.voxelmodpack.hdskins.server;

import com.google.common.base.MoreObjects;

public class SkinUploadResponse {

    private final String message;

    public SkinUploadResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .toString();
    }
}
