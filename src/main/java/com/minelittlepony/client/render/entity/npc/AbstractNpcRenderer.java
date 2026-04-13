package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.npc.textures.*;

abstract class AbstractNpcRenderer<
        T extends Mob & VillagerDataHolder,
        S extends SillyPonyTextureSupplier.State
    > extends PonyRenderer<T, S, ClientPonyModel<S>> {
    private final NpcClothingFeature<T, S, ClientPonyModel<S>, AbstractNpcRenderer<T, S>> clothing;

    public AbstractNpcRenderer(EntityRendererProvider.Context context, String type, TextureSupplier<T> textureSupplier, TextureSupplier<String> formatter) {
        super(context, ModelType.getPlayerModel(Race.EARTH).steveKey(), SillyPonyTextureSupplier.create(textureSupplier, formatter));
        clothing = new NpcClothingFeature<>(this, type);
        this.manager.setModelsLookup(race -> {
            if (race.isHuman()) {
                race = Race.EARTH;
            }
            Models<ClientPonyModel<S>> models = ModelType.getPlayerModel(race).steve();
            initializeModel(models.body());
            return models;
        });
        addLayer(clothing);
    }

    @Override
    public boolean shouldRender(ClientPonyModel<S> model, S state, Wearable wearable, Gear<S> gear) {
        if (wearable == Wearable.SADDLE_BAGS_BOTH) {
            return state.hasSaddlebags;
        }

        if (wearable == Wearable.MUFFIN) {
            return state.hasMuffinHat;
        }

        return super.shouldRender(model, state, wearable, gear);
    }

    protected abstract void initializeModel(ClientPonyModel<S> model);

    @Override
    public Identifier getDefaultTexture(S state, Wearable wearable) {
        if (wearable.isSaddlebags()) {
            return clothing.createTexture(state, "accessory");
        }
        return wearable.getDefaultTexture();
    }
}
