package com.minelittlepony.api.model.skull;

import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Represents a mob head or player skull
 *
 * Implement this interface if you want to extend our behaviour, modders.
 */
public interface Skull<T extends Skull.State> {
    T createState();

    void submit(PoseStack stack, T state, SubmitNodeCollector frame, RenderType layer);

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
