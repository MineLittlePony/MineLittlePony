package com.minelittlepony.render;

import java.util.Map;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.model.components.ModelPonyHead;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.render.ponies.RenderPonySkeleton;
import com.minelittlepony.render.ponies.RenderPonyZombie;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class PonySkullRenderer extends TileEntitySkullRenderer implements IRenderItem {

    private final ModelPonyHead ponyHead = new ModelPonyHead();

    private boolean renderAsPony = false;

    protected boolean transparency = false;

    public void renderSkull(float x, float y, float z, EnumFacing facing, float rotationIn, int skullType, @Nullable GameProfile profile, int destroyStage, float animateTicks, CallbackInfo info) {
        PonyConfig config = MineLittlePony.getConfig();

        switch (skullType)
        {
            default:
            case 0: //skeleton
            case 1: //wither skeleton
                renderAsPony = config.skeletons;
                break;
            case 2: //zombie
                renderAsPony = config.zombies;
                break;
            case 3: // player
                renderAsPony = true;
                break;
            case 4: // creeper
            case 5: // dragon
                renderAsPony = false;
        }

        if (renderAsPony) {
            renderPonySkull(x, y, z, facing, rotationIn, skullType, profile, destroyStage, animateTicks);
        } else {
            super.renderSkull(x, y, z, facing, rotationIn, skullType, profile, destroyStage, animateTicks);
        }
    }

    protected ResourceLocation getSkinResource(GameProfile profile, int skullType) {

        if (skullType == 1) {
            return RenderPonySkeleton.WITHER;
        }
        if (skullType == 2) {
            return RenderPonySkeleton.SKELETON;
        }

        if (skullType == 3) {
            if (profile != null) {
                Minecraft minecraft = Minecraft.getMinecraft();
                Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

                if (map.containsKey(Type.SKIN)) {
                    return minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
                } else {
                    return DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profile));
                }
            }

            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }

        return RenderPonyZombie.ZOMBIE;
    }

    public void renderPonySkull(float x, float y, float z, EnumFacing facing, float rotationIn, int skullType, @Nullable GameProfile profile, int destroyStage, float animateTicks) {
        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            bindTexture(getSkinResource(profile, skullType));
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        switch (facing) {
            case UP:
                GlStateManager.translate(x + 0.5F, y, z + 0.5F);
                break;
            case NORTH:
                GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.74F);
                break;
            case SOUTH:
                GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.26F);
                rotationIn = 180.0F;
                break;
            case WEST:
                GlStateManager.translate(x + 0.74F, y + 0.25F, z + 0.5F);
                rotationIn = 270.0F;
                break;
            case EAST:
            default:
                GlStateManager.translate(x + 0.26F, y + 0.25F, z + 0.5F);
                rotationIn = 90.0F;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.enableAlpha();

        if (skullType == 3) {
            if (transparency) {
                GlStateManager.tryBlendFuncSeparate(SourceFactor.CONSTANT_COLOR, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
            } else {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            }
        }

        ponyHead.render((Entity)null, animateTicks, 0, 0, rotationIn, 0, 0.0625F);
        GlStateManager.popMatrix();

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    @Redirect(method = "renderSkull(FFFLnet/minecraft/util/EnumFacing;FILcom/mojang/authlib/GameProfile;IF)V",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/model/ModelBase;render(Let/minecraft/entity/Entity;FFFFFFF)V"))
    private void redirectRender(ModelBase self, Entity entity, float ticks, float swing, float swingAmount, float age, float headYaw, float headPitch, float scale) {
        if (renderAsPony) {
            self = ponyHead;
        }

        self.render(entity, swing, swingAmount, age, headYaw, headPitch, scale);
    }

    protected void bindTexture(ResourceLocation location, CallbackInfo info) {
        Pony pony = MineLittlePony.getInstance().getManager().getPony(location, false);
        ponyHead.metadata = pony.getMetadata();
        super.bindTexture(location);
    }

    @Override
    public void useTransparency(boolean use) {
        transparency = use;
    }
}
