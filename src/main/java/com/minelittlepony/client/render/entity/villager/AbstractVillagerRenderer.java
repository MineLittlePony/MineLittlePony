package com.minelittlepony.client.render.entity.villager;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.RenderPonyMob;
import com.minelittlepony.client.render.entity.feature.LayerGear;
import com.minelittlepony.model.IUnicorn;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.util.resources.ITextureSupplier;

abstract class AbstractVillagerRenderer<
    T extends MobEntity & VillagerDataContainer,
    M extends ClientPonyModel<T> & IUnicorn<ModelPart> & ModelWithHat> extends RenderPonyMob.Caster<T, M> {

    private final ITextureSupplier<T> baseTextures;

    private final String entityType;

    public AbstractVillagerRenderer(EntityRenderDispatcher manager, ModelKey<? super M> key, String type, ITextureSupplier<String> formatter) {
        super(manager, key);

        entityType = type;
        baseTextures = new PonyTextures<>(formatter);
        addFeature(new ClothingLayer<>(this, entityType));
    }

    @Override
    public boolean shouldRender(M model, T entity, IGear gear) {

        boolean special = PonyTextures.isBestPony(entity);

        if (gear == LayerGear.SADDLE_BAGS) {
            VillagerProfession profession = entity.getVillagerData().getProfession();
            return !special && profession != VillagerProfession.NONE && (
                    profession == VillagerProfession.CARTOGRAPHER
                 || profession == VillagerProfession.FARMER
                 || profession == VillagerProfession.FISHERMAN
                 || profession == VillagerProfession.LIBRARIAN
                 || profession == VillagerProfession.SHEPHERD);
        }

        if (gear == LayerGear.MUFFIN) {
            return PonyTextures.isCrownPony(entity);
        }

        return super.shouldRender(model, entity, gear);
    }

    @Override
    public Identifier getDefaultTexture(T villager, IGear gear) {
        if (gear == LayerGear.SADDLE_BAGS) {
            return ClothingLayer.getClothingTexture(villager, entityType);
        }
        return super.getDefaultTexture(villager, gear);
    }

    @Override
    public Identifier findTexture(T villager) {
        return baseTextures.supplyTexture(villager);
    }
}
