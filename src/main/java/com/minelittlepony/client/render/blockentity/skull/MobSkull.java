package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.base.Suppliers;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;

import java.util.function.Supplier;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import org.jetbrains.annotations.Nullable;

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
        Vec3f v = new Vec3f(0, -2, 1.99F);
        v.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));
        ModelPart head = ponyHead.get().getHead();
        head.pivotX = v.getX();
        head.pivotY = v.getY();
        head.pivotZ = v.getZ();
        ponyHead.get().setVisible(true);
        ponyHead.get().setHeadRotation(animationProgress, yaw, 0);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int lightUv, int overlayUv, float red, float green, float blue, float alpha) {
        ponyHead.get().headRenderList.accept(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
    }
}
