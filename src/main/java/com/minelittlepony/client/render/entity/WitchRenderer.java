package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.WitchPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.entity.EntityRendererFactory;
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

    public void updateRenderState(WitchEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.drinking = entity instanceof WitchEntity w && w.isDrinking();
        state.attributes.visualHeight += 0.5F;
        state.baby |= state.displayName != null && "Filly".equals(state.displayName.getString());
    }

    public static class State extends PonyRenderState {
        public boolean drinking;

        @Override
        public boolean isWearing(Wearable wearable) {
            return wearable == Wearable.HAT || super.isWearing(wearable);
        }
    }
}
