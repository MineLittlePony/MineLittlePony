package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.base.Suppliers;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.mson.api.ModelKey;
import com.mojang.authlib.GameProfile;

import java.util.function.Supplier;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class MobSkull implements ISkull {
    private final Identifier texture;
    private final MobRenderers type;

    private final Supplier<AbstractPonyModel<?>> ponyHead;

    MobSkull(Identifier texture, MobRenderers type, ModelKey<? extends AbstractPonyModel<?>> modelKey) {
        this.texture = texture;
        this.type = type;
        this.ponyHead = Suppliers.memoize(modelKey::createModel);
    }

    @Override
    public boolean canRender(PonyConfig config) {
        return config.ponyskulls.get() && type.get();
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        return texture;
    }

    @Override
    public boolean bindPony(IPony pony) {
        ponyHead.get().setMetadata(pony.metadata());
        return true;
    }

    @Override
    public void setAngles(float yaw, float animationProgress) {
        Vector3f v = new Vector3f(0, -2, 1.99F);
        v.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        ModelPart head = ponyHead.get().getHead();
        head.pivotX = v.x;
        head.pivotY = v.y;
        head.pivotZ = v.z;
        ponyHead.get().setVisible(true);
        ponyHead.get().setHeadRotation(animationProgress, yaw, 0);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int lightUv, int overlayUv, float red, float green, float blue, float alpha) {
        ponyHead.get().headRenderList.accept(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
    }
}
