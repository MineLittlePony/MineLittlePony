package com.minelittlepony.api.model.armour;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

import com.mojang.serialization.Lifecycle;

public final class ArmourRegistry {
    private ArmourRegistry() {}
    static final Registry<IArmour<?>> REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier("minelittlepony", "armour")), Lifecycle.stable());

    @SuppressWarnings("unchecked")
    public static <T extends IArmourModel> IArmour<T> getArmour(ItemStack stack, IArmour<T> fallback) {
        return (IArmour<T>)REGISTRY.getOrEmpty(Registries.ITEM.getId(stack.getItem())).orElse(fallback);
    }
}
