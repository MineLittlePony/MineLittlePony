package com.minelittlepony.client.render.tileentities.skull;

import com.google.common.collect.Maps;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;

import org.lwjgl.opengl.GL11;

import java.util.Map;
import javax.annotation.Nullable;

import static com.mojang.blaze3d.platform.GlStateManager.*;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer extends SkullBlockEntityRenderer {

    public static PonySkullRenderer ponyInstance = new PonySkullRenderer();
    private static SkullBlockEntityRenderer backup = null;

    private static final Map<SkullBlock.SkullType, ISkull> skullMap = SystemUtil.consume(Maps.newHashMap(), (skullMap) -> {
        skullMap.put(SkullBlock.Type.SKELETON, new SkeletonSkullRenderer());
        skullMap.put(SkullBlock.Type.WITHER_SKELETON, new WitherSkullRenderer());
        skullMap.put(SkullBlock.Type.ZOMBIE, new ZombieSkullRenderer());
        skullMap.put(SkullBlock.Type.PLAYER, new PlayerSkullRenderer());
    });

    /**
     * Resolves the games skull renderer to either a specialised pony skull renderer
     * or some other skull renderer depending on the ponyskulls state.
     *
     * Original/Existing renderer is stored to a backup variable as a fallback in case of mods.
     */
    public static SkullBlockEntityRenderer resolve() {
        if (PonyConfig.INSTANCE.ponyskulls.get()) {
            if (!(INSTANCE instanceof PonySkullRenderer)) {
                backup = INSTANCE;
                BlockEntityRendererRegistry.INSTANCE.register(SkullBlockEntity.class, ponyInstance);
                INSTANCE = ponyInstance;
            }
        } else {
            if ((INSTANCE instanceof PonySkullRenderer)) {
                ponyInstance = (PonySkullRenderer) INSTANCE;
                if (backup == null) {
                    backup = new SkullBlockEntityRenderer();
                }
                BlockEntityRendererRegistry.INSTANCE.register(SkullBlockEntity.class, backup);
                INSTANCE = backup;
            }
        }

        return INSTANCE;
    }

    @Override
    public void render(float x, float y, float z, @Nullable Direction facing, float rotation, SkullBlock.SkullType skullType, @Nullable GameProfile profile, int destroyStage, float animateTicks) {

        ISkull skull = skullMap.get(skullType);

        if (skull == null || !skull.canRender()) {
            if (backup != null) {
                backup.render(x, y, z, facing, rotation, skullType, profile, destroyStage, animateTicks);
            } else {
                super.render(x, y, z, facing, rotation, skullType, profile, destroyStage, animateTicks);
            }

            return;
        }

        float scale = 0.0625F;

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGE_TEXTURES[destroyStage]);
            matrixMode(GL11.GL_TEXTURE);
            pushMatrix();
            scalef(4, 2, 1);
            translatef(scale, scale, scale);
            matrixMode(GL11.GL_MODELVIEW);
        } else {
            Identifier skin = skull.getSkinResource(profile);

            skull.bindPony(MineLittlePony.getInstance().getManager().getPony(skin));

            bindTexture(skin);
        }

        pushMatrix();
        disableCull();

        rotation = handleRotation(x, y, z, facing, rotation);

        enableRescaleNormal();
        scalef(-1, -1, 1);
        enableAlphaTest();

        skull.preRender(LevitatingItemRenderer.usesTransparency());
        skull.render(animateTicks, rotation, scale);

        popMatrix();

        if (destroyStage >= 0) {
            matrixMode(GL11.GL_TEXTURE);
            popMatrix();
            matrixMode(GL11.GL_MODELVIEW);
        }
    }

    protected float handleRotation(float x, float y, float z, @Nullable Direction facing, float rotation) {
        if (facing == null) {
            translatef(x + 0.5F, y, z + 0.5F);

            return rotation;
        }

        switch (facing) {
            case NORTH:
                translatef(x + 0.5F, y + 0.25F, z + 0.74F);
                break;
            case SOUTH:
                translatef(x + 0.5F, y + 0.25F, z + 0.26F);
                return 180;
            case WEST:
                translatef(x + 0.74F, y + 0.25F, z + 0.5F);
                return 270;
            case EAST:
            default:
                translatef(x + 0.26F, y + 0.25F, z + 0.5F);
                break;
        }

        return rotation;
    }

    /**
     * A skull, just a skull.
     *
     * Implement this interface if you want to extend our behaviour, modders.
     */
    public interface ISkull {

        void preRender(boolean transparency);

        void render(float animateTicks, float rotation, float scale);

        boolean canRender();

        Identifier getSkinResource(@Nullable GameProfile profile);

        void bindPony(IPony pony);
    }
}
