package com.minelittlepony.renderer;

import java.util.Random;

import com.minelittlepony.PonyGender;
import com.minelittlepony.PonyRace;
import com.minelittlepony.PonySize;
import com.minelittlepony.TailLengths;
import com.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombie<Zombie extends EntityZombie> extends RenderPonyMob<Zombie> {

    private static final ResourceLocation ZOMBIE = new ResourceLocation("minelittlepony", "textures/entity/zombie/zombie_pony.png");
    private static final ResourceLocation HUSK = new ResourceLocation("minelittlepony", "textures/entity/zombie/zombie_husk_pony.png");

    public RenderPonyZombie(RenderManager rendermanager) {
        super(rendermanager, PMAPI.zombie);
    }

    @Override
    protected void preRenderCallback(Zombie entity, float partick) {
        super.preRenderCallback(entity, partick);
        Random rand = new Random(entity.getUniqueID().hashCode());

        // 50-50 chance for gender
        this.playerModel.getModel().metadata.setGender(rand.nextBoolean() ? PonyGender.MARE : PonyGender.STALLION);

        // races
        switch (rand.nextInt(2) + 2) {
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

    private static <T extends Enum<T>> T randEnum(Random rand, Class<T> en) {
        T[] values = en.getEnumConstants();
        return values[rand.nextInt(values.length)];
    }

    @Override
    protected ResourceLocation getEntityTexture(Zombie zombie) {
        return getTexture(ZOMBIE);
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
        protected ResourceLocation getEntityTexture(EntityHusk zombie) {
            return getTexture(HUSK);
        }

    }

}
