package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.WitchPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

public class WitchRenderer extends PonyRenderer<WitchEntity, WitchRenderer.State, WitchPonyModel> {
    private static final Identifier WITCH_TEXTURES = MineLittlePony.id("textures/entity/witch_pony.png");

    public WitchRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.WITCH, TextureSupplier.of(WITCH_TEXTURES), BASE_MODEL_SCALE);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public static class State extends PonyRenderState {
        public boolean drinking;

        @Override
        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            drinking = entity instanceof WitchEntity w && w.isDrinking();
            attributes.visualHeight += 0.5F;
            if (customName != null && "Filly".equals(customName.getString())) {
                baby = true;
            }
        }

        @Override
        public boolean isWearing(Wearable wearable) {
            return wearable == Wearable.HAT || super.isWearing(wearable);
        }
    }
}
