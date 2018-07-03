package com.voxelmodpack.hdskins.mixin;

import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.CubeMapRegistry;
import com.voxelmodpack.hdskins.gui.GuiItemStackButton;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    @Shadow @Final
    private static ResourceLocation[] TITLE_PANORAMA_PATHS;

    private int cubemapRandomResourceIndex = -2;

    private static final int SKINS = 5000;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        cubemapRandomResourceIndex = CubeMapRegistry.getRandomResourceIndex(true);
    }

    @Inject(method = "initGui()V", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        ItemStack itemStack = new ItemStack(Items.LEATHER_LEGGINGS);
        Items.LEATHER_LEGGINGS.setColor(itemStack, 0x3c5dcb);
        this.buttonList.add(new GuiItemStackButton(SKINS, width - 50, height - 50, itemStack));
    }

    @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("RETURN"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == SKINS) {
            this.mc.displayGuiScreen(HDSkinManager.INSTANCE.createSkinsGui());
        }
    }

    @Redirect(method = "drawPanorama(IIF)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiMainMenu;TITLE_PANORAMA_PATHS:[Lnet/minecraft/util/ResourceLocation;"))
    private ResourceLocation[] getPanoramaArray() {
        return cubemapRandomResourceIndex < 0 ? TITLE_PANORAMA_PATHS : CubeMapRegistry.getResource(cubemapRandomResourceIndex);
    }
}
