package com.minelittlepony.render;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.pony.data.Pony;
import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.util.ModUtilities;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer extends TileEntitySkullRenderer implements IRenderItem {

    public static PonySkullRenderer ponyInstance = new PonySkullRenderer();
    private static TileEntitySkullRenderer backup = null;

    private static final Map<Integer, ISkull> skullMap = new HashMap<Integer, ISkull>();

    /**
     * Resolves the games skull renderer to either a specialised pony skull renderer
     * or some other skull renderer depending on the ponyskulls state.
     *
     * Original/Existing renderer is stored to a backup variable as a fallback in case of mods.
     */
    public static TileEntitySkullRenderer resolve() {
        if (MineLittlePony.getConfig().ponyskulls) {
            if (!(instance instanceof PonySkullRenderer)) {
                backup = instance;
                ModUtilities.addRenderer(TileEntitySkull.class, ponyInstance);
                instance = ponyInstance;
            }
        } else {
            if ((instance instanceof PonySkullRenderer)) {
                ponyInstance = (PonySkullRenderer)instance;
                if (backup == null) {
                    backup = new TileEntitySkullRenderer();
                }
                ModUtilities.addRenderer(TileEntitySkull.class, backup);
                instance = backup;
            }
        }

        return instance;
    }

    protected boolean transparency = false;

    @Override
    public void renderSkull(float x, float y, float z, EnumFacing facing, float rotation, int skullType, @Nullable GameProfile profile, int destroyStage, float animateTicks) {

        ISkull skull = skullMap.getOrDefault(skullType, null);

        if (skull == null || !skull.canRender(MineLittlePony.getConfig())) {
            if (backup != null) {
                backup.renderSkull(x, y, z, facing, rotation, skullType, profile, destroyStage, animateTicks);
            } else {
                super.renderSkull(x, y, z, facing, rotation, skullType, profile, destroyStage, animateTicks);
            }

            return;
        }

        float scale = 0.0625F;

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4, 2, 1);
            GlStateManager.translate(scale, scale, scale);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            ResourceLocation skin = skull.getSkinResource(profile);

            skull.bindPony(MineLittlePony.getInstance().getManager().getPony(skin, false));

            bindTexture(skin);
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        rotation = handleRotation(x, y, z, facing, rotation);

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.enableAlpha();

        skull.preRender(transparency);
        skull.render(animateTicks, rotation, scale);

        GlStateManager.popMatrix();

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }

    protected float handleRotation(float x, float y, float z, EnumFacing facing, float rotation) {
        switch (facing) {
            case EAST:
            default:
                GlStateManager.translate(x + 0.26F, y + 0.25F, z + 0.5F);
                return 90;
            case UP:
                GlStateManager.translate(x + 0.5F, y, z + 0.5F);
                break;
            case NORTH:
                GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.74F);
                break;
            case SOUTH:
                GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.26F);
                return 180;
            case WEST:
                GlStateManager.translate(x + 0.74F, y + 0.25F, z + 0.5F);
                return 270;
        }

        return rotation;
    }

    @Override
    public void useTransparency(boolean use) {
        transparency = use;
    }

    /**
     * A skull, just a skull.
     *
     * Implement this interface if you want to extend our behaviour, modders.
     */
    public interface ISkull {

        public static final int SKELETON = 0;
        public static final int WITHER = 1;
        public static final int ZOMBIE = 2;
        public static final int PLAYER = 3;
        public static final int CREEPER = 4;
        public static final int DRAGON = 5;

        void preRender(boolean transparency);

        void render(float animateTicks, float rotation, float scale);

        boolean canRender(PonyConfig config);

        ResourceLocation getSkinResource(@Nullable GameProfile profile);

        void bindPony(Pony pony);

        default ISkull register(int metadataId) {
            PonySkullRenderer.skullMap.put(metadataId, this);
            return this;
        }
    }
}
