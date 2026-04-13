package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Arm;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class ZomponyVillagerRenderer extends AbstractNpcRenderer<ZombieVillagerEntity, ZomponyVillagerRenderer.State> {
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
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(ZombieVillagerEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        if (entity.isConverting()) {
            state.bodyYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }
    }

    @Override
    protected void initializeModel(ClientPonyModel<State> model) {
        model.onSetModelAngles((m, state) -> {
            MobPosingHelper.animateZombieArms(m.getArm(Arm.LEFT), m.getArm(Arm.RIGHT), state.aggressive, state);
        });
    }

    public static class State extends SillyPonyTextureSupplier.State {
        public boolean aggressive;

        public void updateState(ItemModelManager resolver, @Nullable LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            this.aggressive = entity instanceof HostileEntity m && m.isAttacking();
        }
    }
}
