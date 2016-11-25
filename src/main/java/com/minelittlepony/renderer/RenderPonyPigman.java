package com.minelittlepony.renderer;

import com.minelittlepony.model.PMAPI;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyPigman extends RenderPonyMob<EntityPigZombie> {

    private static final ResourceLocation PIGMAN = new ResourceLocation("minelittlepony", "textures/entity/zombie/zombie_pigman_pony.png");

    public RenderPonyPigman(RenderManager renderManager) {
        super(renderManager, PMAPI.pony);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPigZombie entity) {
        return getTexture(PIGMAN);
    }

}
