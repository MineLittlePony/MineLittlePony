package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelVillagerPony;
import com.minelittlepony.client.render.RenderPonyMob;
import com.minelittlepony.util.resources.FormattedTextureSupplier;
import com.minelittlepony.util.resources.ITextureSupplier;
import com.minelittlepony.util.resources.IntStringMapper;

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

    private static final ModelWrapper MODEL_WRAPPER = new ModelWrapper(new ModelVillagerPony());

    public RenderPonyVillager(RenderManager manager) {
        super(manager, MODEL_WRAPPER);
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
