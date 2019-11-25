package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ModelZombiePony;

public class RenderPonyZombie<Zombie extends ZombieEntity> extends RenderPonyMob.Caster<Zombie, ModelZombiePony<Zombie>> {

    public static final Identifier ZOMBIE = new Identifier("minelittlepony", "textures/entity/zombie/zombie_pony.png");
    public static final Identifier HUSK = new Identifier("minelittlepony", "textures/entity/zombie/husk_pony.png");
    public static final Identifier PIGMAN = new Identifier("minelittlepony", "textures/entity/zombie/zombie_pigman_pony.png");
    public static final Identifier DROWNED = new Identifier("minelittlepony", "textures/entity/zombie/drowned_pony.png");

    public RenderPonyZombie(EntityRenderDispatcher manager) {
        super(manager, ModelType.ZOMBIE);
    }

    @Override
    public Identifier findTexture(Zombie entity) {
        return ZOMBIE;
    }

    public static class Drowned extends RenderPonyZombie<DrownedEntity> {

        public Drowned(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public Identifier findTexture(DrownedEntity entity) {
            return DROWNED;
        }
    }

    public static class Pigman extends RenderPonyZombie<ZombiePigmanEntity> {

        public Pigman(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public Identifier findTexture(ZombiePigmanEntity entity) {
            return PIGMAN;
        }
    }

    public static class Husk extends RenderPonyZombie<HuskEntity> {

        public Husk(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public void scale(HuskEntity entity, MatrixStack stack, float ticks) {
            super.scale(entity, stack, ticks);
            stack.scale(1.0625F, 1.0625F, 1.0625F);
        }

        @Override
        public Identifier findTexture(HuskEntity entity) {
            return HUSK;
        }

    }

    public static class Giant extends RenderPonyMob.Caster<GiantEntity, ModelZombiePony<GiantEntity>> {

		public Giant(EntityRenderDispatcher manager) {
			super(manager, ModelType.ZOMBIE);
		}

		@Override
		public void scale(GiantEntity entity, MatrixStack stack, float ticks) {
	        super.scale(entity, stack, ticks);
	        stack.scale(3, 3, 3);
	    }

		@Override
	    public Identifier findTexture(GiantEntity entity) {
	        return ZOMBIE;
	    }
    }
}
