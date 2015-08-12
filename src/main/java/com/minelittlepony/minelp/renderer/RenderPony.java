package com.minelittlepony.minelp.renderer;

import static net.minecraft.client.renderer.GlStateManager.scale;

import com.minelittlepony.minelp.Pony;
import com.minelittlepony.minelp.PonyManager;
import com.minelittlepony.minelp.model.PMAPI;
import com.minelittlepony.minelp.model.PlayerModel;
import com.minelittlepony.minelp.model.pony.pm_Human;
import com.minelittlepony.minelp.model.pony.pm_newPonyAdv;
import com.minelittlepony.minelp.renderer.layer.LayerHeldPonyItem;
import com.minelittlepony.minelp.renderer.layer.LayerPonyArmor;
import com.minelittlepony.minelp.renderer.layer.LayerPonyCape;
import com.minelittlepony.minelp.renderer.layer.LayerPonySkull;
import com.minelittlepony.minelp.util.MineLPPrivateFields;
import com.mumfrey.liteloader.transformers.AppendInsns;
import com.mumfrey.liteloader.transformers.Obfuscated;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class RenderPony extends RendererLivingEntity implements IRenderPony {
    @SuppressWarnings("unused")
    private static RenderPlayer __TARGET;
    private PlayerModel playerModel;

    private RenderPony(RenderManager renderManager) {
        super(renderManager, null, 0.5F);
        throw new InstantiationError("Overlay classes must not be instantiated");
    }

    @AppendInsns("<init>")
    private void init(RenderManager renderManager, boolean useSmallArms) {
        this.playerModel = PMAPI.newPonyAdv;
        this.mainModel = this.playerModel.model;
        this.shadowSize = this.playerModel.shadowsize;
        this.layerRenderers.clear();

        this.addLayer(new LayerPonyArmor(this));
        this.addLayer(new LayerHeldPonyItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerPonySkull(this));
        this.addLayer(new LayerPonyCape(this));
    }

    @Obfuscated({ "a", "func_180596_a" })
    public void doRender(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks) {
        ItemStack currentItemStack = player.inventory.getCurrentItem();
        Pony thePony = PonyManager.getInstance().getPonyFromResourceRegistry(player);
        this.playerModel = this.getModel(player);
        this.mainModel = this.playerModel.model;
        this.playerModel.armor.modelArmorChestplate.heldItemRight = this.playerModel.armor.modelArmor.heldItemRight = this.playerModel.model.heldItemRight = currentItemStack == null
                ? 0 : 1;
        if (currentItemStack != null && player.getItemInUseCount() > 0) {
            EnumAction yOrigin = currentItemStack.getItemUseAction();
            if (yOrigin == EnumAction.BLOCK) {
                this.playerModel.armor.modelArmorChestplate.heldItemRight = this.playerModel.armor.modelArmor.heldItemRight = this.playerModel.model.heldItemRight = 3;
            } else if (yOrigin == EnumAction.BOW) {
                this.playerModel.armor.modelArmorChestplate.aimedBow = this.playerModel.armor.modelArmor.aimedBow = this.playerModel.model.aimedBow = true;
            }
        }

        this.playerModel.armor.modelArmorChestplate.issneak = this.playerModel.armor.modelArmor.issneak = this.playerModel.model.issneak = player.isSneaking();
        this.playerModel.armor.modelArmorChestplate.isFlying = this.playerModel.armor.modelArmor.isFlying = this.playerModel.model.isFlying = thePony.isFlying = thePony
                .isPegasusFlying(player.posX, player.posY, player.posZ, player.fallDistance,
                        MineLPPrivateFields.isJumping.get(player).booleanValue(), player.onGround, this.renderManager.worldObj);
        this.playerModel.armor.modelArmorChestplate.isPegasus = this.playerModel.armor.modelArmor.isPegasus = this.playerModel.model.isPegasus = thePony
                .isPegasus();
        if (this.playerModel.model instanceof pm_newPonyAdv) {
            ((pm_newPonyAdv) this.playerModel.model).setHasWings_Compression(thePony.isPegasus());
        }

        this.playerModel.armor.modelArmorChestplate.isUnicorn = this.playerModel.armor.modelArmor.isUnicorn = this.playerModel.model.isUnicorn = thePony.isUnicorn();
        this.playerModel.armor.modelArmorChestplate.isMale = this.playerModel.armor.modelArmor.isMale = this.playerModel.model.isMale = thePony.isMale();
        this.playerModel.armor.modelArmorChestplate.size = this.playerModel.armor.modelArmor.size = this.playerModel.model.size = thePony.size();
        if (PonyManager.getInstance().getShowScale() == 1) {
            if (this.playerModel != PMAPI.human) {
                if (thePony.size() == 0) {
                    this.shadowSize = 0.25F;
                } else if (thePony.size() == 1) {
                    this.shadowSize = 0.4F;
                } else if (thePony.size() == 2) {
                    this.shadowSize = 0.45F;
                } else if (thePony.size() == 3) {
                    this.shadowSize = 0.5F;
                } else {
                    this.shadowSize = 0.5F;
                }
            } else {
                this.shadowSize = 0.5F;
            }
        } else {
            this.shadowSize = 0.5F;
        }

        double yOrigin1 = y;
        if (player.isSneaking() && !(player instanceof EntityPlayerSP)) {
            yOrigin1 -= 0.125D;
        }

        this.playerModel.model.glowColor = thePony.glowColor();
        this.playerModel.armor.modelArmorChestplate.isSleeping = this.playerModel.armor.modelArmor.isSleeping = this.playerModel.model.isSleeping = player.isPlayerSleeping();
        this.playerModel.armor.modelArmorChestplate.swingProgress = this.playerModel.armor.modelArmor.swingProgress = this.playerModel.model.swingProgress;
        this.playerModel.model.wantTail = thePony.wantTail();
        this.playerModel.armor.modelArmorChestplate.isVillager = this.playerModel.armor.modelArmor.isVillager = this.playerModel.model.isVillager = false;
        super.doRender(player, x, yOrigin1, z, yaw, partialTicks);
        this.playerModel.armor.modelArmorChestplate.aimedBow = this.playerModel.armor.modelArmor.aimedBow = this.playerModel.model.aimedBow = false;
        this.playerModel.armor.modelArmorChestplate.issneak = this.playerModel.armor.modelArmor.issneak = this.playerModel.model.issneak = false;
        this.playerModel.armor.modelArmorChestplate.heldItemRight = this.playerModel.armor.modelArmor.heldItemRight = this.playerModel.model.heldItemRight = 0;
    }

    @AppendInsns("renderLivingAt")
    @Obfuscated({ "a", "func_77039_a" })
    public void setupPlayerScale(AbstractClientPlayer player, double xPosition, double yPosition, double zPosition) {

        if (PonyManager.getInstance().getShowScale() == 1 && !(playerModel.model instanceof pm_Human)) {
            if (this.playerModel.model.size == 2) {
                scale(0.9F, 0.9F, 0.9F);
            } else if (this.playerModel.model.size == 1 || this.playerModel.model.size == 0) {
                scale(0.8F, 0.8F, 0.8F);
            }
        }
    }

    public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        Pony thePony = PonyManager.getInstance().getPonyFromResourceRegistry(player);
        return thePony.getTextureResourceLocation();
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((AbstractClientPlayer) entity);
    }

    protected PlayerModel getModel(AbstractClientPlayer player) {
        Pony thePony = PonyManager.getInstance().getPonyFromResourceRegistry(player);
        return thePony.getModel();
    }

    @Override
    public PlayerModel getPony() {
        return this.playerModel;
    }
}
