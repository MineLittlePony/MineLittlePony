package com.brohoof.minelittlepony.renderer.layer;

import java.io.IOException;
import java.util.Map;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.ducks.IRenderPony;
import com.brohoof.minelittlepony.forge.IPonyArmor;
import com.brohoof.minelittlepony.model.AbstractPonyModel;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.ModelHumanPlayer;
import com.brohoof.minelittlepony.model.pony.armor.ModelPonyArmor;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerPonyArmor implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private static final Map<String, ResourceLocation> HUMAN_ARMORS = Maps.newHashMap();
    private static final Map<ResourceLocation, ResourceLocation> PONY_ARMORS = Maps.newHashMap();

    private RenderLivingBase<? extends EntityLivingBase> renderer;
    private LayerBipedArmor humanArmor;
    private PlayerModel pony;

    public LayerPonyArmor(RenderLivingBase<? extends EntityLivingBase> entity) {
        this.renderer = entity;
        this.humanArmor = new LayerBipedArmor(entity);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ticks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        pony = ((IRenderPony) renderer).getPony();
        if (pony.getModel() instanceof ModelHumanPlayer) {
            humanArmor.doRenderLayer(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale);
        } else {
            renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
            renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
            renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
            renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);

        }
    }

    private void renderArmor(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale,
            EntityEquipmentSlot armorSlot) {
        ItemStack itemstack = entity.getItemStackFromSlot(armorSlot);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
            boolean isLegs = armorSlot == EntityEquipmentSlot.CHEST;

            AbstractPonyModel modelbase = isLegs ? pony.getArmor().modelArmor : pony.getArmor().modelArmorChestplate;
            modelbase = getArmorModel(entity, itemstack, isLegs ? 2 : 1, modelbase);
            modelbase.setModelAttributes(this.pony.getModel());
            modelbase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            prepareToRender((ModelPonyArmor) modelbase, armorSlot);

            this.renderer.bindTexture(getArmorTexture(entity, itemstack, isLegs ? 2 : 1, null));
            if (itemarmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
                int j = itemarmor.getColor(itemstack);
                float f7 = (j >> 16 & 255) / 255.0F;
                float f8 = (j >> 8 & 255) / 255.0F;
                float f9 = (j & 255) / 255.0F;
                GlStateManager.color(f7, f8, f9, 1);
                modelbase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                this.renderer.bindTexture(getArmorTexture(entity, itemstack, isLegs ? 2 : 1, "overlay"));
            }
            GlStateManager.color(1, 1, 1, 1);
            modelbase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            if (itemstack.isItemEnchanted()) {
                this.renderEnchantment(entity, modelbase, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }
        }
    }

    private ResourceLocation getArmorTexture(EntityLivingBase entity, ItemStack itemstack, int slot, String type) {
        ItemArmor item = (ItemArmor) itemstack.getItem();
        String texture = item.getArmorMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == 2 ? 2 : 1),
                type == null ? "" : String.format("_%s", type));
        s1 = getArmorTexture(entity, itemstack, s1, slot, type);
        ResourceLocation human = getHumanResource(s1);
        ResourceLocation pony = getPonyResource(human);

        try {
            Minecraft.getMinecraft().getResourceManager().getResource(pony);
            return pony;
        } catch (IOException e) {
            return human;
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void prepareToRender(ModelPonyArmor model, EntityEquipmentSlot slot) {
        model.setInvisible(false);

        switch (slot) {
        // feet
        case FEET:
            model.bipedRightArm.showModel = true;
            model.bipedLeftArm.showModel = true;
            model.bipedRightLeg.showModel = true;
            model.bipedLeftLeg.showModel = true;
            break;
        // legs
        case LEGS:
            model.bipedRightLeg.showModel = true;
            model.bipedLeftLeg.showModel = true;
            model.bipedRightArm.showModel = true;
            model.bipedLeftArm.showModel = true;
            model.extBody.showModel = true;
            break;
        // chest
        case CHEST:
            model.extBody.showModel = true;
            break;
        // head
        case HEAD:
            model.bipedHead.showModel = true;
            for (ModelRenderer m : model.extHead) {
                m.showModel = true;
            }
        }
    }

    private void renderEnchantment(EntityLivingBase entitylivingbaseIn, ModelBase modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_,
            float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_) {
        float f7 = entitylivingbaseIn.ticksExisted + p_177183_5_;
        this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float f8 = 0.5F;
        GlStateManager.color(f8, f8, f8, 1.0F);

        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(768, 1);
            float f9 = 0.76F;
            GlStateManager.color(0.5F * f9, 0.25F * f9, 0.8F * f9, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f10 = 0.33333334F;
            GlStateManager.scale(f10, f10, f10);
            GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f7 * (0.001F + i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, p_177183_9_);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    private static ResourceLocation getHumanResource(String s1) {
        ResourceLocation human = HUMAN_ARMORS.get(s1);
        if (human == null) {
            human = new ResourceLocation(s1);
            HUMAN_ARMORS.put(s1, human);
        }
        return human;
    }

    private static ResourceLocation getPonyResource(ResourceLocation human) {
        ResourceLocation pony = PONY_ARMORS.get(human);
        if (pony == null) {
            String domain = human.getResourceDomain();
            String path = human.getResourcePath();
            if (domain.equals("minecraft")) {
                // it's a vanilla armor. I provide these.
                domain = "minelittlepony";
            }
            path = path.replace(".png", "_pony.png");
            pony = new ResourceLocation(domain, path);
            PONY_ARMORS.put(human, pony);
        }
        return pony;
    }

    private static String getArmorTexture(EntityLivingBase entity, ItemStack item, String def, int slot, String type) {
        IPonyArmor armor = MineLittlePony.getProxy().getPonyArmors();
        if (armor != null) {
            return armor.getArmorTexture(entity, item, def, slot, type);
        }
        return def;
    }

    private static AbstractPonyModel getArmorModel(EntityLivingBase entity, ItemStack itemstack, int slot, AbstractPonyModel def) {
        IPonyArmor armor = MineLittlePony.getProxy().getPonyArmors();
        if (armor != null) {
            ModelBase model = armor.getArmorModel(entity, itemstack, slot, def);
            if (model instanceof ModelPonyArmor) {
                return (AbstractPonyModel) model;
            }
        }
        return def;
    }
}
