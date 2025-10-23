package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.base.Suppliers;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;

import java.util.function.Supplier;

import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;

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
    public boolean canRender(PonyConfig config) {
        return config.ponyskulls.get() && type.option().get();
    }

    @Override
    public Identifier getSkinResource(@Nullable ProfileComponent profile) {
        return texture;
    }

    @Override
    public void render(MatrixStack stack, State state, OrderedRenderCommandQueue queue, Pony pony, RenderLayer layer) {

        S ponyState = this.state.get();
        ponyState.pony = pony;
        ponyState.race = pony.race();
        ponyState.attributes.size = pony.size();
        ponyState.attributes.metadata = pony.metadata();

        MatrixStack copyStack = new MatrixStack();
        var model = ponyHead.get();
        Vector3f v = new Vector3f(0, -2, 1.99F);
        v.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));

        queue.getBatchingQueue(0).submitCustom(stack, layer, (entry, vertices) -> {
            copyStack.peek().copy(entry);
            model.setVisible(true);
            model.setAngles(ponyState);
            model.getHead().setOrigin(v.x, v.y, v.z);
            model.setHeadRotation(state.poweredTicks, state.yaw, 0);
            model.renderHead(copyStack, vertices, state.light, OverlayTexture.DEFAULT_UV, ColorHelper.getWhite(state.alpha));
        });
    }
}
