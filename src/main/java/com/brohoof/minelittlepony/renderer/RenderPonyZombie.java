package com.brohoof.minelittlepony.renderer;

import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombie extends RenderPonyMob<EntityZombie> {

    public RenderPonyZombie(RenderManager rendermanager) {
        super(rendermanager, PMAPI.zombiePony);
    }

    @Override
    protected void rotateCorpse(EntityZombie zombie, float xPosition, float yPosition, float zPosition) {
        if (zombie.isConverting()) {
            yPosition += (float) (Math.cos(zombie.ticksExisted * 3.25D) * 3.141592653589793D * 0.25D);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityZombie zombie) {
        return zombie instanceof EntityPigZombie ? PonyManager.zombiePigmanPonyResource
                : (zombie.isVillager() ? PonyManager.zombieVillagerPonyResource
                        : PonyManager.zombiePonyResource);
    }

}
