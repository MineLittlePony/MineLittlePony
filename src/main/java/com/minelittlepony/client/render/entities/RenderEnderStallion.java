package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelEnderStallion;
import com.minelittlepony.client.render.layer.LayerEyeGlow;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.layer.LayerEyeGlow.IGlowingRenderer;

import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Random;

public class RenderEnderStallion extends RenderPonyMob<EndermanEntity, ModelEnderStallion> implements IGlowingRenderer {

    public static final Identifier ENDERMAN = new Identifier("minelittlepony", "textures/entity/enderman/enderman_pony.png");
    private static final Identifier EYES = new Identifier("minelittlepony", "textures/entity/enderman/enderman_pony_eyes.png");

    private final Random rnd = new Random();

    public RenderEnderStallion(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, new ModelEnderStallion());
    }

    @Override
    protected void addLayers() {
        addFeature(createItemHoldingLayer());
        addFeature(new StuckArrowsFeatureRenderer<>(this));
        addFeature(new LayerEyeGlow<>(this));
    }

    @Override
    protected LayerHeldPonyItem<EndermanEntity, ModelEnderStallion> createItemHoldingLayer() {
        return new LayerHeldPonyItemMagical<EndermanEntity, ModelEnderStallion>(this) {
            @Override
            protected ItemStack getRightItem(EndermanEntity entity) {
                BlockState state = entity.getCarriedBlock();
                if (state == null) {
                    return ItemStack.EMPTY;
                }

                return new ItemStack(state.getBlock().asItem());
            }
        };
    }

    @Override
    public Identifier findTexture(EndermanEntity entity) {
        return ENDERMAN;
    }

    @Override
    public void render(EndermanEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelEnderStallion modelenderman = getModel();

        modelenderman.isCarrying = entity.getCarriedBlock() != null;
        modelenderman.isAttacking = entity.isAngry();

        if (entity.isAngry()) {
            x += rnd.nextGaussian() / 50;
            z += rnd.nextGaussian() / 50;
        }

        super.render(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    public Identifier getEyeTexture() {
        return EYES;
    }
}
