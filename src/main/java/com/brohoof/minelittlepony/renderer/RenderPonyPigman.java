package com.brohoof.minelittlepony.renderer;

import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyPigman extends RenderPonyMob<EntityPigZombie> {

    public RenderPonyPigman(RenderManager renderManager) {
        super(renderManager, PMAPI.pony);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPigZombie entity) {
        return PonyManager.PIGMAN;
    }

}
