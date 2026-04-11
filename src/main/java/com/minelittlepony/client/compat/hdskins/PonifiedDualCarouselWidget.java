package com.minelittlepony.client.compat.hdskins;

import net.minecraft.resources.Identifier;

import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.render.entity.SeaponyRenderer;
import com.minelittlepony.hdskins.client.gui.DualCarouselWidget;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins;
import com.minelittlepony.hdskins.client.resources.NativeImageFilters;
import com.minelittlepony.hdskins.client.resources.TextureLoader;
import com.minelittlepony.hdskins.profile.SkinType;

class PonifiedDualCarouselWidget extends DualCarouselWidget<PonyBodyWidget.State> {

    public PonifiedDualCarouselWidget(GuiSkins screen) {
        super(screen);
        local.addElement(new LegendOverlayWidget(local.bounds, () -> getLocal().getEntity()));
        remote.addElement(new LegendOverlayWidget(remote.bounds, () -> getRemote().getEntity()));
    }

    @Override
    protected PonyBodyWidget createEntity(PlayerSkins<?> textures) {
        return new PonyBodyWidget(textures);
    }

    @Override
    public Identifier getDefaultSkin(SkinType type, String modelVariant) {
        if (type == MineLPHDSkins.seaponySkinType) {
            return NativeImageFilters.GREYSCALE.load(SeaponyRenderer.SEAPONY, SeaponyRenderer.SEAPONY, getExclusion());
        }
        if (type == MineLPHDSkins.nirikSkinType) {
            return super.getDefaultSkin(SkinType.SKIN, modelVariant);
        }

        Wearable wearable = MineLPHDSkins.WEARABLE_TYPES.getOrDefault(type, Wearable.NONE);

        if (wearable != Wearable.NONE) {
            return NativeImageFilters.GREYSCALE.load(wearable.getDefaultTexture(), wearable.getDefaultTexture(), getExclusion());
        }

        return super.getDefaultSkin(type, modelVariant);
    }

    @Override
    public TextureLoader.Exclusion getExclusion() {
        return TriggerPixel::isTriggerPixelCoord;
    }

}
