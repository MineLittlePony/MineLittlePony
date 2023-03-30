package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.mson.api.*;

import java.util.function.*;

public record PlayerModelKey<T extends LivingEntity, M extends Model & MsonModel & IModel> (
        ModelKey<M> steveKey,
        ModelKey<M> alexKey,
        RendererFactory factory,
        MsonModel.Factory<PonyArmourModel<T>> armorFactory
) {
    PlayerModelKey(String name, BiFunction<ModelPart, Boolean, M> modelFactory, RendererFactory rendererFactory, MsonModel.Factory<PonyArmourModel<T>> armorFactory) {
        this(
            new ModelKeyImpl<>(new Identifier("minelittlepony", "races/steve/" + name), tree -> modelFactory.apply(tree, false)),
            new ModelKeyImpl<>(new Identifier("minelittlepony", "races/alex/" + name), tree -> modelFactory.apply(tree, true)),
            rendererFactory,
            armorFactory
        );
    }

    public ModelKey<M> getKey(boolean slimArms) {
        return slimArms ? alexKey : steveKey;
    }

    public <K extends T, N extends M> ModelWrapper<K, N> create(boolean slimArms) {
        return create(slimArms, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <K extends T, N extends M> ModelWrapper<K, N> create(boolean slimArms, @Nullable Consumer<N> initializer) {
        return new ModelWrapper(this, slimArms, initializer);
    }

    @SuppressWarnings("unchecked")
    public Function<EntityRendererFactory.Context, PlayerEntityRenderer> getFactory(boolean slimArms) {
        return d -> factory.create(d, slimArms, (PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>>)this);
    }

    public interface RendererFactory {
        PlayerEntityRenderer create(
                EntityRendererFactory.Context context,
                boolean slim,
                PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> key
        );
    }
}
