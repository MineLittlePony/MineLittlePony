package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.phys.Vec3;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.IllagerPonyModel;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Arrays;

public class IllusionistPonyRenderer extends IllagerPonyRenderer<Illusioner, IllusionistPonyRenderer.State, IllagerPonyModel<IllusionistPonyRenderer.State>> {
    public IllusionistPonyRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.ILLAGER, ILLUSIONIST);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void submit(State entity, PoseStack stack, SubmitNodeCollector queue, CameraRenderState camera) {
        if (!entity.isInvisible) {
            Vec3[] clones = entity.mirrorCopyOffsets;

            for (int i = 0; i < clones.length; ++i) {
                stack.pushPose();
                stack.translate(
                        clones[i].x + Mth.cos(i + entity.ageInTicks * 0.5F) * 0.025D,
                        clones[i].y + Mth.cos(i + entity.ageInTicks * 0.75F) * 0.0125D,
                        clones[i].z + Mth.cos(i + entity.ageInTicks * 0.7F) * 0.025D
                );
                super.submit(entity, stack, queue, camera);
                stack.popPose();
            }
        } else {
            super.submit(entity, stack, queue, camera);
        }
    }

    @Override
    protected boolean isBodyVisible(State entity) {
        return true;
    }

    @Override
    public void extractRenderState(Illusioner entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        Vec3[] vec3ds = entity.getIllusionOffsets(tickDelta);
        state.mirrorCopyOffsets = Arrays.copyOf(vec3ds, vec3ds.length);
        state.spellcasting = entity.isCastingSpell();
    }

    public static class State extends IllagerPonyRenderer.State {
        public Vec3[] mirrorCopyOffsets = new Vec3[0];
        public boolean spellcasting;
    }
}