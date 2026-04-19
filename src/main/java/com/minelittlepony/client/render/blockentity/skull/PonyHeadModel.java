package com.minelittlepony.client.render.blockentity.skull;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;

import org.joml.Vector3f;

import com.minelittlepony.api.model.skull.Skull;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

class PonyHeadModel extends Model<PonyHeadModel.State> {
    private final ClientPonyModel<?> ponyModel;

    public PonyHeadModel(ClientPonyModel<?> ponyModel) {
        super(ponyModel.root(), ponyModel.renderType());
        this.ponyModel = ponyModel;
    }

    @Override
    public void setupAnim(State state) {
        Vector3f v = new Vector3f(0, -2, 2).rotate(Axis.YP.rotationDegrees(state.yRot));
        ponyModel.setupAnim(state.ponyState);
        ponyModel.getHead().setPos(v.x, v.y, v.z);
        ponyModel.setHeadRotation(state.animationPos, state.yRot, 0);
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        ponyModel.renderHead(matrices, vertices, light, OverlayTexture.NO_OVERLAY, color);
    }

    static class State extends Skull.State {
        public PonyRenderState ponyState;
    }
}