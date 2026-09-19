#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:fog.glsl>
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:oit.glsl>

uniform sampler2D Sampler0;

layout(location = 0) in float sphericalVertexDistance;
layout(location = 1) in float cylindricalVertexDistance;

layout(location = 2) in vec4 vertexColor;
layout(location = 6) in vec2 texCoord0;

#ifndef OIT_ALPHA_ONLY
layout(location = 0) out vec4 fragColor;
#endif

vec4 calculateFinalColor(vec4 color) {
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(color);
    vec4 fogColor = vec4(FogColor.rgb * color.a, FogColor.a);
    #else
    vec4 fogColor = FogColor;
    #endif

    return apply_fog(color,
      sphericalVertexDistance, cylindricalVertexDistance,
      FogEnvironmentalStart, FogEnvironmentalEnd,
      FogRenderDistanceStart, FogRenderDistanceEnd,
      fogColor
    );
}

void main() {
    if (texture(Sampler0, texCoord0).a < 0.01) {
      discard;
    }

    #ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, vertexColor.a);
    #else
    fragColor = calculateFinalColor(vertexColor);
    #endif
}
