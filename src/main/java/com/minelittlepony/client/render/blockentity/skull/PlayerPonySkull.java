package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.model.PlayerModelKey;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.component.ResolvableProfile;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PlayerPonySkull implements ISkull {
    private final Map<PlayerModelKey<AbstractPonyModel<?>>, AbstractPonyModel<?>> modelCache = new HashMap<>();
    private final DJPon3EarsModel deadMau5 = ModelType.DJ_PON_3.createModel();

    @Override
    public boolean canRender(Pony pony, @Nullable ResolvableProfile profile, PonyConfig config) {
        return config.ponyskulls.get()
            && config.ponyLevel.get() != PonyLevel.HUMANS
            && (!pony.race().isHuman() || hasMouseEars(profile) || (profile != null && "Dinnerbone".equals(profile.partialProfile().name())));
    }

    @Override
    public Identifier getSkinResource(@Nullable ResolvableProfile profile) {
        if (profile == null) {
            return DefaultPlayerSkin.getDefaultTexture();
        }
        return Minecraft.getInstance().playerSkinRenderCache().getOrDefault(profile).playerSkin().body().texturePath();
    }

    @Override
    public void render(PoseStack stack, State state, SubmitNodeCollector frame, RenderType layer) {
        Race race = state.pony.race();
        if (race.isHuman()) {
            race = Race.EARTH;
        }

        AbstractPonyModel<?> ponyHead = modelCache.computeIfAbsent(ModelType.getPlayerModel(race), key -> key.steveKey().createModel());
        PlayerPonyRenderState ponyState = new PlayerPonyRenderState();
        ponyState.pony = state.pony;
        ponyState.race = state.pony.race();
        ponyState.attributes.size = SizePreset.NORMAL;
        ponyState.attributes.metadata = state.pony.metadata();

        int color = ARGB.white(state.alpha);

        stack.pushPose();
        if (state.profile != null && "Dinnerbone".equals(state.profile.partialProfile().name())) {
            stack.translate(0, -0.5F, 0);
            stack.mulPose(Axis.XP.rotationDegrees(180));
            stack.mulPose(Axis.YP.rotationDegrees(180));
        }

        PoseStack copyStack = new PoseStack();
        frame.order(0).submitCustomGeometry(stack, layer, (entry, vertices) -> {
            Vector3f v = new Vector3f(0, -2, 2);
            v.rotate(Axis.YP.rotationDegrees(state.yRot));
            ponyHead.setupAnim(ponyState);
            ponyHead.getHead().setPos(v.x, v.y, v.z);
            ponyHead.setHeadRotation(state.animationPos, state.yRot, 0);
            copyStack.last().set(entry);
            ponyHead.headRenderList.accept(copyStack, vertices, state.light, OverlayTexture.NO_OVERLAY, color);
        });

        stack.popPose();
        if (hasMouseEars(state.profile)) {
            stack.pushPose();
            stack.scale(1.3333334f, 1.3333334f, 1.3333334f);
            stack.translate(0, 0.05F, 0);
            frame.order(0).submitModel(deadMau5, state, stack, layer, state.light, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor, state.crumblingOverlay);
            stack.popPose();
        }
    }

    static boolean hasMouseEars(@Nullable ResolvableProfile profile) {
        return profile != null && "deadmau5".equals(profile.partialProfile().name());
    }
}
