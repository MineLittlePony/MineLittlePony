package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;

import java.util.function.Function;

public class PlayerModelKey<T extends LivingEntity, M extends Model & MsonModel> {

    private final ModelKey<M> steveKey;
    private final ModelKey<M> alexKey;

    private final RendererFactory rendererFactory;

    PlayerModelKey(String name, Function<Boolean, M> modelFactory, RendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;

        steveKey = Mson.getInstance().registerModel(new Identifier("minelittlepony", "races/steve/" + name), () -> modelFactory.apply(false));
        alexKey = Mson.getInstance().registerModel(new Identifier("minelittlepony", "races/alex/" + name), () -> modelFactory.apply(true));
    }

    public ModelKey<M> getKey(boolean slimArms) {
        return slimArms ? alexKey : steveKey;
    }

    @SuppressWarnings("unchecked")
    public Function<EntityRenderDispatcher, PlayerEntityRenderer> getRendererFactory(boolean slimArms) {
        return d -> rendererFactory.create(d, slimArms, (ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>>)getKey(slimArms));
    }

    public interface RendererFactory {
        PlayerEntityRenderer create(
                EntityRenderDispatcher dispatcher,
                boolean slim,
                ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> key
        );
    }
}
