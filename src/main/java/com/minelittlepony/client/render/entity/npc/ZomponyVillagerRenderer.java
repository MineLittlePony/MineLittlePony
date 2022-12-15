package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieVillagerEntity;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.IMobModel;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class ZomponyVillagerRenderer extends AbstractNpcRenderer<ZombieVillagerEntity> {

    private static final String TYPE = "zombie_villager";
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");

    public ZomponyVillagerRenderer(EntityRendererFactory.Context context) {
        super(context, TYPE,
                TextureSupplier.ofPool(PonyManager.BACKGROUND_ZOMPONIES,
                TextureSupplier.ofPool(PonyManager.BACKGROUND_PONIES,
                PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER)))),
                FORMATTER);
    }

    @Override
    protected void initializeModel(ClientPonyModel<ZombieVillagerEntity> model) {
        model.onSetModelAngles((m, move, swing, ticks, entity) -> {
            m.getAttributes().visualHeight += SillyPonyTextureSupplier.isCrownPony(entity) ? 0.3F : -0.1F;

            if (m.rightArmPose == ArmPose.EMPTY) {
                IMobModel.rotateUndeadArms(m, move, ticks);
            }
        });
    }

    @Override
    protected void setupTransforms(ZombieVillagerEntity entity, MatrixStack stack, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }

        super.setupTransforms(entity, stack, move, rotationYaw, ticks);
    }
}
