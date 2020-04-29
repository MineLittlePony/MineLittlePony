package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.model.IUnicorn;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.mson.api.ModelKey;

abstract class AbstractNpcRenderer<
    T extends MobEntity & VillagerDataContainer,
    M extends ClientPonyModel<T> & IUnicorn<ModelPart> & ModelWithHat> extends PonyRenderer.Caster<T, M> {

    private final TextureSupplier<T> baseTextures;

    private final String entityType;

    public AbstractNpcRenderer(EntityRenderDispatcher manager, ModelKey<? super M> key, String type, TextureSupplier<String> formatter) {
        super(manager, key);

        entityType = type;
        baseTextures = new PonyTextures<>(formatter);
        addFeature(new NpcClothingFeature<>(this, entityType));
    }

    @Override
    public boolean shouldRender(M model, T entity, Wearable wearable, IGear gear) {

        boolean special = PonyTextures.isBestPony(entity);

        if (wearable == Wearable.SADDLE_BAGS) {
            VillagerProfession profession = entity.getVillagerData().getProfession();
            return !special && profession != VillagerProfession.NONE && (
                    profession == VillagerProfession.CARTOGRAPHER
                 || profession == VillagerProfession.FARMER
                 || profession == VillagerProfession.FISHERMAN
                 || profession == VillagerProfession.LIBRARIAN
                 || profession == VillagerProfession.SHEPHERD);
        }

        if (wearable == Wearable.MUFFIN) {
            return PonyTextures.isCrownPony(entity);
        }

        return super.shouldRender(model, entity, wearable, gear);
    }

    @Override
    public Identifier getDefaultTexture(T villager, Wearable wearable) {
        if (wearable == Wearable.SADDLE_BAGS) {
            return NpcClothingFeature.getClothingTexture(villager, entityType);
        }
        return super.getDefaultTexture(villager, wearable);
    }

    @Override
    public Identifier findTexture(T villager) {
        return baseTextures.supplyTexture(villager);
    }
}
