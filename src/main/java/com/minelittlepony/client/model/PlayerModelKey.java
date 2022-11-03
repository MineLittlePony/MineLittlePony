package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;

import java.util.function.BiFunction;
import java.util.function.Function;

public class PlayerModelKey<T extends LivingEntity, M extends Model & MsonModel> {

    private final ModelKey<M> steveKey;
    private final ModelKey<M> alexKey;

    private final RendererFactory rendererFactory;

    PlayerModelKey(String name, BiFunction<ModelPart, Boolean, M> modelFactory, RendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;

        steveKey = Mson.getInstance().registerModel(new Identifier("minelittlepony", "races/steve/" + name), tree -> modelFactory.apply(tree, false));
        alexKey = Mson.getInstance().registerModel(new Identifier("minelittlepony", "races/alex/" + name), tree -> modelFactory.apply(tree, true));
    }

    public ModelKey<M> getKey(boolean slimArms) {
        return slimArms ? alexKey : steveKey;
    }

    @SuppressWarnings("unchecked")
    public Function<EntityRendererFactory.Context, PlayerEntityRenderer> getRendererFactory(boolean slimArms) {
        return d -> rendererFactory.create(d, slimArms, (ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>>)getKey(slimArms));
    }

    public interface RendererFactory {
        PlayerEntityRenderer create(
                EntityRendererFactory.Context context,
                boolean slim,
                ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> key
        );
    }
}
