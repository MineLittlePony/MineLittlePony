package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.*;

import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.npc.textures.*;

abstract class AbstractNpcRenderer<
        T extends MobEntity & VillagerDataContainer,
        S extends SillyPonyTextureSupplier.State
    > extends PonyRenderer<T, S, ClientPonyModel<S>> {
    private final NpcClothingFeature<T, S, ClientPonyModel<S>, AbstractNpcRenderer<T, S>> clothing;

    public AbstractNpcRenderer(EntityRendererFactory.Context context, String type, TextureSupplier<T> textureSupplier, TextureSupplier<String> formatter) {
        super(context, ModelType.getPlayerModel(Race.EARTH).getKey(false), SillyPonyTextureSupplier.create(textureSupplier, formatter));
        clothing = new NpcClothingFeature<>(this, type);
        this.manager.setModelsLookup(race -> {
            if (race.isHuman()) {
                race = Race.EARTH;
            }
            return ModelType.getPlayerModel(race).create(false, this::initializeModel);
        });
        addFeature(clothing);
    }

    @Override
    public boolean shouldRender(ClientPonyModel<S> model, S entity, Wearable wearable, Gear gear) {
        if (wearable == Wearable.SADDLE_BAGS_BOTH) {
            return entity.hasSaddlebags;
        }

        if (wearable == Wearable.MUFFIN) {
            return entity.hasMuffinHat;
        }

        return super.shouldRender(model, entity, wearable, gear);
    }

    protected abstract void initializeModel(ClientPonyModel<S> model);

    @Override
    public Identifier getDefaultTexture(S villager, Wearable wearable) {
        if (wearable.isSaddlebags()) {
            return clothing.createTexture(villager, "accessory");
        }
        return wearable.getDefaultTexture();
    }
}
