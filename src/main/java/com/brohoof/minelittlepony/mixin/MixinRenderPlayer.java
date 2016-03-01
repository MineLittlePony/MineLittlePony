package com.brohoof.minelittlepony.mixin;

import static net.minecraft.client.renderer.GlStateManager.scale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.Pony;
import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.ModelHumanPlayer;
import com.brohoof.minelittlepony.renderer.IRenderPony;
import com.brohoof.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyArmor;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyCape;
import com.brohoof.minelittlepony.renderer.layer.LayerPonySkull;

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

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RendererLivingEntity implements IRenderPony {

    private static final String RenderManager = "Lnet/minecraft/client/renderer/entity/RenderManager;";
    private static final String AbstractClientPlayer = "Lnet/minecraft/client/entity/AbstractClientPlayer;";

    @Shadow
    private boolean smallArms;
    private PlayerModel playerModel;
    private Pony thePony;

    private MixinRenderPlayer(RenderManager renderManager) {
        super(renderManager, null, 0.5F);
    }

    @Inject(
            method = "<init>(" + RenderManager + "Z)V",
            at = @At("RETURN") )
    private void init(RenderManager renderManager, boolean useSmallArms, CallbackInfo ci) {
        this.playerModel = smallArms ? PMAPI.ponySmall : PMAPI.pony;
        this.mainModel = this.playerModel.getModel();
        this.shadowSize = this.playerModel.getShadowsize();
        this.layerRenderers.clear();

        this.addLayer(new LayerPonyArmor(this));
        this.addLayer(new LayerHeldPonyItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerPonySkull(this));
        this.addLayer(new LayerPonyCape(this));
    }

    /**
     * @reason render a pony instead of a human
     * @author JoyJoy
     */
    @Overwrite
    public void doRender(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks) {
        updateModel(player);
        ItemStack currentItemStack = player.inventory.getCurrentItem();

        this.playerModel.getModel().heldItemRight = currentItemStack == null ? 0 : 1;

        if (currentItemStack != null && player.getItemInUseCount() > 0) {
            EnumAction action = currentItemStack.getItemUseAction();
            if (action == EnumAction.BLOCK) {
                this.playerModel.getModel().heldItemRight = 3;
            } else if (action == EnumAction.BOW) {
                this.playerModel.getModel().aimedBow = true;
            }
        }

        this.playerModel.getModel().isSneak = player.isSneaking();
        this.playerModel.getModel().isFlying = thePony.isPegasusFlying(player);
        this.playerModel.getModel().isRiding = player.isRiding();

        if (MineLittlePony.getConfig().showscale) {
            if (this.playerModel.getModel().metadata.getRace() != null) {
                PonySize size = thePony.metadata.getSize();
                if (size == PonySize.FOAL)
                    this.shadowSize = 0.25F;
                else if (size == PonySize.NORMAL)
                    this.shadowSize = 0.4F;
                else if (size == PonySize.TALL)
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

        this.playerModel.getModel().isSleeping = player.isPlayerSleeping();
        this.playerModel.getModel().swingProgress = getSwingProgress(player, partialTicks);

        super.doRender(player, x, yOrigin1, z, yaw, partialTicks);

        this.playerModel.getModel().aimedBow = false;
        this.playerModel.getModel().isSneak = false;
        this.playerModel.getModel().heldItemRight = 0;
    }

    @Inject(method = "renderLivingAt", at = @At("RETURN") )
    private void setupPlayerScale(AbstractClientPlayer player, double xPosition, double yPosition, double zPosition, CallbackInfo ci) {

        if (MineLittlePony.getConfig().showscale && !(playerModel.getModel() instanceof ModelHumanPlayer)) {
            PonySize size = thePony.metadata.getSize();
            if (size == PonySize.LARGE)
                scale(0.9F, 0.9F, 0.9F);
            else if (size == PonySize.NORMAL || size == PonySize.FOAL)
                scale(0.8F, 0.8F, 0.8F);
        }
    }

    @Inject(method = "renderRightArm(" + AbstractClientPlayer + ")V", at = @At("HEAD") )
    private void onRenderRightArm(AbstractClientPlayer player, CallbackInfo ci) {
        updateModel(player);
        bindEntityTexture(player);
    }

    @Inject(method = "renderLeftArm(" + AbstractClientPlayer + ")V", at = @At("HEAD") )
    private void onRenderLeftArm(AbstractClientPlayer player, CallbackInfo ci) {
        updateModel(player);
        bindEntityTexture(player);
    }

    private void updateModel(AbstractClientPlayer player) {
        this.thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(player);
        thePony.checkSkin();
        this.playerModel = this.getModel(player);
        this.mainModel = this.playerModel.getModel();
        this.playerModel.apply(thePony.metadata);
    }

    private ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(player);
        return thePony.getTextureResourceLocation();
    }

    @Override
    public final ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((AbstractClientPlayer) entity);
    }

    private PlayerModel getModel(AbstractClientPlayer player) {
        ResourceLocation skin = getEntityTexture(player);
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(skin);
        return thePony.getModel(false, this.smallArms);
    }

    @Override
    public PlayerModel getPony() {
        return this.playerModel;
    }
}
