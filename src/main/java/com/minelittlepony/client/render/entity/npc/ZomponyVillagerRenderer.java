package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class ZomponyVillagerRenderer extends AbstractNpcRenderer<ZombieVillager, ZomponyVillagerRenderer.State> {
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
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ZombieVillager entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        if (entity.isConverting()) {
            state.bodyRot += Mth.cos(state.ageInTicks * 3.25F) * (Mth.PI / 4);
        }
    }

    @Override
    protected void initializeModel(PonyModel<State> model) {
        model.onSetModelAngles((m, state) -> {
            MobPosingHelper.animateZombieArms(m.getForeLeg(HumanoidArm.LEFT), m.getForeLeg(HumanoidArm.RIGHT), state.aggressive, state);
        });
    }

    public static class State extends SillyPonyTextureSupplier.State {
        public boolean aggressive;

        public void updateState(ItemModelResolver resolver, @Nullable LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            this.aggressive = entity instanceof Monster m && m.isAggressive();
        }
    }
}
