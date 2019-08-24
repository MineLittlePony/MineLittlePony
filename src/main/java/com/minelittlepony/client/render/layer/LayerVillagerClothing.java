package com.minelittlepony.client.render.layer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;

public class LayerVillagerClothing<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat> extends VillagerClothingFeatureRenderer<T, M> {

    public LayerVillagerClothing(FeatureRendererContext<T, M> context, String type) {
        super(context, (ReloadableResourceManager)MinecraftClient.getInstance().getResourceManager(), type);
    }

    @Override
    public void bindTexture(Identifier texture) {

        if (texture != SpriteAtlasTexture.BLOCK_ATLAS_TEX) {
            if (!"minelittlepony".contentEquals(texture.getNamespace())) {
                texture = new Identifier("minelittlepony", texture.getPath());
            }
        }

        super.bindTexture(texture);
    }
}
