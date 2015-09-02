package com.minelittlepony.minelp.renderer.layer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Maps;
import com.minelittlepony.minelp.PonyManager;
import com.minelittlepony.minelp.model.ModelPony;
import com.minelittlepony.minelp.model.PlayerModel;
import com.minelittlepony.minelp.model.pony.pm_Human;
import com.minelittlepony.minelp.model.pony.pm_newPonyAdv;
import com.minelittlepony.minelp.model.pony.armor.pm_newPonyArmor;
import com.minelittlepony.minelp.renderer.IRenderPony;
import com.minelittlepony.minelp.util.MineLPReflection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerPonyArmor implements LayerRenderer {

    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private static final Map<String, ResourceLocation> field_110859_k = Maps.newHashMap();
    private static final Map<String, ResourceLocation> ponyArmorMap = Maps.newHashMap();
    private static final Map<String, ResourceLocation> onlyPonyArmorMap = Maps.newHashMap();
    private static final String[] bipedArmorFilenamePrefix = { "leather", "chainmail", "iron", "gold", "diamond" };

    private static HashSet<String> ponyArmors = new HashSet<String>();

    private RendererLivingEntity renderer;
    private LayerBipedArmor humanArmor;
    private PlayerModel pony;

    public LayerPonyArmor(RendererLivingEntity entity) {
        this.renderer = entity;
        this.humanArmor = new LayerBipedArmor(entity);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
            float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        pony = ((IRenderPony) renderer).getPony();
        if (pony.model instanceof pm_Human) {
            humanArmor.doRenderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_,
                    p_177141_6_, p_177141_7_, scale);
        } else {
            renderArmor(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_,
                    p_177141_7_, scale, 4);
            renderArmor(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_,
                    p_177141_7_, scale, 3);
            renderArmor(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_,
                    p_177141_7_, scale, 2);
            renderArmor(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_,
                    p_177141_7_, scale, 1);
        }
    }

    private void renderArmor(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
            float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale, int armorSlot) {
        ItemStack itemstack = entitylivingbaseIn.getCurrentArmor(armorSlot - 1);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

            ModelPony modelbase = armorSlot > 2 ? pony.armor.modelArmorChestplate : pony.armor.modelArmor;
            modelbase.setModelAttributes(this.renderer.getMainModel());
            modelbase.setLivingAnimations(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks);
            prepareToRender((pm_newPonyArmor) modelbase, armorSlot);
            this.bindPonyArmorTexture(itemarmor, armorSlot, null);
            if (itemarmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
                int j = itemarmor.getColor(itemstack);
                float f7 = (j >> 16 & 255) / 255.0F;
                float f8 = (j >> 8 & 255) / 255.0F;
                float f9 = (j & 255) / 255.0F;
                GlStateManager.color(f7, f8, f9, 1);
                modelbase.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_,
                        scale);

                bindPonyArmorTexture(itemarmor, armorSlot, "overlay");
            }
            GlStateManager.color(1, 1, 1, 1);
            modelbase.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_,
                    scale);

            if (itemstack.isItemEnchanted()) {
                this.renderEnchantment(entitylivingbaseIn, modelbase, p_177141_2_, p_177141_3_, partialTicks,
                        p_177141_5_, p_177141_6_, p_177141_7_, scale);
            }
        }
    }

    private void prepareToRender(pm_newPonyArmor model, int slot) {
        model.setInvisible(false);

        switch (slot) {
        // feet
        case 1:
            model.bipedRightArm.showModel = true;
            model.bipedLeftArm.showModel = true;
            for (ModelRenderer m : model.extLegs) {
                m.showModel = true;
            }
            model.bipedRightLeg.showModel = true;
            model.bipedLeftLeg.showModel = true;
            break;
        // legs
        case 2:
            model.bipedRightLeg.showModel = true;
            model.bipedLeftLeg.showModel = true;
            model.bipedRightArm.showModel = true;
            model.bipedLeftArm.showModel = true;
            model.Bodypiece.showModel = true;
            model.extBody.showModel = true;
            model.bipedBody.showModel = true;
            break;
        // chest
        case 3:
            model.bipedRightLeg.showModel = true;
            model.bipedLeftLeg.showModel = true;
            model.bipedRightArm.showModel = true;
            model.bipedLeftArm.showModel = true;
            model.extBody.showModel = true;
            break;
        // head
        case 4:
            model.bipedHead.showModel = true;
            for (ModelRenderer m : model.extHead) {
                m.showModel = true;
            }
        }
    }

    private void renderEnchantment(EntityLivingBase entitylivingbaseIn, ModelBase modelbaseIn, float p_177183_3_,
            float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_,
            float p_177183_9_) {
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
            modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_,
                    p_177183_9_);
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

    protected boolean bindPonyArmorTexture(ItemArmor armorPiece, int slot, String overlay) {
        String overlayText = "";
        if (overlay != null) {
            overlayText = String.format("_%s", overlay);
        }

        String path = this.pony.armor.path
                + bipedArmorFilenamePrefix[armorPiece.getArmorMaterial().ordinal()]
                + "_layer_" + this.pony.armor.subimage(slot) + overlayText + ".png";
        if (PonyManager.getInstance().getPonyArmor() == 1 && this.pony.model instanceof pm_newPonyAdv) {
            Object[] armorResourceAndState = this.getPonyResourceLocation(path);
            renderer.bindTexture((ResourceLocation) armorResourceAndState[0]);
            return ((Boolean) armorResourceAndState[1]).booleanValue();
        }
        return false;
    }

    protected boolean bindForgeArmorTexture(Entity playerEntity, ItemStack armorStack, ItemArmor armorPiece, int slot,
            String overlay) {
        String path = "";

        try {
            path = String.format("textures/models/armor/%s_layer_%d%s.png",
                    bipedArmorFilenamePrefix[armorPiece.renderIndex],
                    Integer.valueOf(slot == 2 ? 2 : 1),
                    overlay == null ? "" : String.format("_%s", overlay));
        } catch (ArrayIndexOutOfBoundsException var10) {}

        path = (String) MineLPReflection.forgeAPI.invokeMethod("ForgeHooksClient.getArmorTexture", (Object) null,
                playerEntity, armorStack, path, Integer.valueOf(slot), overlay);
        boolean ponyArmor;
        if (ponyArmors.contains(path)) {
            ponyArmor = this.bindPonyArmorTexture(armorPiece, slot, overlay);
        } else {
            ponyArmor = false;
            ResourceLocation forgeResourceLocation;
            if (PonyManager.getInstance().getPonyArmor() == 1 && this.pony.model instanceof pm_newPonyAdv) {
                Object[] armorResourceAndState = this.getPonyResourceLocation(path);
                forgeResourceLocation = (ResourceLocation) armorResourceAndState[0];
                ponyArmor = ((Boolean) armorResourceAndState[1]).booleanValue();
            } else {
                forgeResourceLocation = field_110859_k.get(path);
                if (forgeResourceLocation == null) {
                    forgeResourceLocation = new ResourceLocation(path);
                    field_110859_k.put(path, forgeResourceLocation);
                }

                ponyArmor = false;
            }

            renderer.bindTexture(forgeResourceLocation);
        }

        return ponyArmor;
    }

    protected Object[] getPonyResourceLocation(String path) {
        boolean ponyArmor = false;
        String ponyPath = path.replace(".png", "_pony.png");
        ResourceLocation ponyResourceLocation = ponyArmorMap.get(path);
        if (ponyResourceLocation == null) {
            ResourceLocation ponyArmorResource = new ResourceLocation(ponyPath);

            try {
                TextureUtil.readImageData(Minecraft.getMinecraft().getResourceManager(), ponyArmorResource);
                ponyResourceLocation = ponyArmorResource;
                ponyArmorMap.put(path, ponyArmorResource);
                onlyPonyArmorMap.put(path, ponyArmorResource);
                ponyArmor = true;
            } catch (IOException var7) {
                ponyResourceLocation = field_110859_k.get(path);
                if (ponyResourceLocation == null) {
                    ponyResourceLocation = new ResourceLocation(path);
                    field_110859_k.put(path, ponyResourceLocation);
                }

                ponyArmorMap.put(path, ponyResourceLocation);
                ponyArmor = false;
            }
        } else {
            ponyArmor = true;
            ponyResourceLocation = onlyPonyArmorMap.get(path);
            if (ponyResourceLocation == null) {
                ponyResourceLocation = ponyArmorMap.get(path);
                ponyArmor = false;
            }
        }

        return new Object[] { ponyResourceLocation, Boolean.valueOf(ponyArmor) };
    }

    static {
        MineLPReflection.preCall();

        for (int i = 1; i <= 2; ++i) {
            for (String prefix : bipedArmorFilenamePrefix) {
                ponyArmors.add("textures/models/armor/" + prefix + "_layer_" + i + ".png");
            }
        }

        ponyArmors.add("textures/models/armor/leather_layer_1_overlay.png");
        ponyArmors.add("textures/models/armor/leather_layer_2_overlay.png");
    }
}
