package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.pig.Pig;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.api.state.PonifiedRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public class PonyPigRenderer extends PigRenderer {
    public PonyPigRenderer(EntityRendererProvider.Context context) {
        super(context);
        addLayer(new CrownFeature(this));
    }

    @Override
    public PigRenderState createRenderState() {
        return new State();
    }

    public void extractRenderState(final Pig entity, final PigRenderState state, final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (state instanceof State s) {
            s.isTechnoblade = isTechnoblade(entity);
        }
    }

    private final class CrownFeature extends RenderLayer<PigRenderState, PigModel> {
        private final PigModel model = new PigModel(LayerDefinition.create(createBasePigModel(new CubeDeformation(0.5F)), 64, 32).bakeRoot()) {
            @Override
            public void setupAnim(LivingEntityRenderState state) {
                super.setupAnim(state);
                if (state.isBaby) {
                    this.head.y += 6;
                    this.head.z += 6;
                }
            }
        };

        public CrownFeature(RenderLayerParent<PigRenderState, PigModel> context) {
            super(context);
        }

        protected static MeshDefinition createBasePigModel(final CubeDeformation g) {
            MeshDefinition mesh = QuadrupedModel.createBodyMesh(6, true, false, g);
            PartDefinition root = mesh.getRoot();
            root.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4, -4, -8, 8, 3, 8, g).texOffs(16, 16).addBox(-2, 0, -9, 4, 3, 1, g),
                PartPose.offset(0, 10, -6)
            );
            return mesh;
        }

        @Override
        public void submit(PoseStack matrices, SubmitNodeCollector frame, int light, PigRenderState state, float yRot, float xRot) {
            if (state instanceof State s && s.isTechnoblade) {
                coloredCutoutModelCopyLayerRender(model, Wearable.CROWN.getDefaultTexture(), matrices, frame, light, state, CommonColors.WHITE, 0);
            }
        }
    }

    public static boolean isTechnoblade(LivingEntity entity) {
        return entity.hasCustomName() && entity.getCustomName().getString().equalsIgnoreCase("technoblade");
    }

    static class State extends PigRenderState implements PonifiedRenderState {
        public boolean isTechnoblade;

        @Override
        public boolean isOf(EntityType<?> entityType) {
            return this.entityType == entityType;
        }
    }
}
