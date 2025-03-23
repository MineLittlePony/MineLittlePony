package com.minelittlepony.client.model.armour;

import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.util.ResourceUtil;

import java.util.stream.Stream;

public record ArmourTexture(EquipmentModel.LayerType layerType, Identifier texture, ArmourVariant variant) {
    private static final Interner<ArmourTexture> INTERNER = Interners.newWeakInterner();

    public boolean validate() {
        return texture != TextureManager.MISSING_IDENTIFIER && ResourceUtil.textureExists(texture);
    }

    public static ArmourTexture unknown(EquipmentModel.LayerType layerType) {
        return legacy(layerType, TextureManager.MISSING_IDENTIFIER);
    }

    public static ArmourTexture legacy(EquipmentModel.LayerType layerType, Identifier texture) {
        return INTERNER.intern(new ArmourTexture(layerType, texture, ArmourVariant.LEGACY));
    }

    public static ArmourTexture modern(EquipmentModel.LayerType layerType, Identifier texture) {
        return INTERNER.intern(new ArmourTexture(layerType, texture, ArmourVariant.NORMAL));
    }

    public Stream<ArmourTexture> ponify() {
        if (!PonyConfig.getInstance().disablePonifiedArmour.get()) {
            return Stream.of(modern(layerType, texture().withPath(p -> p.replace(layerType.asString(), "ponified_" + layerType.asString()))), this);
        }
        return Stream.of(this);
    }
}
