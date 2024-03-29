package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class VillagerPonyRenderer extends AbstractNpcRenderer<VillagerEntity> {
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/villager/%s.png");

    public VillagerPonyRenderer(EntityRendererFactory.Context context) {
        super(context, "villager",
                TextureSupplier.ofPool(VariatedTextureSupplier.BACKGROUND_PONIES_POOL,
                PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER))), FORMATTER);
    }

    @Override
    protected void initializeModel(ClientPonyModel<VillagerEntity> model) {
        model.onSetModelAngles((m, move, swing, ticks, entity) -> {
            m.getAttributes().visualHeight += SillyPonyTextureSupplier.isCrownPony(entity) ? 0.3F : -0.1F;

            if (entity.getHeadRollingTimeLeft() > 0) {
                m.head.yaw = 0.3F * MathHelper.sin(0.45F * ticks);
            }
        });
    }
}
