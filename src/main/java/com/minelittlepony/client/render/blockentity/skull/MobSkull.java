package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.base.Suppliers;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.skull.Skull;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Supplier;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.component.ResolvableProfile;

import org.jetbrains.annotations.Nullable;

public class MobSkull<S extends PonyRenderState> implements Skull<PonyHeadModel.State> {
    private final Identifier texture;
    private final MobRenderers type;

    private final Supplier<PonyHeadModel> ponyHead;
    private final Supplier<S> state;

    MobSkull(Identifier texture, MobRenderers type, ModelKey<? extends ClientPonyModel<?>> modelKey, Supplier<S> state) {
        this.texture = texture;
        this.type = type;
        this.state = state;
        this.ponyHead = Suppliers.memoize(() -> new PonyHeadModel(modelKey.createModel()));
    }

    @Override
    public boolean canRender(Pony pony, @Nullable ResolvableProfile profile, PonyConfig config) {
        return config.ponyskulls.get() && type.option().get() && !pony.race().isHuman();
    }

    @Override
    public Identifier getSkinResource(@Nullable ResolvableProfile profile) {
        return texture;
    }

    @Override
    public PonyHeadModel.State createState() {
        return new PonyHeadModel.State();
    }

    @Override
    public void submit(PoseStack stack, PonyHeadModel.State state, SubmitNodeCollector queue, RenderType layer) {
        state.ponyState = this.state.get();
        state.ponyState.pony = state.pony;
        state.ponyState.race = state.pony.race();
        state.ponyState.attributes.size = state.pony.size();
        state.ponyState.attributes.metadata = state.pony.metadata();
        state.ponyState.headVisible = true;
        queue.order(0).submitModel(ponyHead.get(), state, stack, layer, state.light, OverlayTexture.NO_OVERLAY, ARGB.white(state.alpha), null, state.outlineColor, state.crumblingOverlay);
    }
}
