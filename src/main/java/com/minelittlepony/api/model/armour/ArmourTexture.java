package com.minelittlepony.api.model.armour;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.util.ResourceUtil;

import java.util.stream.Stream;

public record ArmourTexture(EquipmentClientInfo.LayerType layerType, Identifier texture, ArmourVariant variant) {
    private static final Interner<ArmourTexture> INTERNER = Interners.newWeakInterner();

    public boolean validate() {
        return texture != TextureManager.INTENTIONAL_MISSING_TEXTURE && ResourceUtil.textureExists(texture);
    }

    public static ArmourTexture unknown(EquipmentClientInfo.LayerType layerType) {
        return legacy(layerType, TextureManager.INTENTIONAL_MISSING_TEXTURE);
    }

    public static ArmourTexture legacy(EquipmentClientInfo.LayerType layerType, Identifier texture) {
        return INTERNER.intern(new ArmourTexture(layerType, texture, ArmourVariant.LEGACY));
    }

    public static ArmourTexture modern(EquipmentClientInfo.LayerType layerType, Identifier texture) {
        return INTERNER.intern(new ArmourTexture(layerType, texture, ArmourVariant.NORMAL));
    }

    public Stream<ArmourTexture> ponify() {
        if (!PonyConfig.getInstance().disablePonifiedArmour.get()) {
            return Stream.of(modern(layerType, texture().withPath(p -> p.replace(layerType.getSerializedName(), "ponified_" + layerType.getSerializedName()))), this);
        }
        return Stream.of(this);
    }
}
