package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.util.render.FormattedTextureSupplier;
import com.minelittlepony.util.render.ITextureSupplier;
import com.minelittlepony.util.render.IntStringMapper;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombieVillager extends RenderPonyMob<EntityZombieVillager> {

    /**
     * Ditto.
     * @see RenderPonyVillager.MAPPER
     */
    public static final IntStringMapper MAPPER = new IntStringMapper("farmer", "librarian", "priest", "smith", "butcher", "villager");

    private static final ITextureSupplier<String> FORMATTER = new FormattedTextureSupplier("minelittlepony", "textures/entity/zombie_villager/zombie_%s_pony.png");

    private static final ResourceLocation DEFAULT = FORMATTER.supplyTexture("villager");
    private static final ResourceLocation EGG = FORMATTER.supplyTexture("silly");
    private static final ResourceLocation EGG_2 = FORMATTER.supplyTexture("tiny_silly");

    private static final ITextureSupplier<Integer> PROFESSIONS = new VillagerProfessionTextureCache(FORMATTER, MAPPER, DEFAULT);

    public RenderPonyZombieVillager(RenderManager manager) {
        super(manager, PMAPI.zombieVillager);
    }

    @Override
    public ResourceLocation getTexture(EntityZombieVillager entity) {
        String name = entity.getCustomNameTag();
        if ("Derpy".equals(name) || (entity.isChild() && "Dinky".equals(name))) {
            if (entity.isChild()) {
                return EGG_2;
            }
            return EGG;
        }

        return PROFESSIONS.supplyTexture(entity.getProfession());
    }

    @Override
    protected void applyRotations(EntityZombieVillager entity, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.ticksExisted * 3.25D) * (Math.PI / 4));
        }

        super.applyRotations(entity, move, rotationYaw, ticks);
    }
}
