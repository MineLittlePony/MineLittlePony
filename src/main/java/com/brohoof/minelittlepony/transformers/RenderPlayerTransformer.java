package com.brohoof.minelittlepony.transformers;

import com.mumfrey.liteloader.transformers.ClassOverlayTransformer;

public class RenderPlayerTransformer extends ClassOverlayTransformer {

    private static final String overlayClassName = "com.brohoof.minelittlepony.renderer.RenderPony";

    public RenderPlayerTransformer() {
        super(overlayClassName);
    }
}
