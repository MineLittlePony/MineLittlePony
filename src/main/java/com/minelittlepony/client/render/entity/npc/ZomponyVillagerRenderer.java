package com.minelittlepony.client.render.entity.npc;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ZomponyVillagerModel;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieVillagerEntity;

public class ZomponyVillagerRenderer extends AbstractNpcRenderer<ZombieVillagerEntity, ZomponyVillagerModel> {

    private static final String TYPE = "zombie_villager";
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");

    public ZomponyVillagerRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ZOMBIE_VILLAGER, TYPE, FORMATTER);
    }

    @Override
    protected void setupTransforms(ZombieVillagerEntity entity, MatrixStack stack, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }

        super.setupTransforms(entity, stack, move, rotationYaw, ticks);
    }
}
