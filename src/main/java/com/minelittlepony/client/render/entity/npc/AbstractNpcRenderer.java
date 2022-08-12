package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.api.model.gear.IGear;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.entity.PonyRenderer;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractNpcRenderer<T extends MobEntity & VillagerDataContainer> extends PonyRenderer.Caster<T, ClientPonyModel<T>> {

    private final TextureSupplier<T> baseTextures;

    private final String entityType;

    private final Map<Race, ModelWrapper<T, ClientPonyModel<T>>> models = new HashMap<>();

    public AbstractNpcRenderer(EntityRendererFactory.Context context, String type, TextureSupplier<String> formatter) {
        super(context, ModelType.getPlayerModel(Race.EARTH).getKey(false));
        entityType = type;
        baseTextures = new PonyTextures<>(formatter);
        addFeature(new NpcClothingFeature<>(this, entityType));
    }

    @Override
    public boolean shouldRender(ClientPonyModel<T> model, T entity, Wearable wearable, IGear gear) {

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

    public void render(T entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        model = manager.setModel(models.computeIfAbsent(getEntityPony(entity).getRace(false), this::createModel)).body();

        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
    }

    private ModelWrapper<T, ClientPonyModel<T>> createModel(Race race) {
        if (race.isHuman()) {
            race = Race.EARTH;
        }
        return ModelWrapper.<T, ClientPonyModel<T>>of(ModelType.getPlayerModel(race).getKey(false), this::initializeModel);
    }

    protected void initializeModel(ClientPonyModel<T> model) {

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
