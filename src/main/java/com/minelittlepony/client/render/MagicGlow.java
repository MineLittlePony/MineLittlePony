package com.minelittlepony.client.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

public interface MagicGlow {
    RenderPipeline /*ENTITY_EYES*/ ENTITY_MAGIC_GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET)
                    .withLocation("pipeline/magic_glow")
                    .withVertexShader("core/entity")
                    .withFragmentShader("core/entity")
                    .withShaderDefine("EMISSIVE")
                    .withShaderDefine("NO_OVERLAY")
                    .withShaderDefine("NO_CARDINAL_LIGHTING")
                    .withSampler("Sampler0")
                    .withBlend(BlendFunction.LIGHTNING)
                    .withDepthWrite(false)
                    .withCull(false) /*added*/
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST) /*added*/
                    .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                            VertexFormat.DrawMode.QUADS)
                    .build()
    );

    Supplier<RenderLayer> MAGIC = Suppliers.memoize(() -> {
        return RenderLayer.of("mlp_magic_glow", 1536, false, true, RenderPipelines.ENTITY_EYES,
                RenderLayer.MultiPhaseParameters.builder()
                        .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                        .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                        .target(RenderPhase.TRANSLUCENT_TARGET)
                        .build(false));
    });

    BiFunction<Identifier, Integer, RenderLayer> TINTED_LAYER = Util.memoize((texture, color) -> {
        return RenderLayer.of("mlp_tint_layer", 1536, false, true, RenderPipelines.ENTITY_EYES,
                RenderLayer.MultiPhaseParameters.builder()
                        .texture(new Colored(texture, color))
                        .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                        .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                        .target(RenderPhase.TRANSLUCENT_TARGET)
                        .build(true));
    });

    static RenderLayer getRenderLayer() {
        return MAGIC.get();
    }

    static RenderLayer getColoured(Identifier texture, int color) {
        return TINTED_LAYER.apply(texture, color);
    }

    static void bootstrap() {
    }

    class Colored extends RenderPhase.Texture {
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

        public Colored(Identifier texture, int color) {
            super(texture, false); // Pass 'false' for mipmap, or 'true' if you want mipmaps
            this.red = ColorHelper.getRedFloat(color);
            this.green = ColorHelper.getGreenFloat(color);
            this.blue = ColorHelper.getBlueFloat(color);
            this.alpha = 0.8F;
        }

        @Override
        public void startDrawing() {
            super.startDrawing();
        }

        @Override
        public void endDrawing() {
            super.endDrawing();
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other)
                    && ((Colored) other).red == red
                    && ((Colored) other).green == green
                    && ((Colored) other).blue == blue
                    && ((Colored) other).alpha == alpha;
        }
    }
}
