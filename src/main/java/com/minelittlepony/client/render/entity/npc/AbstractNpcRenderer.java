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
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.npc.textures.*;

import java.util.*;

abstract class AbstractNpcRenderer<T extends MobEntity & VillagerDataContainer> extends PonyRenderer<T, ClientPonyModel<T>> {

    private final String entityType;

    private final Map<Race, ModelWrapper<T, ClientPonyModel<T>>> models = new EnumMap<>(Race.class);

    private final NpcClothingFeature<T, ClientPonyModel<T>, AbstractNpcRenderer<T>> clothing;

    public AbstractNpcRenderer(EntityRendererFactory.Context context, String type, TextureSupplier<T> textureSupplier, TextureSupplier<String> formatter) {
        super(context, ModelType.getPlayerModel(Race.EARTH).getKey(false), new SillyPonyTextureSupplier<>(textureSupplier, formatter));
        entityType = type;
        clothing = new NpcClothingFeature<>(this, entityType);
        addFeature(clothing);
    }

    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        addFeature(createHeldItemFeature(context));
        addFeature(new GearFeature<>(this));
    }

    @Override
    public boolean shouldRender(ClientPonyModel<T> model, T entity, Wearable wearable, IGear gear) {

        boolean special = SillyPonyTextureSupplier.isBestPony(entity);

        if (wearable == Wearable.SADDLE_BAGS_BOTH) {
            VillagerProfession profession = entity.getVillagerData().getProfession();
            return !special && profession != VillagerProfession.NONE && (
                    profession == VillagerProfession.CARTOGRAPHER
                 || profession == VillagerProfession.FARMER
                 || profession == VillagerProfession.FISHERMAN
                 || profession == VillagerProfession.LIBRARIAN
                 || profession == VillagerProfession.SHEPHERD);
        }

        if (wearable == Wearable.MUFFIN) {
            return SillyPonyTextureSupplier.isCrownPony(entity);
        }

        return super.shouldRender(model, entity, wearable, gear);
    }

    @Override
    public void render(T entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        model = manager.setModel(models.computeIfAbsent(getEntityPony(entity).race(), this::createModel)).body();

        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
    }

    private ModelWrapper<T, ClientPonyModel<T>> createModel(Race race) {
        if (race.isHuman()) {
            race = Race.EARTH;
        }
        return ModelType.getPlayerModel(race).create(false, this::initializeModel);
    }

    protected void initializeModel(ClientPonyModel<T> model) {

    }

    @Override
    public Identifier getDefaultTexture(T villager, Wearable wearable) {
        if (wearable.isSaddlebags()) {
            return clothing.createTexture(villager, "accessory");
        }
        return wearable.getDefaultTexture();
    }
}
