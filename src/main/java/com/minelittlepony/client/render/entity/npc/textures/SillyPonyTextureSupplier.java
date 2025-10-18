package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.*;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class SillyPonyTextureSupplier {
    public static <T extends LivingEntity> TextureSupplier<T> create(TextureSupplier<T> fallback, TextureSupplier<String> formatter) {
        Identifier egg = formatter.apply("silly_pony");
        Identifier egg2 = formatter.apply("tiny_silly_pony");
        return entity -> isBestPony(entity) ? (isDinky(entity) ? egg2 : egg) : fallback.apply(entity);
    }

    public static boolean isBestPony(LivingEntity entity) {
        if (!entity.hasCustomName()) {
            return false;
        }
        String name = entity.getCustomName().getString();
        return "Derpy".equals(name) || "Dinky".equals(name);
    }

    public static boolean isCrownPony(LivingEntity entity) {
        return isBestPony(entity) && entity.getUuid().getLeastSignificantBits() % 20 == 0;
    }

    public static boolean isDinky(LivingEntity entity) {
        return entity.hasCustomName() && "Dinky".equals(entity.getCustomName().getString());
    }

    public static class State extends PonyRenderState {
        public RegistryKey<VillagerType> type = VillagerType.PLAINS;
        public RegistryKey<VillagerProfession> profession = VillagerProfession.NONE;
        public int level;

        public boolean isDerpy;
        public boolean isDinky;
        public boolean hasMuffinHat;
        public boolean hasSaddlebags;

        @Override
        public void updateState(ItemModelManager resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            attributes.visualHeight += hasMuffinHat ? 0.3F : -0.1F;
            isDerpy = isBestPony(entity);
            isDinky = isDinky(entity);
            hasMuffinHat = isCrownPony(entity);

            var villagerData = ((VillagerDataContainer)entity).getVillagerData();
            type = villagerData.type().getKey().orElse(VillagerType.PLAINS);
            profession = villagerData.profession().getKey().orElse(VillagerProfession.NONE);
            level = villagerData.level();

            hasSaddlebags = !isDerpy && profession != VillagerProfession.NONE && (
                    profession == VillagerProfession.CARTOGRAPHER
                 || profession == VillagerProfession.FARMER
                 || profession == VillagerProfession.FISHERMAN
                 || profession == VillagerProfession.LIBRARIAN
                 || profession == VillagerProfession.SHEPHERD);
        }
    }
}
