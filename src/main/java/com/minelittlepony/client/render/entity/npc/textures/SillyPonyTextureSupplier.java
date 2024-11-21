package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.*;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class SillyPonyTextureSupplier {
    public static <T extends State> TextureSupplier<T> create(TextureSupplier<T> fallback, TextureSupplier<String> formatter) {
        Identifier egg = formatter.apply("silly_pony");
        Identifier egg2 = formatter.apply("tiny_silly_pony");
        return entity -> entity.isDerpy ? (entity.isDinky ? egg2 : egg) : fallback.apply(entity);
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

    public static class State extends PonyRenderState implements VillagerDataContainer {
        public VillagerData villagerData;

        public boolean isDerpy;
        public boolean isDinky;
        public boolean hasMuffinHat;
        public boolean hasSaddlebags;

        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            attributes.visualHeight += hasMuffinHat ? 0.3F : -0.1F;
            isDerpy = SillyPonyTextureSupplier.isBestPony(entity);
            isDinky = isDerpy && customName != null && "Dinky".equals(customName.getString());
            hasMuffinHat = SillyPonyTextureSupplier.isCrownPony(entity);

            villagerData = ((VillagerDataContainer)entity).getVillagerData();
            VillagerProfession profession = villagerData.getProfession();

            hasSaddlebags = !isDerpy && profession != VillagerProfession.NONE && (
                    profession == VillagerProfession.CARTOGRAPHER
                 || profession == VillagerProfession.FARMER
                 || profession == VillagerProfession.FISHERMAN
                 || profession == VillagerProfession.LIBRARIAN
                 || profession == VillagerProfession.SHEPHERD);
        }

        @Override
        public VillagerData getVillagerData() {
            return villagerData;
        }

        @Override
        public void setVillagerData(VillagerData villagerData) {
        }
    }
}
