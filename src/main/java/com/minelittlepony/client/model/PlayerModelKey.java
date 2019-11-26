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

    private final ModelKey<M> key;

    private boolean slim;

    private final Key steveKey;
    private final Key alexKey;

    private final RendererFactory rendererFactory;

    PlayerModelKey(Identifier id, Function<Boolean, M> factory, RendererFactory rendererFactory) {
        this.key = Mson.getInstance().registerModel(id, () -> factory.apply(slim));
        this.rendererFactory = rendererFactory;

        steveKey = new Key(false);
        alexKey = new Key(true);
    }

    public Key getKey(boolean slimArms) {
        return slimArms ? alexKey : steveKey;
    }

    public class Key implements ModelKey<M> {

        final boolean slim;

        public Key(boolean slim) {
            this.slim = slim;
        }

        @Override
        public Identifier getId() {
            return key.getId();
        }

        @Override
        public M createModel() {
            PlayerModelKey.this.slim = this.slim;
            return key.createModel();
        }

        @SuppressWarnings("unchecked")
        public Function<EntityRenderDispatcher, PlayerEntityRenderer> getFactory() {
            return d -> rendererFactory.create(d, slim, (ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>>)this);
        }
    }

    public interface RendererFactory {
        PlayerEntityRenderer create(EntityRenderDispatcher dispatcher, boolean slim, ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> key);
    }
}
