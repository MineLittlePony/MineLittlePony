package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PlayerPonySkull implements ISkull {
    private final Map<PlayerModelKey<AbstractPonyModel<?>>, AbstractPonyModel<?>> modelCache = new HashMap<>();
    private final DJPon3EarsModel deadMau5 = ModelType.DJ_PON_3.createModel();

    @Override
    public boolean canRender(PonyConfig config) {
        return config.ponyskulls.get() && config.ponyLevel.get() != PonyLevel.HUMANS;
    }

    @Override
    public Identifier getSkinResource(@Nullable ProfileComponent profile) {
        if (profile == null) {
            return DefaultSkinHelper.getTexture();
        }
        return MinecraftClient.getInstance().getPlayerSkinCache().get(profile).getTextures().body().texturePath();
    }

    @Override
    public void render(MatrixStack stack, State state, OrderedRenderCommandQueue queue, Pony pony, RenderLayer layer) {
        Race race = pony.race();
        boolean renderingEars = state.profile != null && "deadmau5".equals(state.profile.getGameProfile().name());
        if (race.isHuman()) {
            race = Race.EARTH;
            if (!renderingEars) {
                return;
            }
        }
        AbstractPonyModel<?> ponyHead = modelCache.computeIfAbsent(ModelType.getPlayerModel(race), key -> key.steveKey().createModel());
        PlayerPonyRenderState ponyState = new PlayerPonyRenderState();
        ponyState.pony = pony;
        ponyState.race = pony.race();
        ponyState.attributes.size = pony.size();
        ponyState.attributes.metadata = pony.metadata();

        int color = ColorHelper.getWhite(state.alpha);

        stack.push();
        MatrixStack copyStack = new MatrixStack();
        queue.getBatchingQueue(0).submitCustom(stack, layer, (entry, vertices) -> {
            Vector3f v = new Vector3f(0, -2, 2);
            v.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));
            ponyHead.setVisible(true);
            ponyHead.setAngles(ponyState);
            ponyHead.getHead().setOrigin(v.x, v.y, v.z);
            ponyHead.setHeadRotation(state.poweredTicks, state.yaw, 0);
            copyStack.peek().getPositionMatrix().set(entry.getPositionMatrix());
            copyStack.peek().getNormalMatrix().set(entry.getNormalMatrix());
            ponyHead.headRenderList.accept(copyStack, vertices, state.light, OverlayTexture.DEFAULT_UV, color);
        });

        stack.pop();
        if (renderingEars) {
            stack.push();
            stack.scale(1.3333334f, 1.3333334f, 1.3333334f);
            stack.translate(0, 0.05F, 0);
            queue.getBatchingQueue(0).submitModel(deadMau5, state, stack, layer, state.light, OverlayTexture.DEFAULT_UV, color, null, state.outlineColor, state.crumblingOverlay);
            stack.pop();
        }
    }
}
