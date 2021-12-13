package com.minelittlepony.api.model.armour;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.IModelWrapper;
import com.minelittlepony.api.pony.IPonyData;

/**
 * Wrapper for an armour model and texture.
 *
 * @param <V> The type of the contained armour model.
 */
public interface IArmour<V extends IArmourModel> extends IModelWrapper {
    /**
     * Registers a custom armour for the supplied item.
     *
     * Mods can register their own armour here if they wish to override the default handling.
     *
     * Only one registration per item allowed.
     */
    public static <T extends IArmourModel> IArmour<T> register(IArmour<T> armour, Item item) {
        return Registry.register(ArmourRegistry.REGISTRY, Registry.ITEM.getId(item), armour);
    }

    /**
     * Gets the armour model to render for the given layer.
     * <p>
     * Return null to preserve the default behaviour or override and return your custom model.
     */
    @Nullable
    V getModel(ArmourLayer layer);

    /**
     * Override this to specify your own textures.
     *
     * The default resolver will simply attempt to ponify the vanilla locations.
     */
    default IArmourTextureResolver getTextureResolver(IArmourTextureResolver defaultResolver) {
        return defaultResolver;
    }

    @Override
    default IArmour<V> applyMetadata(IPonyData meta) {
        return this;
    }
}
