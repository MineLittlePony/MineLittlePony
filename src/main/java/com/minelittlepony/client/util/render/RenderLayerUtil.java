package com.minelittlepony.client.util.render;

import java.util.Optional;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

public interface RenderLayerUtil {
    static Optional<Identifier> getTexture(RenderLayer layer) {
        if (layer instanceof RenderLayer.MultiPhase multiphase) {
            return multiphase.getPhases().texture.getId();
        }
        return Optional.empty();
    }
}
