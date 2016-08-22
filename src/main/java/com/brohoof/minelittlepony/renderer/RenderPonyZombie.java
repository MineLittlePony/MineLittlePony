package com.brohoof.minelittlepony.renderer;

import java.util.Random;

import com.brohoof.minelittlepony.PonyGender;
import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.PonyRace;
import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.TailLengths;
import com.brohoof.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombie extends RenderPonyMob<EntityZombie> {

    public RenderPonyZombie(RenderManager rendermanager) {
        super(rendermanager, PMAPI.zombie);
    }

    @Override
    protected void preRenderCallback(EntityZombie entity, float partick) {
        super.preRenderCallback(entity, partick);
        Random rand = new Random(entity.getUniqueID().hashCode());

        // 50-50 chance for gender
        this.playerModel.getModel().metadata.setGender(rand.nextBoolean() ? PonyGender.MARE : PonyGender.STALLION);

        // races
        switch (rand.nextInt(2)+2) {
        case 0:
        case 1:
            this.playerModel.getModel().metadata.setRace(PonyRace.EARTH);
            break;
        case 2:
            this.playerModel.getModel().metadata.setRace(PonyRace.PEGASUS);
            break;
        case 3:
            this.playerModel.getModel().metadata.setRace(PonyRace.UNICORN);
            break;
        }
        // Let's play the lottery!
        if (rand.nextInt(10000) == 0) {
            this.playerModel.getModel().metadata.setRace(PonyRace.ALICORN);
        }
        // sizes
        if (entity.isChild()) {
            this.playerModel.getModel().metadata.setSize(PonySize.FOAL);
        } else {
            PonySize size = randEnum(rand, PonySize.class);
            this.playerModel.getModel().metadata.setSize(size != PonySize.FOAL ? size : PonySize.NORMAL);
        }
        this.playerModel.getModel().metadata.setTail(randEnum(rand, TailLengths.class));

        // glow
        this.playerModel.getModel().metadata.setGlowColor(rand.nextInt());

    }

    private <T extends Enum<T>> T randEnum(Random rand, Class<T> en) {
        T[] values = en.getEnumConstants();
        return values[rand.nextInt(values.length)];
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
        ResourceLocation loc = PonyManager.ZOMBIES.get(zombie.getZombieType());
        if (loc == null) {
            loc = zombie.isVillager() ? PonyManager.ZOMBIE_VILLAGER : PonyManager.ZOMBIE;
        }
        return loc;
    }

}
