package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.armour.ArmourRendererPlugin;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer {
    public static final PonySkullRenderer INSTANCE = new PonySkullRenderer();

    public static final RenderStateDataKey<Data> DATA_KEY = RenderStateDataKey.create(() -> "Pony_Skull_State");
    @Nullable
    private final AtomicReference<PonySkullRenderer.Data> ponySkullState = new AtomicReference<>(null);

    public Data popState() {
        return ponySkullState.getAndSet(null);
    }

    public void pushState(@Nullable Data data) {
        ponySkullState.set(data);
    }

    private Function<SkullBlock.Type, ISkull> skulls;

    private PonySkullRenderer() {
        reload();
    }

    public void reload() {
        skulls = Util.memoize(type -> type instanceof SkullBlock.Types t ? switch (t) {
            case SKELETON -> new MobSkull<>(SkeleponyRenderer.SKELETON, MobRenderers.SKELETON, ModelType.SKELETON, SkeleponyRenderer.State::new);
            case WITHER_SKELETON -> new MobSkull<>(SkeleponyRenderer.WITHER, MobRenderers.SKELETON, ModelType.SKELETON, SkeleponyRenderer.State::new);
            case ZOMBIE -> new MobSkull<>(ZomponyRenderer.ZOMBIE, MobRenderers.ZOMBIE, ModelType.ZOMBIE, PonyRenderState::new);
            case PIGLIN -> new MobSkull<>(PonyPiglinRenderer.PIGLIN, MobRenderers.PIGLIN, ModelType.PIGLIN, PonyPiglinRenderer.State::new);
            case PLAYER -> new PlayerPonySkull();
            default -> null;
        } : null);
    }

    @Nullable
    public Data getSkullState(SkullBlock.Type skullType, @Nullable ResolvableProfile profile, @Nullable Identifier overrideTexture, float animation) {
        @Nullable
        ISkull skull = skulls.apply(skullType);
        return skull == null ? null :new Data(skull, overrideTexture, profile, animation);
    }

    /**
     * A skull, just a skull.
     *
     * Implement this interface if you want to extend our behaviour, modders.
     */
    public interface ISkull {
        void render(PoseStack stack, State state, SubmitNodeCollector frame, RenderType layer);

        boolean canRender(Pony pony, @Nullable ResolvableProfile profile, PonyConfig config);

        Identifier getSkinResource(@Nullable ResolvableProfile profile);

        class State extends SkullModelBase.State {
            public float alpha;
            public int outlineColor;
            public int light;
            public @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay;
            public @Nullable ResolvableProfile profile;
            public Pony pony;
        }
    }

    public record Data(ISkull model, @Nullable Identifier overrideTexture, @Nullable ResolvableProfile profile, float animation) {

        public boolean canRender() {
            return model.canRender(pony(), profile, PonyConfig.getInstance());
        }

        public RenderType layer() {
            return RenderTypes.entityTranslucent(pony().texture());
        }

        public Pony pony() {
            return Pony.getManager().getPony(overrideTexture == null ? model.getSkinResource(profile) : overrideTexture);
        }

        public boolean render(PoseStack matrices, SubmitNodeCollector frame, int light, int outlineColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
            final Pony pony = pony();

            if (!model.canRender(pony, profile, PonyConfig.getInstance())) {
                return false;
            }

            ISkull.State skullModelState = new ISkull.State();
            skullModelState.animationPos = animation;
            skullModelState.alpha = ArmourRendererPlugin.INSTANCE.get().getArmourAlpha(EquipmentSlot.HEAD, EquipmentClientInfo.LayerType.HUMANOID);
            skullModelState.outlineColor = outlineColor;
            skullModelState.light = light;
            skullModelState.crumblingOverlay = crumblingOverlay;
            skullModelState.profile = profile;
            skullModelState.pony = pony;
            model.render(matrices, skullModelState, frame, RenderTypes.entityTranslucent(pony.texture()));

            return true;
        }
    }
}
