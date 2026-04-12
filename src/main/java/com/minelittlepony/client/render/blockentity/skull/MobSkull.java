package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.base.Suppliers;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.function.Supplier;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.component.ResolvableProfile;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class MobSkull<S extends PonyRenderState> implements ISkull {
    private final Identifier texture;
    private final MobRenderers type;

    private final Supplier<ClientPonyModel<?>> ponyHead;
    private final Supplier<S> state;

    MobSkull(Identifier texture, MobRenderers type, ModelKey<? extends ClientPonyModel<?>> modelKey, Supplier<S> state) {
        this.texture = texture;
        this.type = type;
        this.state = state;
        this.ponyHead = Suppliers.memoize(modelKey::createModel);
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
    public void render(PoseStack stack, State state, SubmitNodeCollector queue, RenderType layer) {
        S ponyState = this.state.get();
        ponyState.pony = state.pony;
        ponyState.race = state.pony.race();
        ponyState.attributes.size = state.pony.size();
        ponyState.attributes.metadata = state.pony.metadata();
        ponyState.headVisible = true;

        PoseStack copyStack = new PoseStack();
        var model = ponyHead.get();
        Vector3f v = new Vector3f(0, -2, 1.99F);
        v.rotate(Axis.YP.rotationDegrees(state.yRot));

        queue.order(0).submitCustomGeometry(stack, layer, (entry, vertices) -> {
            copyStack.last().set(entry);
            model.setupAnim(ponyState);
            model.getHead().setPos(v.x, v.y, v.z);
            model.setHeadRotation(state.animationPos, state.yRot, 0);
            model.renderHead(copyStack, vertices, state.light, OverlayTexture.NO_OVERLAY, ARGB.white(state.alpha));
        });
    }
}
