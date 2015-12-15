package com.brohoof.minelittlepony.renderer;

import java.util.Random;

import com.brohoof.minelittlepony.PonyGender;
import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.PonyRace;
import com.brohoof.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombie extends RenderPonyMob<EntityZombie> {

    public RenderPonyZombie(RenderManager rendermanager) {
        super(rendermanager, PMAPI.zombie);
    }

    @Override
    protected void preRenderCallback(EntityZombie entity, float partick) {
        Random rand = new Random(entity.getUniqueID().hashCode());

        // 50-50 chance for gender
        this.playerModel.getModel().metadata.setGender(rand.nextBoolean() ? PonyGender.MARE : PonyGender.STALLION);

        switch (rand.nextInt(5)) {
        case 0:
        case 1:
        case 2:
            this.playerModel.getModel().metadata.setRace(PonyRace.EARTH);
            break;
        case 3:
            this.playerModel.getModel().metadata.setRace(PonyRace.PEGASUS);
            break;
        case 4:
            this.playerModel.getModel().metadata.setRace(PonyRace.UNICORN);
            break;
        }
        this.playerModel.getModel().metadata.setGlowColor(rand.nextInt());
        // Let's play the lottery!
        if (rand.nextInt(10000) == 0) {
            this.playerModel.getModel().metadata.setRace(PonyRace.ALICORN);
        }
    }

    @Override
    protected void rotateCorpse(EntityZombie zombie, float xPosition, float yPosition, float zPosition) {
        super.rotateCorpse(zombie, xPosition, yPosition, zPosition);
        if (zombie.isConverting()) {
            yPosition += (float) (Math.cos(zombie.ticksExisted * 3.25D) * Math.PI * 0.25D);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityZombie zombie) {
        return zombie instanceof EntityPigZombie ? PonyManager.PIGMAN
                : (zombie.isVillager() ? PonyManager.ZOMBIE_VILLAGER
                        : PonyManager.ZOMBIE);
    }

}
