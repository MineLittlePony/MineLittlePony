package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.util.render.FormattedTextureSupplier;
import com.minelittlepony.util.render.ITextureSupplier;
import com.minelittlepony.util.render.IntStringMapper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVillager extends RenderPonyMob<EntityVillager> {

    /**
     * Key mapping from a villager profession id to a human readable name
     */
    public static final IntStringMapper MAPPER = new IntStringMapper("farmer", "librarian", "priest", "smith", "butcher", "villager");

    private static final ITextureSupplier<String> FORMATTER = new FormattedTextureSupplier("minelittlepony", "textures/entity/villager/%s_pony.png");

    private static final ResourceLocation DEFAULT = FORMATTER.supplyTexture("villager");
    private static final ResourceLocation EGG = FORMATTER.supplyTexture("silly");
    private static final ResourceLocation EGG_2 = FORMATTER.supplyTexture("tiny_silly");

    private static final ITextureSupplier<Integer> PROFESSIONS = new VillagerProfessionTextureCache(FORMATTER, MAPPER, DEFAULT);

    public RenderPonyVillager(RenderManager manager) {
        super(manager, PMAPI.villager);
    }

    @Override
    public void preRenderCallback(EntityVillager villager, float ticks) {
        super.preRenderCallback(villager, ticks);
        GlStateManager.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    @Override
    public ResourceLocation getTexture(EntityVillager entity) {
        String name = entity.getCustomNameTag();
        if ("Derpy".equals(name) || (entity.isChild() && "Dinky".equals(name))) {
            if (entity.isChild()) {
                return EGG_2;
            }
            return EGG;
        }

        return PROFESSIONS.supplyTexture(entity.getProfession());
    }
}
