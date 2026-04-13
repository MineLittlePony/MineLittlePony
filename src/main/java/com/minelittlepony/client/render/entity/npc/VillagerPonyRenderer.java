package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class VillagerPonyRenderer extends AbstractNpcRenderer<VillagerEntity, VillagerPonyRenderer.State> {
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/villager/%s.png");
    private static final TextureSupplier<VillagerEntity> TEXTURES = TextureSupplier.ofPool(
            VariatedTextureSupplier.BACKGROUND_PONIES_POOL,
            PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER))
    );

    public VillagerPonyRenderer(EntityRendererFactory.Context context) {
        super(context, "villager", TEXTURES, FORMATTER);
    }

    @Override
    protected void initializeModel(ClientPonyModel<State> model) {
        model.onSetModelAngles((m, state) -> {
            if (state.headRolling) {
                m.head.roll = 0.3F * MathHelper.sin(0.45F * state.age);
                m.head.pitch = 0.4F;
            } else {
                m.head.roll = 0;
            }
        });
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(VillagerEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.headRolling = entity.getHeadRollingTimeLeft() > 0;
        state.relativeHeadYaw = 0.3F * MathHelper.sin(0.45F * state.age);
    }

    @Override
    public BipedEntityModel.ArmPose getArmPose(VillagerEntity entity, Arm arm) {
        if (arm == entity.getMainArm() && !entity.getMainHandStack().isEmpty()) {
            return BipedEntityModel.ArmPose.ITEM;
        }
        return super.getArmPose(entity, arm);
    }

    public static class State extends SillyPonyTextureSupplier.State {
        public boolean headRolling;
    }
}
