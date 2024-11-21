package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class VillagerPonyRenderer extends AbstractNpcRenderer<VillagerEntity, VillagerPonyRenderer.State> {
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/villager/%s.png");
    private static final TextureSupplier<State> TEXTURES = TextureSupplier.ofPool(
            VariatedTextureSupplier.BACKGROUND_PONIES_POOL,
            PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER))
    );

    public VillagerPonyRenderer(EntityRendererFactory.Context context) {
        super(context, "villager", TEXTURES, FORMATTER);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public static class State extends SillyPonyTextureSupplier.State {
        public int headRollingTime;
        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            if (((VillagerEntity)entity).getHeadRollingTimeLeft() > 0) {
                this.yawDegrees = 0.3F * MathHelper.sin(0.45F * age);
            }
        }
    }
}
