package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class ZomponyVillagerRenderer extends AbstractNpcRenderer<ZombieVillagerEntity, ZomponyVillagerRenderer.State> {
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");
    private static final TextureSupplier<State> TEXTURES = TextureSupplier.ofPool(
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
    public State createRenderState() {
        return new State();
    }

    @Override
    protected void initializeModel(ClientPonyModel<State> model) {
        model.onSetModelAngles((m, state) -> {
            if (m.getArmPoseForSide(state, state.mainArm) == ArmPose.EMPTY) {
                MobPosingHelper.rotateUndeadArms(state, m, state.limbFrequency, state.age);
            }
        });
    }

    public static class State extends SillyPonyTextureSupplier.State {
        @Override
        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            if (((ZombieVillagerEntity)entity).isConverting()) {
                bodyYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
            }
        }
    }
}
