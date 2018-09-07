package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.ponies.ModelEnderStallion;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.layer.LayerEyeGlow;
import com.minelittlepony.render.layer.LayerHeldPonyItem;
import com.minelittlepony.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.render.layer.LayerEyeGlow.IGlowingRenderer;

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

    public RenderEnderStallion(RenderManager manager) {
        super(manager, PMAPI.enderman);
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
                IBlockState state = entity.getHeldBlockState();
                if (state == null) {
                    return ItemStack.EMPTY;
                }
                return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            }
        };
    }

    @Override
    protected ResourceLocation getTexture(EntityEnderman entity) {
        return ENDERMAN;
    }

    @Override
    public void doRender(EntityEnderman entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelEnderStallion modelenderman = (ModelEnderStallion)getMainModel();

        modelenderman.isCarrying = entity.getHeldBlockState() != null;
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
