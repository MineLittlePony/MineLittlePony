package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PiglinPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.HostilePonyRenderState;

public class PonyPiglinRenderer extends PonyRenderer<Mob, PonyPiglinRenderer.State, PiglinPonyModel> {
    public static final Identifier PIGLIN = MineLittlePony.id("textures/entity/piglin/piglin_pony.png");
    public static final Identifier PIGLIN_BRUTE = MineLittlePony.id("textures/entity/piglin/piglin_brute_pony.png");
    public static final Identifier ZOMBIFIED_PIGLIN = MineLittlePony.id("textures/entity/piglin/zombified_piglin_pony.png");

    public PonyPiglinRenderer(EntityRendererProvider.Context context, Identifier texture, float scale) {
        super(context, ModelType.PIGLIN, TextureSupplier.of(texture), scale);
    }

    public static PonyPiglinRenderer piglin(EntityRendererProvider.Context context) {
        return new PonyPiglinRenderer(context, PIGLIN, 1);
    }

    public static PonyPiglinRenderer brute(EntityRendererProvider.Context context) {
        return new PonyPiglinRenderer(context, PIGLIN_BRUTE, 1.15F);
    }

    public static PonyPiglinRenderer zombified(EntityRendererProvider.Context context) {
        return new PonyPiglinRenderer(context, ZOMBIFIED_PIGLIN, 1);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public ArmPose getArmPose(Mob entity, HumanoidArm arm) {
        if (entity instanceof AbstractPiglin piglin) {
            return switch (arm) {
                case LEFT -> switch (piglin.getArmPose()) {
                    case CROSSBOW_HOLD -> ArmPose.CROSSBOW_HOLD;
                    case CROSSBOW_CHARGE -> ArmPose.CROSSBOW_CHARGE;
                    default -> ArmPose.EMPTY;
                };
                case RIGHT -> switch (piglin.getArmPose()) {
                    case ADMIRING_ITEM -> ArmPose.ITEM;
                    default -> ArmPose.EMPTY;
                };
            };
        }

        return super.getArmPose(entity, arm);
    }

    public void extractRenderState(Mob entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.zombified = entity instanceof ZombifiedPiglin;
        state.activity = entity instanceof AbstractPiglin piglin ? piglin.getArmPose() : PiglinArmPose.DEFAULT;
        state.shaking |= entity instanceof AbstractPiglin piglin && piglin.isConverting();
    }

    public static class State extends HostilePonyRenderState {
        public boolean zombified;
        public boolean shaking;
        public PiglinArmPose activity = PiglinArmPose.DEFAULT;
    }
}
