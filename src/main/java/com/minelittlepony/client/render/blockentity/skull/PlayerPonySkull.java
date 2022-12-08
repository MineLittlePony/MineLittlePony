package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.base.Suppliers;
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
import java.util.function.Supplier;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.RotationAxis;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PlayerPonySkull implements ISkull {
    private AbstractPonyModel<?> ponyHead;
    private final Map<PlayerModelKey<?, AbstractPonyModel<?>>, AbstractPonyModel<?>> modelCache = new HashMap<>();

    private final Supplier<DJPon3EarsModel> deadMau5 = Suppliers.memoize(ModelType.DJ_PON_3::createModel);

    @Override
    public boolean canRender(PonyConfig config) {
        return config.ponyskulls.get() && config.ponyLevel.get() != PonyLevel.HUMANS;
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        deadMau5.get().setVisible(true);//profile != null && "deadmau5".equals(profile.getName()));

        if (profile != null) {
            Identifier skin = SkinsProxy.instance.getSkinTexture(profile);

            if (skin != null) {
                return skin;
            }

            return DefaultSkinHelper.getTexture(Uuids.getUuidFromProfile(profile));
        }

        return DefaultSkinHelper.getTexture();
    }

    @Override
    public boolean bindPony(IPony pony) {
        Race race = pony.getRace();
        if (race.isHuman()) {
            return false;
        }
        ponyHead = modelCache.computeIfAbsent(ModelType.getPlayerModel(race), key -> key.getKey(false).createModel());
        ponyHead.setMetadata(pony.getMetadata());
        return true;
    }

    @Override
    public void setAngles(float yaw, float animationProgress) {
        Vector3f v = new Vector3f(0, -2, 2);
        v.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        ponyHead.getHead().pivotX = v.x;
        ponyHead.getHead().pivotY = v.y;
        ponyHead.getHead().pivotZ = v.z;
        ponyHead.setVisible(true);
        ponyHead.setHeadRotation(animationProgress, yaw, 0);
        deadMau5.get().setHeadRotation(animationProgress, yaw, 0);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int lightUv, int overlayUv, float red, float green, float blue, float alpha) {
        ponyHead.renderHead(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
        ponyHead.renderHelmet(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
        deadMau5.get().render(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
    }
}
