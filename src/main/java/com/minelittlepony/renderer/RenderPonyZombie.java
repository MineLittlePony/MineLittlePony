package com.minelittlepony.renderer;

import com.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombie<Zombie extends EntityZombie> extends RenderPonyMob<Zombie> {

    private static final ResourceLocation ZOMBIE = new ResourceLocation("minelittlepony", "textures/entity/zombie/zombie_pony.png");
    private static final ResourceLocation HUSK = new ResourceLocation("minelittlepony", "textures/entity/zombie/husk_pony.png");

    public RenderPonyZombie(RenderManager rendermanager) {
        super(rendermanager, PMAPI.zombie);
    }

    @Override
    protected ResourceLocation getTexture(Zombie zombie) {
        return ZOMBIE;
    }

    public static class Husk extends RenderPonyZombie<EntityHusk> {

        public Husk(RenderManager rendermanager) {
            super(rendermanager);
        }

        @Override
        protected void preRenderCallback(EntityHusk entitylivingbaseIn, float partialTickTime) {
            GlStateManager.scale(1.0625F, 1.0625F, 1.0625F);
            super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        }

        @Override
        protected ResourceLocation getTexture(EntityHusk zombie) {
            return HUSK;
        }

    }
    
    public static class Giant extends RenderPonyMob<EntityGiantZombie> {

		public Giant(RenderManager renderManager) {
			super(renderManager, PMAPI.zombie);
		}
		
		@Override
		protected void preRenderCallback(EntityGiantZombie entitylivingbaseIn, float partialTickTime) {
	        GlStateManager.scale(3, 3, 3);
	        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
	    }
    	
		@Override
	    protected ResourceLocation getTexture(EntityGiantZombie zombie) {
	        return ZOMBIE;
	    }
    }
}
