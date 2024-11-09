package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.*;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PiglinPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PonyPiglinRenderer extends PonyRenderer<HostileEntity, PonyPiglinRenderer.State, PiglinPonyModel> {
    public static final Identifier PIGLIN = MineLittlePony.id("textures/entity/piglin/piglin_pony.png");
    public static final Identifier PIGLIN_BRUTE = MineLittlePony.id("textures/entity/piglin/piglin_brute_pony.png");
    public static final Identifier ZOMBIFIED_PIGLIN = MineLittlePony.id("textures/entity/piglin/zombified_piglin_pony.png");

    public PonyPiglinRenderer(EntityRendererFactory.Context context, Identifier texture, float scale) {
        super(context, ModelType.PIGLIN, TextureSupplier.of(texture), scale);
    }

    public static PonyPiglinRenderer piglin(EntityRendererFactory.Context context) {
        return new PonyPiglinRenderer(context, PIGLIN, 1);
    }

    public static PonyPiglinRenderer brute(EntityRendererFactory.Context context) {
        return new PonyPiglinRenderer(context, PIGLIN_BRUTE, 1.15F);
    }

    public static PonyPiglinRenderer zombified(EntityRendererFactory.Context context) {
        return new PonyPiglinRenderer(context, ZOMBIFIED_PIGLIN, 1);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public void updateRenderState(AbstractPiglinEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.activity = entity.getActivity();
        state.shouldZombify = entity.shouldZombify();
    }

    @Override
    protected boolean isShaking(State state) {
       return super.isShaking(state) || state.shouldZombify;
    }

    public static class State extends PonyRenderState {
        public boolean shouldZombify;
        public PiglinActivity activity;
    }

}
