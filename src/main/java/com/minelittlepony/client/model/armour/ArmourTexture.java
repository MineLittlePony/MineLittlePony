package com.minelittlepony.client.model.armour;

import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.util.ResourceUtil;

import java.util.stream.Stream;

public record ArmourTexture(Identifier texture, ArmourVariant variant) {
    private static final Interner<ArmourTexture> INTERNER = Interners.newWeakInterner();
    public static final ArmourTexture UNKNOWN = legacy(TextureManager.MISSING_IDENTIFIER);

    public boolean validate() {
        return texture != TextureManager.MISSING_IDENTIFIER && ResourceUtil.textureExists(texture);
    }

    public static ArmourTexture legacy(Identifier texture) {
        return INTERNER.intern(new ArmourTexture(texture, ArmourVariant.LEGACY));
    }

    public static ArmourTexture modern(Identifier texture) {
        return INTERNER.intern(new ArmourTexture(texture, ArmourVariant.NORMAL));
    }

    public Stream<ArmourTexture> ponify() {
        if (!PonyConfig.getInstance().disablePonifiedArmour.get()) {
            return Stream.of(this, modern(texture().withPath(p -> p.replace("humanoid", "ponified"))));
        }
        return Stream.of(this);
    }
}
