package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelEnderStallion;
import com.minelittlepony.client.render.RenderPonyMob;
import com.minelittlepony.client.render.layer.LayerEyeGlow;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.layer.LayerEyeGlow.IGlowingRenderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RenderEnderStallion extends RenderPonyMob<EntityEnderman> implements IGlowingRenderer {

    public static final ResourceLocation ENDERMAN = new ResourceLocation("minelittlepony", "textures/entity/enderman/enderman_pony.png");
    private static final ResourceLocation EYES = new ResourceLocation("minelittlepony", "textures/entity/enderman/enderman_pony_eyes.png");

    private final Random rnd = new Random();

    private static final ModelWrapper MODEL_WRAPPER = new ModelWrapper(new ModelEnderStallion());

    public RenderEnderStallion(RenderManager manager) {
        super(manager, MODEL_WRAPPER);
    }

    @Override
    protected void addLayers() {
        addLayer(createItemHoldingLayer());
        addLayer(new LayerArrow(this));
        addLayer(new LayerEyeGlow<>(this));
    }

    @Override
    protected LayerHeldPonyItem<EntityEnderman> createItemHoldingLayer() {
        return new LayerHeldPonyItemMagical<EntityEnderman>(this) {
            @Override
            protected ItemStack getRightItem(EntityEnderman entity) {
                IBlockState state = entity.func_195405_dq();
                if (state == null) {
                    return ItemStack.EMPTY;
                }

                return new ItemStack(state.getBlock().asItem());
            }
        };
    }

    @Override
    public ResourceLocation getTexture(EntityEnderman entity) {
        return ENDERMAN;
    }

    @Override
    public void doRender(EntityEnderman entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelEnderStallion modelenderman = (ModelEnderStallion)getMainModel();

        modelenderman.isCarrying = entity.func_195405_dq() != null;
        modelenderman.isAttacking = entity.isScreaming();

        if (entity.isScreaming()) {
            x += rnd.nextGaussian() / 50;
            z += rnd.nextGaussian() / 50;
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    public ResourceLocation getEyeTexture() {
        return EYES;
    }
}
