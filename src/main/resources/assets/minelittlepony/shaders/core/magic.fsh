#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    if (texture(Sampler0, texCoord0).a < 0.01) {
      discard;
    }
    fragColor = apply_fog(vertexColor,
      sphericalVertexDistance, cylindricalVertexDistance,
      FogEnvironmentalStart, FogEnvironmentalEnd,
      FogRenderDistanceStart, FogRenderDistanceEnd,
      FogColor
    );
}
