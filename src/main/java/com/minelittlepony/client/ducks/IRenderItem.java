package com.minelittlepony.client.ducks;

public interface IRenderItem {

    /**
     * Sets whether items should be rendered with transparency support.
     */
    void useTransparency(boolean use);

    /**
     * Returns true if this renderer is set to render models as a transparent overlay.
     */
    boolean usesTransparency();
}
