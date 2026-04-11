package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.WitchPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Witch;

public class WitchRenderer extends PonyRenderer<Witch, WitchRenderer.State, WitchPonyModel> {
    private static final Identifier WITCH_TEXTURES = MineLittlePony.id("textures/entity/witch_pony.png");

    public WitchRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.WITCH, TextureSupplier.of(WITCH_TEXTURES), BASE_MODEL_SCALE);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Witch entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.drinking = entity.isDrinkingPotion();
        state.attributes.visualHeight += 0.5F;
        state.isBaby |= state.nameTag != null && "Filly".equals(state.nameTag.getString());
    }

    public static class State extends PonyRenderState {
        public boolean drinking;

        @Override
        public boolean isWearing(Wearable wearable) {
            return wearable == Wearable.HAT || super.isWearing(wearable);
        }
    }
}
