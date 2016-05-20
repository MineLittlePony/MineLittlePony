package com.brohoof.minelittlepony.mixin;

import static net.minecraft.client.renderer.GlStateManager.scale;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.Pony;
import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.ducks.IRenderPony;
import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.ModelHumanPlayer;
import com.brohoof.minelittlepony.model.pony.ModelPlayerPony;
import com.brohoof.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyArmor;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyCape;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyElytra;
import com.brohoof.minelittlepony.renderer.layer.LayerPonySkull;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.util.ResourceLocation;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RenderLivingBase<AbstractClientPlayer> implements IRenderPony {

    @Shadow
    private boolean smallArms;
    private PlayerModel playerModel;
    private Pony thePony;

    private MixinRenderPlayer(RenderManager renderManager) {
        super(renderManager, null, 0.5F);
    }

    @Inject(
            method = "<init>(" + "Lnet/minecraft/client/renderer/entity/RenderManager;" + "Z)V",
            at = @At("RETURN"))
    private void init(RenderManager renderManager, boolean useSmallArms, CallbackInfo ci) {
        this.playerModel = smallArms ? PMAPI.ponySmall : PMAPI.pony;
        this.mainModel = this.playerModel.getModel();
        this.shadowSize = this.playerModel.getShadowsize();
        this.layerRenderers.clear();

        this.addLayer(new LayerPonyArmor(this));
        this.addLayer(new LayerHeldPonyItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerPonyCape(this));
        this.addLayer(new LayerPonySkull(this));
        this.addLayer(new LayerPonyElytra((RenderPlayer) (Object) this));
    }

    @Inject(
            method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V",
            at = @At("HEAD"))
    private void onDoRender(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        updateModel(player);

        this.playerModel.getModel().isSneak = player.isSneaking();
        this.playerModel.getModel().isFlying = thePony.isPegasusFlying(player);

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

    }

    @Inject(
            method = "renderLivingAt",
            at = @At("RETURN"))
    private void setupPlayerScale(AbstractClientPlayer player, double xPosition, double yPosition, double zPosition, CallbackInfo ci) {

        if (MineLittlePony.getConfig().showscale && !(playerModel.getModel() instanceof ModelHumanPlayer)) {
            PonySize size = thePony.metadata.getSize();
            if (size == PonySize.LARGE)
                scale(0.9F, 0.9F, 0.9F);
            else if (size == PonySize.NORMAL || size == PonySize.FOAL)
                scale(0.8F, 0.8F, 0.8F);
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

    @Redirect(
            method = "rotateCorpse(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V",
                    ordinal = 3))
    private void rotateRedirect(float f1, float f2, float f3, float f4) {
        if (this.playerModel.getModel() instanceof ModelPlayerPony)
            f1 += 90;
        GlStateManager.rotate(f1, f2, f3, f4);
    }

    private void updateModel(AbstractClientPlayer player) {
        this.thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(player);
        thePony.invalidateSkinCheck();
        thePony.checkSkin();
        this.playerModel = this.getModel(player);
        this.mainModel = this.playerModel.getModel();
        this.playerModel.apply(thePony.metadata);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(player);
        return thePony.getTextureResourceLocation();
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
