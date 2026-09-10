package com.minelittlepony.client.model.part;

import com.minelittlepony.api.pony.meta.Race;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.*;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.MagicGlow;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.api.model.PonyModel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;


public class UnicornHorn<T extends PonyRenderState> implements SubModel<T> {
    private final ModelPart horn;
    private final ModelPart glow;
    private final ModelPart[] hornLength;

    private final ModelPart changelingAntlers;
    //TODO: make a custom glow for changeling antlers that doesn't look like GARBAGE!!!

    private int tint;

    public UnicornHorn(ModelPart tree) {
        horn = tree.getChild("bone");
        glow = tree.getChild("corona");
        // the following values correspond to what's set in HornLength.java
        hornLength = new ModelPart[5];
        hornLength[0] = horn.getChild("stub");
        hornLength[1] = horn.getChild("foal");
        hornLength[2] = horn.getChild("short");
        hornLength[3] = horn.getChild("full");
        hornLength[4] = horn.getChild("long");

        if (tree.hasChild("changeling_antlers")) {
            changelingAntlers = tree.getChild("changeling_antlers");
        } else {
            changelingAntlers = null;
        }
    }

    @Override
    public void accept(PoseStack matrices, VertexConsumer vertices, int overlay, int light, int color) {
        horn.render(matrices, vertices, overlay, light, color);
        if (changelingAntlers != null) {
            changelingAntlers.render(matrices, vertices, overlay, light, color);
        }
    }

    @Override
    public void render(PonyModel<T> model, T state, PoseStack matrices, SubmitNodeCollector frame) {
        if (tint != 0) {
            matrices.pushPose();
            model.transformAccessory(state, BodyPart.HEAD, matrices);
            frame.submitModelPart(glow, matrices, MagicGlow.getRenderLayer(),
                    LightCoordsUtil.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY, null, ARGB.color(1F, tint),
                    null, 0);
            matrices.popPose();
        }
    }

    @Override
    public void setAngles(PonyModel<T> model, T state) {
        horn.resetPose();
        glow.resetPose();
        tint = !state.isSpectator && state.hasMagicGlow() && state.headVisible && state.hornGlowVisible ? state.glowColor : 0;
        horn.visible = !state.isSpectator && state.race.hasHorn() && state.headVisible;
        glow.visible = tint != 0;
        // setting horn length
        if (horn.visible) {
            for (ModelPart modelPart : hornLength) {
                modelPart.visible = false;
            }
            hornLength[state.attributes.metadata.hornLength().getValue()].visible = true;
            glow.yScale = state.attributes.metadata.hornLength().getGlowSize();
        }
        if (changelingAntlers != null) {
            changelingAntlers.resetPose();
            state.transformation.transform(state.attributes, BodyPart.HORN, changelingAntlers); // if the horn part is meant to exist
            if (state.attributes.metadata.changelingAntlers() != 0) {
                changelingAntlers.visible = horn.visible;
            }
        }
        state.transformation.transform(state.attributes, BodyPart.HORN, horn);
        state.transformation.transform(state.attributes, BodyPart.HORN, glow);
    }

    public void setHidden() {
        horn.visible = glow.visible = false;
        if (changelingAntlers != null) {
            changelingAntlers.visible = false;
        }
    }
}
