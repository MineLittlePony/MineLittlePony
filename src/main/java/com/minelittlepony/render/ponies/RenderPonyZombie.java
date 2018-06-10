package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombie<Zombie extends EntityZombie> extends RenderPonyMob<Zombie> {

    public static final ResourceLocation ZOMBIE = new ResourceLocation("minelittlepony", "textures/entity/zombie/zombie_pony.png");
    public static final ResourceLocation HUSK = new ResourceLocation("minelittlepony", "textures/entity/zombie/husk_pony.png");

    public RenderPonyZombie(RenderManager manager) {
        super(manager, PMAPI.zombie);
    }

    @Override
    protected ResourceLocation getTexture(Zombie entity) {
        return ZOMBIE;
    }

    public static class Husk extends RenderPonyZombie<EntityHusk> {

        public Husk(RenderManager manager) {
            super(manager);
        }

        @Override
        protected void preRenderCallback(EntityHusk entity, float ticks) {
            super.preRenderCallback(entity, ticks);
            GlStateManager.scale(1.0625F, 1.0625F, 1.0625F);
        }

        @Override
        protected ResourceLocation getTexture(EntityHusk entity) {
            return HUSK;
        }

    }

    public static class Giant extends RenderPonyMob<EntityGiantZombie> {

		public Giant(RenderManager manager) {
			super(manager, PMAPI.zombie);
		}

		@Override
		protected void preRenderCallback(EntityGiantZombie entity, float ticks) {
	        super.preRenderCallback(entity, ticks);
	        GlStateManager.scale(3, 3, 3);
	    }

		@Override
	    protected ResourceLocation getTexture(EntityGiantZombie entity) {
	        return ZOMBIE;
	    }
    }

  //TODO: MC1.13 EntityDrowned
}
