package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ZomponyModel;

public class ZomponyRenderer<Zombie extends ZombieEntity> extends PonyRenderer.Caster<Zombie, ZomponyModel<Zombie>> {

    public static final Identifier ZOMBIE = new Identifier("minelittlepony", "textures/entity/zombie/zombie_pony.png");
    public static final Identifier HUSK = new Identifier("minelittlepony", "textures/entity/zombie/husk_pony.png");
    public static final Identifier DROWNED = new Identifier("minelittlepony", "textures/entity/zombie/drowned_pony.png");

    public ZomponyRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ZOMBIE);
    }

    @Override
    public Identifier getTexture(Zombie entity) {
        return ZOMBIE;
    }

    public static class Drowned extends ZomponyRenderer<DrownedEntity> {

        public Drowned(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public Identifier getTexture(DrownedEntity entity) {
            return DROWNED;
        }
    }

    public static class Husk extends ZomponyRenderer<HuskEntity> {

        public Husk(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public void scale(HuskEntity entity, MatrixStack stack, float ticks) {
            super.scale(entity, stack, ticks);
            stack.scale(1.0625F, 1.0625F, 1.0625F);
        }

        @Override
        public Identifier getTexture(HuskEntity entity) {
            return HUSK;
        }

    }

    public static class Giant extends PonyRenderer.Caster<GiantEntity, ZomponyModel<GiantEntity>> {

		public Giant(EntityRendererFactory.Context context) {
			super(context, ModelType.ZOMBIE);
		}

		@Override
		public void scale(GiantEntity entity, MatrixStack stack, float ticks) {
	        super.scale(entity, stack, ticks);
	        stack.scale(3, 3, 3);
	    }

		@Override
	    public Identifier getTexture(GiantEntity entity) {
	        return ZOMBIE;
	    }
    }
}
