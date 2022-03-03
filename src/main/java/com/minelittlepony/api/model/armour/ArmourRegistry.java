package com.minelittlepony.api.model.armour;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import com.mojang.serialization.Lifecycle;

public final class ArmourRegistry {
    private ArmourRegistry() {}
    static final Registry<IArmour<?>> REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier("minelittlepony", "armour")), Lifecycle.stable(), null);

    @SuppressWarnings("unchecked")
    public static <T extends IArmourModel> IArmour<T> getArmour(ItemStack stack, IArmour<T> fallback) {
        return (IArmour<T>)REGISTRY.getOrEmpty(Registry.ITEM.getId(stack.getItem())).orElse(fallback);
    }
}
