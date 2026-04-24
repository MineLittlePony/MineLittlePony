package com.minelittlepony.client.render.blockentity.skull;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

import org.joml.Vector3f;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.skull.Skull;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.entity.PiglinPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.util.PartUtil;
import com.minelittlepony.mson.util.RenderList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

class PonyHeadModel extends Model<PonyHeadModel.State> {
    private final ClientPonyModel<?> ponyModel;

    private final RenderList head;

    public PonyHeadModel(ClientPonyModel<?> ponyModel) {
        super(PartUtil.EMPTY_PART, ponyModel.renderType());
        this.ponyModel = ponyModel;
        this.head = ponyModel.getRenderList(BodyPart.HEAD);
    }

    @Override
    public void setupAnim(State state) {
        ponyModel.setupAnim(state.ponyState);
        var head = ponyModel.getBodyPart(BodyPart.HEAD);
        Vector3f v = new Vector3f(0, -2, 2).rotate(Axis.YP.rotationDegrees(state.yRot));
        head.setPos(v.x, v.y, v.z);
        head.yRot = 0;
        head.xRot = state.yRot * Mth.DEG_TO_RAD;
        if (ponyModel instanceof PiglinPonyModel m) {
            m.setHeadRotation(state.animationPos);
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        head.accept(matrices, vertices, light, OverlayTexture.NO_OVERLAY, color);
    }

    static class State extends Skull.State {
        public PonyRenderState ponyState;
    }
}