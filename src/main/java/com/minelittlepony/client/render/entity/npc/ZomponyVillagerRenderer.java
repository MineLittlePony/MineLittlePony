package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class ZomponyVillagerRenderer extends AbstractNpcRenderer<ZombieVillager, SillyPonyTextureSupplier.State> {
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");
    private static final TextureSupplier<ZombieVillager> TEXTURES = TextureSupplier.ofPool(
            VariatedTextureSupplier.BACKGROUND_ZOMPONIES_POOL,
            TextureSupplier.ofPool(
                    VariatedTextureSupplier.BACKGROUND_PONIES_POOL,
                    PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER))
            )
    );

    public ZomponyVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, "zombie_villager", TEXTURES, FORMATTER);
    }

    @Override
    public SillyPonyTextureSupplier.State createRenderState() {
        return new SillyPonyTextureSupplier.State();
    }

    @Override
    public void extractRenderState(ZombieVillager entity, SillyPonyTextureSupplier.State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        if (entity.isConverting()) {
            state.bodyRot += Mth.cos(state.ageInTicks * 3.25F) * (Mth.PI / 4);
        }
    }

    @Override
    protected void initializeModel(ClientPonyModel<SillyPonyTextureSupplier.State> model) {
        model.onSetModelAngles((m, state) -> {
            if ((state.mainArm == HumanoidArm.LEFT ? state.leftArmPose : state.rightArmPose) == ArmPose.EMPTY) {
                MobPosingHelper.rotateUndeadArms(state, m, state.attackTime, state.ageInTicks);
            }
        });
    }
}
