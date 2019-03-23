package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelZombiePony;
import com.minelittlepony.client.render.RenderPonyMob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombie<Zombie extends EntityZombie> extends RenderPonyMob<Zombie> {

    public static final ResourceLocation ZOMBIE = new ResourceLocation("minelittlepony", "textures/entity/zombie/zombie_pony.png");
    public static final ResourceLocation HUSK = new ResourceLocation("minelittlepony", "textures/entity/zombie/husk_pony.png");
    public static final ResourceLocation PIGMAN = new ResourceLocation("minelittlepony", "textures/entity/zombie/zombie_pigman_pony.png");

    private static final ModelWrapper MODEL_WRAPPER = new ModelWrapper(new ModelZombiePony());

    public RenderPonyZombie(RenderManager manager) {
        super(manager, MODEL_WRAPPER);
    }

    @Override
    public ResourceLocation getTexture(Zombie entity) {
        return ZOMBIE;
    }

    public static class Pigman extends RenderPonyZombie<EntityPigZombie> {

        public Pigman(RenderManager manager) {
            super(manager);
        }

        @Override
        public ResourceLocation getTexture(EntityPigZombie entity) {
            return PIGMAN;
        }
    }

    public static class Husk extends RenderPonyZombie<EntityHusk> {

        public Husk(RenderManager manager) {
            super(manager);
        }

        @Override
        public void preRenderCallback(EntityHusk entity, float ticks) {
            super.preRenderCallback(entity, ticks);
            GlStateManager.scale(1.0625F, 1.0625F, 1.0625F);
        }

        @Override
        public ResourceLocation getTexture(EntityHusk entity) {
            return HUSK;
        }

    }

    public static class Giant extends RenderPonyMob<EntityGiantZombie> {

		public Giant(RenderManager manager) {
			super(manager, MODEL_WRAPPER);
		}

		@Override
		public void preRenderCallback(EntityGiantZombie entity, float ticks) {
	        super.preRenderCallback(entity, ticks);
	        GlStateManager.scale(3, 3, 3);
	    }

		@Override
	    public ResourceLocation getTexture(EntityGiantZombie entity) {
	        return ZOMBIE;
	    }
    }

  //TODO: MC1.13 EntityDrowned
}
