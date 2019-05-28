package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IPonyModel;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.village.VillagerDataContainer;

import static com.mojang.blaze3d.platform.GlStateManager.*;

public class LayerPonyCustomHead<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    public LayerPonyCustomHead(IPonyRender<T, M> renderPony) {
        super(renderPony);
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        ItemStack itemstack = entity.getEquippedStack(EquipmentSlot.HEAD);
        if (!itemstack.isEmpty()) {
            M model = getContext().getModelWrapper().getBody();
            Item item = itemstack.getItem();

            pushMatrix();

            model.transform(BodyPart.HEAD);
            model.getHead().applyTransform(0.0625f);

            if (model instanceof AbstractPonyModel) {
                translatef(0, 0.2F, 0);
            } else {
                translatef(0, 0, 0.15F);
            }

            color4f(1, 1, 1, 1);

            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                boolean isVillager = entity instanceof VillagerDataContainer;

                renderSkull(itemstack, isVillager, move);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
                renderBlock(entity, itemstack);
            }

            popMatrix();
        }

    }

    private void renderBlock(T entity, ItemStack itemstack) {
        rotatef(180, 0, 1, 0);
        scalef(0.625F, -0.625F, -0.625F);
        translatef(0, 0.4F, -0.21F);

        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(entity, itemstack, ModelTransformation.Type.HEAD);
    }

    private void renderSkull(ItemStack itemstack, boolean isVillager, float limbSwing) {
        translatef(0, 0, -0.14F);
        float f = 1.1875f;
        scalef(f, -f, -f);
        if (isVillager) {
            translatef(0, 0.0625F, 0);
        }
        translatef(0, 0, -0.05F);
        GameProfile profile = null;

        if (itemstack.hasTag()) {
            CompoundTag nbt = itemstack.getTag();

            assert nbt != null;

            if (nbt.containsKey("SkullOwner", 10)) {
                profile = TagHelper.deserializeProfile(nbt.getCompound("SkullOwner"));
            } else if (nbt.containsKey("SkullOwner", 8)) {
                profile = SkullBlockEntity.loadProperties(new GameProfile(null, nbt.getString("SkullOwner")));
                nbt.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), profile));
            }
        }

        SkullType type = ((AbstractSkullBlock) ((BlockItem) itemstack.getItem()).getBlock()).getSkullType();

        PonySkullRenderer.resolve().render(-0.5F, 0, -0.45F, Direction.UP, 180, type, profile, -1, limbSwing);
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }

}
