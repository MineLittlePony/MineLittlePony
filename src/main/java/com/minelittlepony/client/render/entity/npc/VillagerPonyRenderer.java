package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.ClientPonyModel;

public class VillagerPonyRenderer extends AbstractNpcRenderer<VillagerEntity> {

    private static final String TYPE = "villager";
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/villager/%s.png");

    public VillagerPonyRenderer(EntityRendererFactory.Context context) {
        super(context, TYPE, FORMATTER);
    }

    @Override
    protected void initializeModel(ClientPonyModel<VillagerEntity> model) {
        model.onSetModelAngles((m, move, swing, ticks, entity) -> {
            m.getAttributes().visualHeight += PonyTextures.isCrownPony(entity) ? 0.3F : -0.1F;

            boolean isHeadRolling = entity instanceof MerchantEntity && ((MerchantEntity)entity).getHeadRollingTimeLeft() > 0;

            if (isHeadRolling) {
                m.head.roll = 0.3F * MathHelper.sin(0.45F * ticks);
                m.head.pitch = 0.4F;
            } else {
                m.head.roll = 0;
            }
        });
    }

    @Override
    public void scale(VillagerEntity villager, MatrixStack stack, float ticks) {
        super.scale(villager, stack, ticks);
        stack.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }
}
