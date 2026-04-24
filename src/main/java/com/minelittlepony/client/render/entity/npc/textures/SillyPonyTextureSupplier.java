package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.*;

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
        return "derpy".equalsIgnoreCase(name) || "dinky".equalsIgnoreCase(name);
    }

    public static boolean isCrownPony(LivingEntity entity) {
        return isBestPony(entity) && entity.getUUID().getLeastSignificantBits() % 20 == 0;
    }

    public static boolean isDinky(LivingEntity entity) {
        return entity.hasCustomName() && "dinky".equalsIgnoreCase(entity.getCustomName().getString());
    }

    public static class State extends PonyRenderState {
        public ResourceKey<VillagerType> type = VillagerType.PLAINS;
        public ResourceKey<VillagerProfession> profession = VillagerProfession.NONE;
        public int level;

        public boolean isDerpy;
        public boolean isDinky;
        public boolean hasMuffinHat;
        public boolean hasSaddlebags;

        @Override
        public void updateState(ItemModelResolver resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            attributes.visualHeight += hasMuffinHat ? 0.3F : -0.1F;
            isDerpy = isBestPony(entity);
            isDinky = isDinky(entity);
            hasMuffinHat = isCrownPony(entity);

            var villagerData = ((VillagerDataHolder)entity).getVillagerData();
            type = villagerData.type().unwrapKey().orElse(VillagerType.PLAINS);
            profession = villagerData.profession().unwrapKey().orElse(VillagerProfession.NONE);
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
