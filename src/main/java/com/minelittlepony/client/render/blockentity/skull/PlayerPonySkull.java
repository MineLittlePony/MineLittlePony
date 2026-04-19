package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.model.PlayerModelKey;
import com.minelittlepony.api.model.skull.Skull;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import net.minecraft.world.item.component.ResolvableProfile;

import org.jetbrains.annotations.Nullable;

public class PlayerPonySkull implements Skull<PonyHeadModel.State> {
    private final Function<PlayerModelKey<AbstractPonyModel<?>>, PonyHeadModel> models = Util.memoize(key -> new PonyHeadModel(key.steveKey().createModel()));
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
    public PonyHeadModel.State createState() {
        return new PonyHeadModel.State();
    }

    @Override
    public void submit(PoseStack stack, PonyHeadModel.State state, SubmitNodeCollector frame, RenderType layer) {
        Race race = state.pony.race();
        if (race.isHuman()) {
            race = Race.EARTH;
        }

        state.ponyState = new PlayerPonyRenderState();
        state.ponyState.pony = state.pony;
        state.ponyState.race = state.pony.race();
        state.ponyState.attributes.size = SizePreset.NORMAL;
        state.ponyState.attributes.metadata = state.pony.metadata();

        int color = ARGB.white(state.alpha);

        stack.pushPose();
        if (state.profile != null && "Dinnerbone".equals(state.profile.partialProfile().name())) {
            stack.translate(0, -0.5F, 0);
            stack.mulPose(Axis.YP.rotationDegrees(-state.yRot)
                    .rotateLocalZ(MathUtil.Angles._180_DEG)
                    .rotateLocalY(state.yRot * Mth.DEG_TO_RAD)
            );
        }
        frame.order(0).submitModel(models.apply(ModelType.getPlayerModel(race)), state, stack, layer, state.light, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor, state.crumblingOverlay);
        stack.popPose();
        if (hasMouseEars(state.profile)) {
            stack.pushPose();
            stack.scale(DJPon3EarsModel.DEFAULT_SCALE, DJPon3EarsModel.DEFAULT_SCALE, DJPon3EarsModel.DEFAULT_SCALE);
            stack.translate(0, 0.05F, 0);
            frame.order(0).submitModel(deadMau5, state, stack, layer, state.light, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor, state.crumblingOverlay);
            stack.popPose();
        }
    }

    static boolean hasMouseEars(@Nullable ResolvableProfile profile) {
        return profile != null && "deadmau5".equals(profile.partialProfile().name());
    }
}
