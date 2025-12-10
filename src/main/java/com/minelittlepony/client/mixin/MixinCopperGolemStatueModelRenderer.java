package com.minelittlepony.client.mixin;

import net.minecraft.block.CopperGolemStatueBlock.Pose;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.state.CopperGolemStatueBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.CopperGolemStatueModelRenderer;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SpikeModel;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.util.ResourceUtil;

import java.util.function.Consumer;

@Mixin(CopperGolemStatueModelRenderer.class)
abstract class MixinCopperGolemStatueModelRenderer implements SimpleSpecialModelRenderer {

    private final SpikeModel.BlockModel spike_model = new SpikeModel.BlockModel(ModelType.SPIKE.createTree().get());

    @Shadow
    private @Final Identifier texture;

    @Shadow
    private static void setAngles(MatrixStack matrices) {}


    @Inject(
            method = "collectVertices(Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCollectVertices(Consumer<Vector3fc> vertices, CallbackInfo info) {
        if (MobRenderers.COPPER_GOLEMS.option().get()) {
            if (ResourceUtil.textureExists(MineLittlePony.id(texture.getPath().replace(".png", "_dragon.png")))) {
                MatrixStack matrices = new MatrixStack();
                setAngles(matrices);
                spike_model.getRootPart().collectVertices(matrices, vertices);
                info.cancel();
            }
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRender(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i, CallbackInfo info) {
        if (MobRenderers.COPPER_GOLEMS.option().get()) {
            Identifier ponifiedTexture = MineLittlePony.id(texture.getPath().replace(".png", "_dragon.png"));
            if (ResourceUtil.textureExists(ponifiedTexture)) {
                setAngles(matrices);
                CopperGolemStatueBlockEntityRenderState state = new CopperGolemStatueBlockEntityRenderState();
                state.facing = Direction.SOUTH;
                state.pose = Pose.STANDING;
                queue.submitModel(spike_model, state, matrices, RenderLayers.entityCutoutNoCull(MineLittlePony.id(texture.getPath().replace(".png", "_dragon.png"))), light, overlay, -1, null, i, null);
                info.cancel();
            }
        }
    }


}
