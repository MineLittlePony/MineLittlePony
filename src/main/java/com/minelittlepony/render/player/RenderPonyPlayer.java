package com.minelittlepony.render.player;

import java.util.Map;
import java.util.Optional;

import com.minelittlepony.PonyConfig;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.components.ModelDeadMau5Ears;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.render.PonySkull;
import com.minelittlepony.render.PonySkullRenderer.ISkull;
import com.minelittlepony.util.math.MathUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderPonyPlayer extends RenderPonyBase {

    public static final ISkull SKULL = new PonySkull() {

        private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

        @Override
        public boolean canRender(PonyConfig config) {
            return config.getPonyLevel() != PonyLevel.HUMANS;
        }

        @Override
        public void preRender(boolean transparency) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }

        @Override
        public ResourceLocation getSkinResource(GameProfile profile) {
            if (profile != null) {
                deadMau5.setVisible("deadmau5".equals(profile.getName()));

                Optional<ResourceLocation> skin = HDSkinManager.INSTANCE.getSkinLocation(profile, Type.SKIN, true);
                if (skin.isPresent()) {
                    return skin.get();
                }

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

        @Override
        public void render(float animateTicks, float rotation, float scale) {
            super.render(animateTicks, rotation, scale);
            deadMau5.render(null, animateTicks, 0, 0, rotation, 0, scale);
        }
    }.register(ISkull.PLAYER);

    public RenderPonyPlayer(RenderManager renderManager, boolean useSmallArms, ModelWrapper model) {
        super(renderManager, useSmallArms, model);
    }

    @Override
    public float getShadowScale() {
        return getPony().getMetadata().getSize().getShadowSize();
    }

    @Override
    public float getScaleFactor() {
        return getPony().getMetadata().getSize().getScaleFactor();
    }

    @Override
    protected void transformElytraFlight(AbstractClientPlayer player, double motionX, double motionY, double motionZ, float ticks) {
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.translate(0, player.isSneaking() ? 0.2F : -1, 0);
    }

    private double calculateRoll(AbstractClientPlayer player, double motionX, double motionY, double motionZ) {

        // since model roll should probably be calculated from model rotation rather than entity rotation...
        double roll = MathUtil.sensibleAngle(player.prevRenderYawOffset - player.renderYawOffset);
        double horMotion = Math.sqrt(motionX * motionX + motionZ * motionZ);
        float modelYaw = MathUtil.sensibleAngle(player.renderYawOffset);

        // detecting that we're flying backwards and roll must be inverted
        if (Math.abs(MathUtil.sensibleAngle((float) Math.toDegrees(Math.atan2(motionX, motionZ)) + modelYaw)) > 90) {
            roll *= -1;
        }

        // ayyy magic numbers (after 5 - an approximation of nice looking coefficients calculated by hand)

        // roll might be zero, in which case Math.pow produces +Infinity. Anything x Infinity = NaN.
        double pow = roll != 0 ? Math.pow(Math.abs(roll), -0.191) : 0;

        roll *= horMotion * 5 * (3.6884f * pow);

        assert !Float.isNaN((float)roll);

        return MathHelper.clamp(roll, -54, 54);
    }

    @Override
    protected void transformPegasusFlight(AbstractClientPlayer player, double motionX, double motionY, double motionZ, float yaw, float pitch, float ticks) {
        double dist = Math.sqrt(motionX * motionX + motionZ * motionZ);
        double angle = Math.atan2(motionY, dist);

        if (!player.capabilities.isFlying) {
            if (angle > 0) {
                angle = 0;
            } else {
                angle /= 2;
            }
        }

        angle = MathUtil.clampLimit(angle, Math.PI / 3);

        ponyModel.motionPitch = (float) Math.toDegrees(angle);

        GlStateManager.rotate(ponyModel.motionPitch, 1, 0, 0);

        float roll = (float)calculateRoll(player, motionX,  motionY, motionZ);

        roll = getPony().getMetadata().getInterpolator().interpolate("pegasusRoll", roll, 10);

        GlStateManager.rotate((float)roll, 0, 0, 1);

    }

    //TODO: MC1.13 transformSwimming()
}
