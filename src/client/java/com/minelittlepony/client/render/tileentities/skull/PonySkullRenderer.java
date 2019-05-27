package com.minelittlepony.client.render.tileentities.skull;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.MineLPClient;
import com.minelittlepony.client.ducks.IRenderItem;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer extends SkullBlockEntityRenderer implements IRenderItem {

    public static final int SKELETON = 0;
    public static final int WITHER = 1;
    public static final int ZOMBIE = 2;
    public static final int PLAYER = 3;
    public static final int CREEPER = 4;
    public static final int DRAGON = 5;

    public static PonySkullRenderer ponyInstance = new PonySkullRenderer();
    private static SkullBlockEntityRenderer backup = null;

    private final Map<SkullBlock.SkullType, ISkull> skullMap = new HashMap<>();

    private PonySkullRenderer() {
        skullMap.put(SkullBlock.Type.SKELETON, new SkeletonSkullRenderer());
        skullMap.put(SkullBlock.Type.WITHER_SKELETON, new WitherSkullRenderer());
        skullMap.put(SkullBlock.Type.ZOMBIE, new ZombieSkullRenderer());
        skullMap.put(SkullBlock.Type.PLAYER, new PlayerSkullRenderer());
    }

    /**
     * Resolves the games skull renderer to either a specialised pony skull renderer
     * or some other skull renderer depending on the ponyskulls state.
     *
     * Original/Existing renderer is stored to a backup variable as a fallback in case of mods.
     */
    public static SkullBlockEntityRenderer resolve() {
        if (MineLittlePony.getInstance().getConfig().ponyskulls) {
            if (!(INSTANCE instanceof PonySkullRenderer)) {
                backup = INSTANCE;
                MineLPClient.getInstance().getModUtilities().addRenderer(SkullBlockEntity.class, ponyInstance);
                INSTANCE = ponyInstance;
            }
        } else {
            if ((INSTANCE instanceof PonySkullRenderer)) {
                ponyInstance = (PonySkullRenderer) INSTANCE;
                if (backup == null) {
                    backup = new SkullBlockEntityRenderer();
                }
                MineLPClient.getInstance().getModUtilities().addRenderer(SkullBlockEntity.class, backup);
                INSTANCE = backup;
            }
        }

        return INSTANCE;
    }

    protected boolean transparency = false;

    @Override
    public void render(float x, float y, float z, @Nullable Direction facing, float rotation, SkullBlock.SkullType skullType, @Nullable GameProfile profile, int destroyStage, float animateTicks) {

        ISkull skull = skullMap.get(skullType);

        if (skull == null || !skull.canRender(MineLittlePony.getInstance().getConfig())) {
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
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4, 2, 1);
            GlStateManager.translatef(scale, scale, scale);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            Identifier skin = skull.getSkinResource(profile);

            skull.bindPony(MineLittlePony.getInstance().getManager().getPony(skin));

            bindTexture(skin);
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        rotation = handleRotation(x, y, z, facing, rotation);

        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1, -1, 1);
        GlStateManager.enableAlphaTest();

        skull.preRender(transparency);
        skull.render(animateTicks, rotation, scale);

        GlStateManager.popMatrix();

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }

    protected float handleRotation(float x, float y, float z, @Nullable Direction facing, float rotation) {
        switch (facing) {
            case EAST:
            default:
                GlStateManager.translatef(x + 0.26F, y + 0.25F, z + 0.5F);
                return 90;
            case UP:
                GlStateManager.translatef(x + 0.5F, y, z + 0.5F);
                break;
            case NORTH:
                GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.74F);
                break;
            case SOUTH:
                GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.26F);
                return 180;
            case WEST:
                GlStateManager.translatef(x + 0.74F, y + 0.25F, z + 0.5F);
                return 270;
        }

        return rotation;
    }

    @Override
    public void useTransparency(boolean use) {
        transparency = use;
    }

    public boolean usesTransparency() {
        return transparency;
    }

    /**
     * A skull, just a skull.
     *
     * Implement this interface if you want to extend our behaviour, modders.
     */
    public interface ISkull {

        void preRender(boolean transparency);

        void render(float animateTicks, float rotation, float scale);

        boolean canRender(PonyConfig config);

        Identifier getSkinResource(@Nullable GameProfile profile);

        void bindPony(IPony pony);
    }
}
