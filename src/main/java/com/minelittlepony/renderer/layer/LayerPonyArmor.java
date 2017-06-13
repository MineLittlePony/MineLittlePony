package com.minelittlepony.renderer.layer;

import com.google.common.collect.Maps;
import com.minelittlepony.ForgeProxy;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.model.pony.armor.ModelPonyArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

public class LayerPonyArmor extends AbstractPonyLayer<EntityLivingBase> {

    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private static final Map<String, ResourceLocation> HUMAN_ARMORS = Maps.newHashMap();
    private static final Map<ResourceLocation, ResourceLocation> PONY_ARMORS = Maps.newHashMap();

    private PlayerModel pony;

    public LayerPonyArmor(RenderLivingBase<? extends EntityLivingBase> renderer) {
        super(renderer, new LayerBipedArmor(renderer));
    }

    @Override
    public void doPonyRender(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ticks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        pony = ((IRenderPony) getRenderer()).getPony();
        renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
        renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
        renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
        renderArmor(entity, limbSwing, limbSwingAmount, ticks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);

    }

    private void renderArmor(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot armorSlot) {
        ItemStack itemstack = entity.getItemStackFromSlot(armorSlot);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemArmor) {

            ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

            AbstractPonyModel modelbase;
            if (armorSlot == EntityEquipmentSlot.LEGS) {
                modelbase = pony.getArmor().modelArmor;
            } else {
                modelbase = pony.getArmor().modelArmorChestplate;
            }
            modelbase = getArmorModel(entity, itemstack, armorSlot, modelbase);
            modelbase.setModelAttributes(this.pony.getModel());
            modelbase.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

            Tuple<ResourceLocation, Boolean> armors = getArmorTexture(entity, itemstack, armorSlot, null);
            prepareToRender((ModelPonyArmor) modelbase, armorSlot, armors.getSecond());

            this.getRenderer().bindTexture(armors.getFirst());
            if (itemarmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
                int color = itemarmor.getColor(itemstack);
                float r = (color >> 16 & 255) / 255.0F;
                float g = (color >> 8 & 255) / 255.0F;
                float b = (color & 255) / 255.0F;
                GlStateManager.color(r, g, b, 1);
                modelbase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                armors = getArmorTexture(entity, itemstack, armorSlot, "overlay");
                this.getRenderer().bindTexture(armors.getFirst());
            }
            GlStateManager.color(1, 1, 1, 1);
            modelbase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            if (itemstack.isItemEnchanted()) {
                this.renderEnchantment(entity, modelbase, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }
        }
    }

    private Tuple<ResourceLocation, Boolean> getArmorTexture(EntityLivingBase entity, ItemStack itemstack, EntityEquipmentSlot slot, @Nullable String type) {
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
                for (ModelRenderer extLeg : model.extLegs) {
                    extLeg.showModel = isPony;
                }
                break;
            // legs
            case LEGS:
                model.bipedRightLeg.showModel = !isPony;
                model.bipedLeftLeg.showModel = !isPony;
                model.bipedRightArm.showModel = true;
                model.bipedLeftArm.showModel = true;
                model.bipedBody.showModel = !isPony;
                model.Bodypiece.showModel = !isPony;
                model.extBody.showModel = isPony;
                for (ModelRenderer extLeg : model.extLegs) {
                    extLeg.showModel = isPony;
                }
                break;
            // chest
            case CHEST:
                model.extBody.showModel = isPony;
                model.bipedBody.showModel = !isPony;
                model.Bodypiece.showModel = !isPony;
                break;
            // head
            case HEAD:
                model.bipedHead.showModel = true;
                for (ModelRenderer head : model.extHead) {
                    head.showModel = isPony;
                }
        }
    }

    private void renderEnchantment(EntityLivingBase entitylivingbaseIn, ModelBase modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_,
                                   float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_) {
        float f7 = entitylivingbaseIn.ticksExisted + p_177183_5_;
        this.getRenderer().bindTexture(ENCHANTED_ITEM_GLINT_RES);
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

    private static ResourceLocation getHumanResource(String s1) {
        return HUMAN_ARMORS.computeIfAbsent(s1, ResourceLocation::new);
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
