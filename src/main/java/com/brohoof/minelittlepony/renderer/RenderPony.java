package com.brohoof.minelittlepony.renderer;

import static net.minecraft.client.renderer.GlStateManager.scale;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.Pony;
import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.pm_Human;
import com.brohoof.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyArmor;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyCape;
import com.brohoof.minelittlepony.renderer.layer.LayerPonySkull;
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
    private Pony thePony;

    private RenderPony(RenderManager renderManager) {
        super(renderManager, null, 0.5F);
        throw new InstantiationError("Overlay classes must not be instantiated");
    }

    @AppendInsns("<init>")
    private void init(RenderManager renderManager, boolean useSmallArms) {
        this.playerModel = PMAPI.newPonyAdv;
        this.mainModel = this.playerModel.getModel();
        this.shadowSize = this.playerModel.getShadowsize();
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
        this.thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(player);
        thePony.checkSkin();
        this.playerModel = this.getModel(player);
        this.mainModel = this.playerModel.getModel();
        this.playerModel.getArmor().modelArmorChestplate.heldItemRight = this.playerModel.getArmor().modelArmor.heldItemRight = this.playerModel
                .getModel().heldItemRight = currentItemStack == null ? 0 : 1;
        this.playerModel.apply(thePony.metadata);
        if (currentItemStack != null && player.getItemInUseCount() > 0) {
            EnumAction yOrigin = currentItemStack.getItemUseAction();
            if (yOrigin == EnumAction.BLOCK) {
                this.playerModel.getArmor().modelArmorChestplate.heldItemRight = this.playerModel.getArmor().modelArmor.heldItemRight = this.playerModel
                        .getModel().heldItemRight = 3;
            } else if (yOrigin == EnumAction.BOW) {
                this.playerModel.getArmor().modelArmorChestplate.aimedBow = this.playerModel.getArmor().modelArmor.aimedBow = this.playerModel
                        .getModel().aimedBow = true;
            }
        }

        this.playerModel.getArmor().modelArmorChestplate.issneak = this.playerModel.getArmor().modelArmor.issneak = this.playerModel.getModel().issneak = player
                .isSneaking();
        this.playerModel
                .getArmor().modelArmorChestplate.isFlying = this.playerModel.getArmor().modelArmor.isFlying = this.playerModel.getModel().isFlying = thePony
                        .isPegasusFlying(player);
        // , this.renderManager.worldObj);

        if (MineLittlePony.getConfig().getShowScale().get()) {
            if (this.playerModel != PMAPI.human) {
                PonySize size = thePony.metadata.getSize();
                if (size == PonySize.FOAL)
                    this.shadowSize = 0.25F;
                else if (size == PonySize.NORMAL)
                    this.shadowSize = 0.4F;
                else if (size == PonySize.PRINCESS)
                    this.shadowSize = 0.45F;
                else
                    this.shadowSize = 0.5F;
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

        this.playerModel.getArmor().modelArmorChestplate.isSleeping = this.playerModel.getArmor().modelArmor.isSleeping = this.playerModel
                .getModel().isSleeping = player.isPlayerSleeping();
        this.playerModel.getArmor().modelArmorChestplate.swingProgress = this.playerModel.getArmor().modelArmor.swingProgress = this.playerModel
                .getModel().swingProgress;
        this.playerModel.getArmor().modelArmorChestplate.isVillager = this.playerModel.getArmor().modelArmor.isVillager = this.playerModel
                .getModel().isVillager = false;

        super.doRender(player, x, yOrigin1, z, yaw, partialTicks);

        this.playerModel
                .getArmor().modelArmorChestplate.aimedBow = this.playerModel.getArmor().modelArmor.aimedBow = this.playerModel.getModel().aimedBow = false;
        this.playerModel.getArmor().modelArmorChestplate.issneak = this.playerModel.getArmor().modelArmor.issneak = this.playerModel.getModel().issneak = false;
        this.playerModel.getArmor().modelArmorChestplate.heldItemRight = this.playerModel.getArmor().modelArmor.heldItemRight = this.playerModel
                .getModel().heldItemRight = 0;
    }

    @AppendInsns("renderLivingAt")
    @Obfuscated({ "a", "func_77039_a" })
    public void setupPlayerScale(AbstractClientPlayer player, double xPosition, double yPosition, double zPosition) {

        if (MineLittlePony.getConfig().getShowScale().get() && !(playerModel.getModel() instanceof pm_Human)) {
            PonySize size = thePony.metadata.getSize();
            if (size == PonySize.LARGE)
                scale(0.9F, 0.9F, 0.9F);
            else if (size == PonySize.NORMAL || size == PonySize.FOAL)
                scale(0.8F, 0.8F, 0.8F);
        }
    }

    public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(player);
        return thePony.getTextureResourceLocation();
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((AbstractClientPlayer) entity);
    }

    protected PlayerModel getModel(AbstractClientPlayer player) {
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(player);
        return thePony.getModel();
    }

    @Override
    public PlayerModel getPony() {
        return this.playerModel;
    }
}
