package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class VillagerPonyRenderer extends AbstractNpcRenderer<VillagerEntity> {

    private static final String TYPE = "villager";
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/villager/%s.png");

    public VillagerPonyRenderer(EntityRendererFactory.Context context) {
        super(context, TYPE,
                TextureSupplier.ofPool(PonyManager.BACKGROUND_PONIES,
                PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER))), FORMATTER);
    }

    @Override
    protected void initializeModel(ClientPonyModel<VillagerEntity> model) {
        model.onSetModelAngles((m, move, swing, ticks, entity) -> {
            m.getAttributes().visualHeight += SillyPonyTextureSupplier.isCrownPony(entity) ? 0.3F : -0.1F;

            boolean isHeadRolling = entity instanceof MerchantEntity && ((MerchantEntity)entity).getHeadRollingTimeLeft() > 0;

            if (isHeadRolling) {
                m.head.yaw = 0.3F * MathHelper.sin(0.45F * ticks);
            }
        });
    }
}
