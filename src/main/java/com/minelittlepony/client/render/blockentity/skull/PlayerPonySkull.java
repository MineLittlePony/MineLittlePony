package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import com.mojang.authlib.GameProfile;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.Vec3f;

import org.jetbrains.annotations.Nullable;

public class PlayerPonySkull implements ISkull {
    private AbstractPonyModel<?> ponyHead;
    private final Map<PlayerModelKey<?, AbstractPonyModel<?>>, AbstractPonyModel<?>> modelCache = new HashMap<>();

    private final DJPon3EarsModel deadMau5 = ModelType.DJ_PON_3.createModel();

    private boolean renderingEars;

    @Override
    public boolean canRender(PonyConfig config) {
        return config.ponyskulls.get() && config.ponyLevel.get() != PonyLevel.HUMANS;
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        renderingEars = profile != null && "deadmau5".equals(profile.getName());

        if (profile != null) {
            Identifier skin = SkinsProxy.instance.getSkinTexture(profile);

            if (skin != null) {
                return skin;
            }

            return DefaultSkinHelper.getTexture(DynamicSerializableUuid.getUuidFromProfile(profile));
        }

        return DefaultSkinHelper.getTexture();
    }

    @Override
    public boolean bindPony(IPony pony) {
        Race race = pony.race();
        if (race.isHuman()) {
            race = Race.EARTH;
            if (!renderingEars) {
                return false;
            }
        }
        ponyHead = modelCache.computeIfAbsent(ModelType.getPlayerModel(race), key -> key.getKey(false).createModel());
        ponyHead.setMetadata(pony.metadata());
        return true;
    }

    @Override
    public void setAngles(float yaw, float animationProgress) {
        Vec3f v = new Vec3f(0, -2, 2);
        v.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));
        ponyHead.getHead().pivotX = v.getX();
        ponyHead.getHead().pivotY = v.getY();
        ponyHead.getHead().pivotZ = v.getZ();
        ponyHead.setVisible(true);
        ponyHead.setHeadRotation(animationProgress, yaw, 0);
        if (renderingEars) {
            deadMau5.setHeadRotation(animationProgress, yaw, 0);
        }
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int lightUv, int overlayUv, float red, float green, float blue, float alpha) {
        stack.push();
        ponyHead.headRenderList.accept(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
        stack.pop();
        stack.push();
        ponyHead.helmetRenderList.accept(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
        stack.pop();
        if (renderingEars) {
            stack.push();
            stack.scale(1.3333334f, 1.3333334f, 1.3333334f);
            stack.translate(0, 0.05F, 0);
            deadMau5.render(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
            stack.pop();
        }
    }
}
