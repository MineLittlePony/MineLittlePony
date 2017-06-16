package com.minelittlepony.mixin;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.Pony;
import com.minelittlepony.PonyRace;
import com.minelittlepony.PonySize;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.model.pony.ModelHumanPlayer;
import com.minelittlepony.model.pony.ModelPlayerPony;
import com.minelittlepony.renderer.layer.LayerEntityOnPonyShoulder;
import com.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.minelittlepony.renderer.layer.LayerPonyArmor;
import com.minelittlepony.renderer.layer.LayerPonyCape;
import com.minelittlepony.renderer.layer.LayerPonyCustomHead;
import com.minelittlepony.renderer.layer.LayerPonyElytra;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.util.ResourceLocation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RenderLivingBase<AbstractClientPlayer> implements IRenderPony {

    @Shadow
    @Final
    private boolean smallArms;
    private PlayerModel playerModel;
    private Pony thePony;

    @SuppressWarnings("ConstantConditions")
    private MixinRenderPlayer(RenderManager renderManager) {
        super(renderManager, null, 0.5F);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V",
            at = @At("RETURN"))
    private void init(RenderManager renderManager, boolean useSmallArms, CallbackInfo ci) {
        this.playerModel = smallArms ? PMAPI.ponySmall : PMAPI.pony;
        this.mainModel = this.playerModel.getModel();
        this.layerRenderers.clear();

        this.addLayer(new LayerPonyArmor(this));
        this.addLayer(new LayerHeldPonyItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerPonyCape(this));
        this.addLayer(new LayerPonyCustomHead(this));
        this.addLayer(new LayerPonyElytra(this));
        this.addLayer(new LayerEntityOnPonyShoulder(renderManager, this));

    }

    @Inject(
            method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V",
            at = @At("HEAD"))
    private void onDoRender(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        updateModel(player);

        this.playerModel.getModel().isSneak = player.isSneaking();
        this.playerModel.getModel().isFlying = thePony.isPegasusFlying(player);
        this.playerModel.getModel().isSleeping = player.isPlayerSleeping();

        if (MineLittlePony.getConfig().showscale && this.playerModel.getModel().metadata.getRace() != PonyRace.HUMAN) {
            PonySize size = thePony.getMetadata().getSize();
            if (size == PonySize.FOAL) {
                this.shadowSize = 0.25F;
            } else if (size == PonySize.NORMAL) {
                this.shadowSize = 0.4F;
            } else if (size == PonySize.TALL) {
                this.shadowSize = 0.45F;
            } else {
                this.shadowSize = 0.5F;
            }

        } else {
            this.shadowSize = 0.5F;
        }

    }

    @Inject(
            method = "renderLivingAt(Lnet/minecraft/client/entity/AbstractClientPlayer;DDD)V",
            at = @At("RETURN"))
    private void setupPlayerScale(AbstractClientPlayer player, double xPosition, double yPosition, double zPosition, CallbackInfo ci) {

        if (MineLittlePony.getConfig().showscale && !(playerModel.getModel() instanceof ModelHumanPlayer)) {
            PonySize size = thePony.getMetadata().getSize();
            if (size == PonySize.LARGE) {
                GlStateManager.scale(0.9F, 0.9F, 0.9F);
            } else if (size == PonySize.NORMAL || size == PonySize.FOAL) {
                GlStateManager.scale(0.8F, 0.8F, 0.8F);
            }
        }
    }

    @Inject(
            method = "renderRightArm(Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
            at = @At("HEAD"))
    private void onRenderRightArm(AbstractClientPlayer player, CallbackInfo ci) {
        updateModel(player);
        bindEntityTexture(player);
    }

    @Inject(
            method = "renderLeftArm(Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
            at = @At("HEAD"))
    private void onRenderLeftArm(AbstractClientPlayer player, CallbackInfo ci) {
        updateModel(player);
        bindEntityTexture(player);
    }

    @Redirect(
            method = "renderLeftArm(Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/model/ModelPlayer;bipedLeftArm:Lnet/minecraft/client/model/ModelRenderer;",
                    opcode = Opcodes.GETFIELD),
            require = 2)
    private ModelRenderer redirectLeftArm(ModelPlayer mr) {
        return this.playerModel.getModel().steveLeftArm;
    }

    @Redirect(
            method = "renderLeftArm(Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/model/ModelPlayer;bipedLeftArmwear:Lnet/minecraft/client/model/ModelRenderer;",
                    opcode = Opcodes.GETFIELD),
            require = 2)
    private ModelRenderer redirectLeftArmwear(ModelPlayer mr) {
        return this.playerModel.getModel().steveLeftArmwear;
    }

    @Redirect(
            method = "renderRightArm(Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/model/ModelPlayer;bipedRightArm:Lnet/minecraft/client/model/ModelRenderer;",
                    opcode = Opcodes.GETFIELD),
            require = 2)
    private ModelRenderer redirectRightArm(ModelPlayer mr) {
        return this.playerModel.getModel().steveRightArm;
    }

    @Redirect(
            method = "renderRightArm(Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/model/ModelPlayer;bipedRightArmwear:Lnet/minecraft/client/model/ModelRenderer;",
                    opcode = Opcodes.GETFIELD),
            require = 2)
    private ModelRenderer redirectRightArmwear(ModelPlayer mr) {
        return this.playerModel.getModel().steveRightArmwear;
    }

    @Inject(
            method = "applyRotations(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/RenderLivingBase;"
                            + "applyRotations(Lnet/minecraft/entity/EntityLivingBase;FFF)V",
                    ordinal = 1,
                    shift = Shift.AFTER))
    private void onRotateCorpse(AbstractClientPlayer player, float yaw, float pitch, float ticks, CallbackInfo ci) {
        if (this.mainModel instanceof ModelPlayerPony) {
            // require arms to be stretched out (sorry mud ponies, no flight
            // skills for you)
            if (!((ModelPlayerPony) this.mainModel).rainboom) {
                this.playerModel.getModel().motionPitch = 0;
                return;
            }
            double motionX = player.posX - player.prevPosX;
            double motionY = player.posY - player.prevPosY;
            double motionZ = player.posZ - player.prevPosZ;
            if (player.onGround) {
                motionY = 0;
            }
            double dist = Math.sqrt(motionX * motionX + motionZ * motionZ);
            double angle = Math.atan2(motionY, dist);
            if (!player.capabilities.isFlying) {
                if (angle > 0) {
                    angle = 0;
                } else {
                    angle /= 2;
                }
            }

            if (angle > Math.PI / 3) {
                angle = Math.PI / 3;
            }
            if (angle < -Math.PI / 3) {
                angle = -Math.PI / 3;
            }

            this.playerModel.getModel().motionPitch = (float) Math.toDegrees(angle);

            GlStateManager.rotate((float) Math.toDegrees(angle), 1F, 0F, 0F);

        }
    }

    @Redirect(
            method = "applyRotations(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V",
                    ordinal = 3))
    private void rotateRedirect(float f1, float f2, float f3, float f4) {
        boolean isPony = this.playerModel.getModel() instanceof ModelPlayerPony;
        if (isPony) {
            f1 += 90;
        }

        GlStateManager.rotate(f1, f2, f3, f4);
        if (isPony) {
            GlStateManager.translate(0, -1, 0);
        }
    }

    private void updateModel(AbstractClientPlayer player) {
        this.thePony = MineLittlePony.getInstance().getManager().getPony(player);
        this.playerModel = this.getModel(player);
        this.mainModel = this.playerModel.getModel();
        this.playerModel.apply(thePony.getMetadata());
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        Pony thePony = MineLittlePony.getInstance().getManager().getPony(player);
        return thePony.getTexture();
    }

    private PlayerModel getModel(AbstractClientPlayer player) {
        ResourceLocation skin = getEntityTexture(player);
        Pony thePony = MineLittlePony.getInstance().getManager().getPony(skin);
        return thePony.getModel(false, this.smallArms);
    }

    @Override
    public PlayerModel getPony() {
        return this.playerModel;
    }
}
