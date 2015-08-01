package com.minelittlepony.minelp.transformers;

import com.mumfrey.liteloader.transformers.ClassOverlayTransformer;

public class RenderPlayerTransformer extends ClassOverlayTransformer {

    private static final String overlayClassName = "com.minelittlepony.minelp.renderer.RenderPony";

    public RenderPlayerTransformer() {
        super(overlayClassName);
    }
}
