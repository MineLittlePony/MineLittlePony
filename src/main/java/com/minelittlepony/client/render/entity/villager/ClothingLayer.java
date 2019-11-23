package com.minelittlepony.client.render.entity.villager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.RenderPony;

class ClothingLayer<
    T extends LivingEntity & VillagerDataContainer,
    M extends EntityModel<T> & IPonyModel<T> & ModelWithHat,
    C extends FeatureRendererContext<T, M> & IPonyRender<T, M>> extends VillagerClothingFeatureRenderer<T, M> {

    private final RenderPony<T, M> renderer;

    public ClothingLayer(C context, String type) {
        super(context, (ReloadableResourceManager)MinecraftClient.getInstance().getResourceManager(), type);
        renderer = context.getInternalRenderer();
    }

    public static Identifier getClothingTexture(VillagerDataContainer entity, String entityType) {
        VillagerProfession profession = entity.getVillagerData().getProfession();

        return createTexture("minelittlepony", "profession", entityType, Registry.VILLAGER_PROFESSION.getId(profession));
    }

    public static Identifier getHatTexture(VillagerDataContainer entity, String entityType) {
        VillagerType villagerType = entity.getVillagerData().getType();

        return createTexture("minecraft", "type", entityType, Registry.VILLAGER_TYPE.getId(villagerType));
    }

    public static Identifier createTexture(String namespace, String type, String entityType, Identifier profession) {
        return new Identifier(namespace, String.format("textures/entity/%s/%s/%s.png", entityType, type, profession.getPath()));
    }

    @Override
    public void bindTexture(Identifier texture) {

        if (texture != SpriteAtlasTexture.BLOCK_ATLAS_TEX) {
            if (!"minelittlepony".contentEquals(texture.getNamespace())) {
                texture = new Identifier("minelittlepony", texture.getPath());
            }
        }

        renderer.updateMetadata(texture);

        super.bindTexture(texture);
    }
}
