package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.npc.textures.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.function.Function;

abstract class AbstractNpcRenderer<
        T extends MobEntity & VillagerDataContainer,
        S extends PonyRenderState
    > extends PonyRenderer<T, S, ClientPonyModel<S>> {
    private final NpcClothingFeature<T, S, ClientPonyModel<S>, AbstractNpcRenderer<T, S>> clothing;
    private final Function<Race, Models<T, ClientPonyModel<S>>> models = Util.memoize(race -> {
        if (race.isHuman()) {
            race = Race.EARTH;
        }
        return ModelType.getPlayerModel(race).create(false, this::initializeModel);
    });

    public AbstractNpcRenderer(EntityRendererFactory.Context context, String type, TextureSupplier<T> textureSupplier, TextureSupplier<String> formatter) {
        super(context, ModelType.getPlayerModel(Race.EARTH).getKey(false), SillyPonyTextureSupplier.create(textureSupplier, formatter));
        clothing = new NpcClothingFeature<>(this, type);
        this.manager.setModelsLookup(entity -> models.apply(getEntityPony(entity).race()));
        addFeature(clothing);
    }

    @Override
    public boolean shouldRender(ClientPonyModel<S> model, S entity, Wearable wearable, Gear gear) {
        if (wearable == Wearable.SADDLE_BAGS_BOTH) {
            VillagerProfession profession = entity.getVillagerData().getProfession();
            return !SillyPonyTextureSupplier.isBestPony(entity) && profession != VillagerProfession.NONE && (
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

    protected void initializeModel(ClientPonyModel<S> model) {

    }

    @Override
    public Identifier getDefaultTexture(S villager, Wearable wearable) {
        if (wearable.isSaddlebags()) {
            return clothing.createTexture(villager, "accessory");
        }
        return wearable.getDefaultTexture();
    }
}
