package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.*;

import com.minelittlepony.util.ResourceUtil;

public class ProfessionTextureSupplier<T extends VillagerDataContainer> implements TextureSupplier<T> {

    public static <T extends VillagerDataContainer> TextureSupplier<T> create(TextureSupplier<String> formatter) {
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
        return getTexture(t.getType(), t.getProfession());
    }

    private Identifier getTexture(final VillagerType type, final VillagerProfession profession) {
        return ResourceUtil.verifyTexture(formatter.apply(getKey(type, profession))).orElseGet(() -> {
            if (type.equals(VillagerType.PLAINS)) {
                // if texture loading fails, use the fallback.
                return fallback;
            }

            return getTexture(VillagerType.PLAINS, profession);
        });
    }

    public static String getKey(VillagerDataContainer container) {
        VillagerData t = container.getVillagerData();
        return getKey(
                t.getType(),
                t.getProfession()
        );
    }

    public static String getKey(final VillagerType type, final VillagerProfession profession) {
        return ResourceUtil.format("pony/%s/%s",
                Registries.VILLAGER_TYPE.getId(type).getPath(),
                Registries.VILLAGER_PROFESSION.getId(profession).getPath());
    }

}
