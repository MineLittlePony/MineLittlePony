package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.IllagerPonyModel;

import java.util.Arrays;

public class IllusionistPonyRenderer extends IllagerPonyRenderer<IllusionerEntity, IllusionistPonyRenderer.State, IllagerPonyModel<IllusionistPonyRenderer.State>> {
    public IllusionistPonyRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ILLAGER, ILLUSIONIST);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void render(IllusionistPonyRenderer.State entity, MatrixStack stack, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        if (!entity.invisible) {
            Vec3d[] clones = entity.mirrorCopyOffsets;

            for (int i = 0; i < clones.length; ++i) {
                stack.push();
                stack.translate(
                        clones[i].x + MathHelper.cos(i + entity.age * 0.5F) * 0.025D,
                        clones[i].y + MathHelper.cos(i + entity.age * 0.75F) * 0.0125D,
                        clones[i].z + MathHelper.cos(i + entity.age * 0.7F) * 0.025D
                );
                super.render(entity, stack, queue, camera);
                stack.pop();
            }
        } else {
            super.render(entity, stack, queue, camera);
        }
    }

    @Override
    protected boolean isVisible(IllusionistPonyRenderer.State entity) {
        return true;
    }

    @Override
    public void updateRenderState(IllusionerEntity entity, IllusionistPonyRenderer.State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        Vec3d[] vec3ds = entity.getMirrorCopyOffsets(tickDelta);
        state.mirrorCopyOffsets = (Vec3d[])Arrays.copyOf(vec3ds, vec3ds.length);
        state.spellcasting = entity.isSpellcasting();
    }

    public static class State extends IllagerPonyRenderer.State {
        public Vec3d[] mirrorCopyOffsets;
        public boolean spellcasting;
    }
}