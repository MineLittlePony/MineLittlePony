package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Arm;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class ZomponyVillagerRenderer extends AbstractNpcRenderer<ZombieVillagerEntity, SillyPonyTextureSupplier.State> {
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");
    private static final TextureSupplier<ZombieVillagerEntity> TEXTURES = TextureSupplier.ofPool(
            VariatedTextureSupplier.BACKGROUND_ZOMPONIES_POOL,
            TextureSupplier.ofPool(
                    VariatedTextureSupplier.BACKGROUND_PONIES_POOL,
                    PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER))
            )
    );

    public ZomponyVillagerRenderer(EntityRendererFactory.Context context) {
        super(context, "zombie_villager", TEXTURES, FORMATTER);
    }

    @Override
    public SillyPonyTextureSupplier.State createRenderState() {
        return new SillyPonyTextureSupplier.State();
    }

    @Override
    public void updateRenderState(ZombieVillagerEntity entity, SillyPonyTextureSupplier.State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        if (entity.isConverting()) {
            state.bodyYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }
    }

    @Override
    protected void initializeModel(ClientPonyModel<SillyPonyTextureSupplier.State> model) {
        model.onSetModelAngles((m, state) -> {
            if ((state.mainArm == Arm.LEFT ? state.leftArmPose : state.rightArmPose) == ArmPose.EMPTY) {
                MobPosingHelper.rotateUndeadArms(state, m, state.limbSwingAnimationProgress, state.age);
            }
        });
    }
}
