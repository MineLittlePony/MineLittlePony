package com.mumfrey.liteloader.client.overlays;

import net.minecraft.util.ResourceLocation;

import com.mumfrey.liteloader.transformers.access.Accessor;
import com.mumfrey.liteloader.transformers.access.Invoker;

/**
 * Adapter for EntityRenderer to expose some private functionality
 *
 * @author Adam Mummery-Smith
 */
@Accessor("EntityRenderer")
public interface IEntityRenderer
{
    @Accessor("useShader") public abstract boolean getUseShader();
    @Accessor("useShader") public abstract void setUseShader(boolean useShader);

    @Accessor("shaderResourceLocations") public abstract ResourceLocation[] getShaders();

    @Accessor("shaderIndex") public abstract int getShaderIndex();
    @Accessor("shaderIndex") public abstract void setShaderIndex(int shaderIndex);

    @Invoker("loadShader") public abstract void selectShader(ResourceLocation shader);

    @Invoker("getFOVModifier") public abstract float getFOV(float partialTicks, boolean armFOV);

    @Invoker("setupCameraTransform") public abstract void setupCamera(float partialTicks, int pass);
}
