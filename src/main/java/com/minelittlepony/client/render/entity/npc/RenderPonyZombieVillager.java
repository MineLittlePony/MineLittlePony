package com.minelittlepony.client.render.entity.npc;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ZomponyVillagerModel;
import com.minelittlepony.util.resources.ITextureSupplier;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieVillagerEntity;

public class RenderPonyZombieVillager extends AbstractVillagerRenderer<ZombieVillagerEntity, ZomponyVillagerModel> {

    private static final String TYPE = "zombie_villager";
    private static final ITextureSupplier<String> FORMATTER = ITextureSupplier.formatted("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");

    public RenderPonyZombieVillager(EntityRenderDispatcher manager) {
        super(manager, ModelType.ZOMBIE_VILLAGER, TYPE, FORMATTER);
    }

    @Override
    protected void setupTransforms(ZombieVillagerEntity entity, MatrixStack stack, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }

        super.setupTransforms(entity, stack, move, rotationYaw, ticks);
    }
}
