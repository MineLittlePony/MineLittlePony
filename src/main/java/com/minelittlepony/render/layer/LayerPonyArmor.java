package com.minelittlepony.render.layer;

import com.google.common.collect.Maps;
import com.minelittlepony.ForgeProxy;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.util.coordinates.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Map;

public class LayerPonyArmor<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private static final Map<String, ResourceLocation> HUMAN_ARMORS = Maps.newHashMap();
    private static final Map<ResourceLocation, ResourceLocation> PONY_ARMORS = Maps.newHashMap();

    private ModelWrapper pony;

    public LayerPonyArmor(RenderLivingBase<T> renderer) {
        super(renderer);
    }

    @Override
    public void doPonyRender(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        pony = ((IRenderPony) getRenderer()).getPlayerModel();

        for (EntityEquipmentSlot i : EntityEquipmentSlot.values()) {
            if (i.getSlotType() == Type.ARMOR) {
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i);
            }
        }
    }

    private void renderArmor(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armorSlot) {
        ItemStack itemstack = entity.getItemStackFromSlot(armorSlot);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemArmor) {

            ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

            AbstractPonyModel modelbase = pony.getArmor().getArmorForSlot(armorSlot);
            modelbase = getArmorModel(entity, itemstack, armorSlot, modelbase);
            modelbase.setModelAttributes(pony.getModel());
            modelbase.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

            Tuple<ResourceLocation, Boolean> armors = getArmorTexture(entity, itemstack, armorSlot, null);
            prepareToRender((ModelPonyArmor) modelbase, armorSlot, armors.getSecond());

            getRenderer().bindTexture(armors.getFirst());
            if (itemarmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
                Color.glColor(itemarmor.getColor(itemstack), 1);
                modelbase.render(entity, move, swing, ticks, headYaw, headPitch, scale);
                armors = getArmorTexture(entity, itemstack, armorSlot, "overlay");
                getRenderer().bindTexture(armors.getFirst());
            }
            GlStateManager.color(1, 1, 1, 1);
            modelbase.render(entity, move, swing, ticks, headYaw, headPitch, scale);

            if (itemstack.isItemEnchanted()) {
                renderEnchantment(entity, modelbase, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
            }
        }
    }

    private Tuple<ResourceLocation, Boolean> getArmorTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, @Nullable String type) {
        ItemArmor item = (ItemArmor) itemstack.getItem();
        String texture = item.getArmorMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, slot == EntityEquipmentSlot.LEGS ? 2 : 1,
                type == null ? "" : String.format("_%s", type));
        s1 = getArmorTexture(entity, itemstack, s1, slot, type);
        ResourceLocation human = getHumanResource(s1);
        ResourceLocation pony = getPonyResource(human);

        // check resource packs for either texture.
        for (ResourcePackRepository.Entry entry : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {
            if (entry.getResourcePack().resourceExists(pony)) {
                // ponies are more important
                return new Tuple<>(pony, true);
            } else if (entry.getResourcePack().resourceExists(human)) {
                // but I guess I'll take a human
                return new Tuple<>(human, false);
            }
        }
        // the default pack
        try {
            Minecraft.getMinecraft().getResourceManager().getResource(pony);
            return new Tuple<>(pony, true);
        } catch (IOException e) {
            return new Tuple<>(human, false);
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void prepareToRender(ModelPonyArmor model, EntityEquipmentSlot slot, boolean isPony) {
        model.setVisible(false);

        switch (slot) {
            // feet
            case FEET:
                model.bipedRightArm.showModel = true;
                model.bipedLeftArm.showModel = true;
                model.bipedRightLeg.showModel = !isPony;
                model.bipedLeftLeg.showModel = !isPony;
                model.leftLegging.showModel = isPony;
                model.rightLegging.showModel = isPony;
                break;
            // legs
            case LEGS:
                model.bipedRightLeg.showModel = !isPony;
                model.bipedLeftLeg.showModel = !isPony;
                model.bipedRightArm.showModel = true;
                model.bipedLeftArm.showModel = true;
                model.bipedBody.showModel = !isPony;
                model.flankGuard.showModel = !isPony;
                model.saddle.showModel = isPony;
                model.leftLegging.showModel = isPony;
                model.rightLegging.showModel = isPony;
                break;
            // chest
            case CHEST:
                model.saddle.showModel = isPony;
                model.bipedBody.showModel = !isPony;
                model.flankGuard.showModel = !isPony;
                break;
            // head
            case HEAD:
                model.bipedHead.showModel = true;
                model.helmet.showModel = isPony;
        }
    }

    private void renderEnchantment(T entity, ModelBase model, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        getRenderer().bindTexture(ENCHANTED_ITEM_GLINT_RES);

        GlStateManager.enableBlend();
        GlStateManager.depthFunc(GL11.GL_EQUAL);
        GlStateManager.depthMask(false);

        float brightness = 0.5F;
        GlStateManager.color(brightness, brightness, brightness, 1);

        float baseYOffset = entity.ticksExisted + partialTicks;
        float glintBrightness = 0.76F;
        float scaleFactor = 0.33333334F;

        for (int i = 0; i < 2; i++) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);

            GlStateManager.color(glintBrightness / 2, glintBrightness / 4, 0.8F * glintBrightness, 1);

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();


            GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
            GlStateManager.rotate(30 - i * 60, 0, 0, 1);
            GlStateManager.translate(0, baseYOffset * (0.02F + i * 0.06F), 0);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            model.render(entity, move, swing, ticks, headYaw, headPitch, scale);
        }

        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.disableBlend();
    }

    private static ResourceLocation getHumanResource(String s1) {
        return HUMAN_ARMORS.computeIfAbsent(s1, ResourceLocation::new);
    }

    private static ResourceLocation getPonyResource(ResourceLocation human) {
        ResourceLocation pony = PONY_ARMORS.get(human);
        if (pony == null) {
            String domain = human.getResourceDomain();
            String path = human.getResourcePath();
            if (domain.equals("minecraft")) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }
            path = path.replace(".png", "_pony.png");
            pony = new ResourceLocation(domain, path);
            PONY_ARMORS.put(human, pony);
        }
        return pony;
    }

    private static String getArmorTexture(EntityLivingBase entity, ItemStack item, String def, EntityEquipmentSlot slot, @Nullable String type) {
        return ForgeProxy.getArmorTexture(entity, item, def, slot, type);
    }

    private static AbstractPonyModel getArmorModel(EntityLivingBase entity, ItemStack itemstack, EntityEquipmentSlot slot, AbstractPonyModel def) {
        ModelBase model = ForgeProxy.getArmorModel(entity, itemstack, slot, def);
        if (model instanceof ModelPonyArmor) {
            return (AbstractPonyModel) model;
        }

        return def;
    }

}
