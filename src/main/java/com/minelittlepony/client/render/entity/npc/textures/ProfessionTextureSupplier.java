package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.*;

import com.minelittlepony.util.ResourceUtil;

public class ProfessionTextureSupplier<T extends VillagerDataHolder> implements TextureSupplier<T> {

    public static <T extends VillagerDataHolder> TextureSupplier<T> create(TextureSupplier<String> formatter) {
        return TextureSupplier.memoize(new ProfessionTextureSupplier<>(formatter), ProfessionTextureSupplier::getKey);
    }

    private final TextureSupplier<String> formatter;

    private final Identifier fallback;

    public ProfessionTextureSupplier(TextureSupplier<String> formatter) {
        this.formatter = formatter;
        this.fallback = formatter.apply("villager_pony");
    }

    @Override
    public Identifier apply(T container) {
        return apply(container.getVillagerData());
    }

    public Identifier apply(VillagerData t) {
        return getTexture(t.type().unwrapKey().orElse(VillagerType.PLAINS), t.profession().unwrapKey().orElse(VillagerProfession.NONE));
    }

    private Identifier getTexture(final ResourceKey<VillagerType> type, final ResourceKey<VillagerProfession> profession) {
        return ResourceUtil.verifyTexture(formatter.apply(getKey(type, profession))).orElseGet(() -> {
            if (type.equals(VillagerType.PLAINS)) {
                // if texture loading fails, use the fallback.
                return fallback;
            }

            return getTexture(VillagerType.PLAINS, profession);
        });
    }

    public static String getKey(VillagerDataHolder container) {
        VillagerData t = container.getVillagerData();
        return getKey(
                t.type().unwrapKey().orElse(VillagerType.PLAINS),
                t.profession().unwrapKey().orElse(VillagerProfession.NONE)
        );
    }

    public static String getKey(final ResourceKey<VillagerType> type, final ResourceKey<VillagerProfession> profession) {
        return ResourceUtil.format("pony/%s/%s",
                type.identifier().getPath(),
                profession.identifier().getPath());
    }

}
