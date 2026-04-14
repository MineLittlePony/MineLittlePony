package com.minelittlepony.api.model.skull;

import net.minecraft.world.level.block.SkullBlock;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import java.util.function.Supplier;

public interface SkullRegistry {
    static SkullRegistry getInstance() {
        return PonySkullRenderer.INSTANCE;
    }

    void register(SkullBlock.Type type, Supplier<Skull> factory);

    @Nullable
    Supplier<Skull> get(SkullBlock.Type type);
}
