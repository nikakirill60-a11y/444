//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Sky shader
//Krogenit 
#version 330 core
layout (location = 0) out vec4 outputColor;
layout (location = 1) out vec4 bloomColor;

uniform bool useBloom;
uniform bool useTexture;
uniform bool useEmission;
uniform float emissionPower;

uniform float gamma;

uniform bool useFog;
uniform vec3 fogColor;
uniform float density;
uniform float fogStart;
uniform float fogEnd;
uniform int fogType;

in vec2 textureCoords;
in vec4 color;
in vec3 normal;
in vec3 position;

uniform sampler2D gColor;
uniform sampler2D gEmission;

void main() {
	

	if(useEmission) {
		outputColor.a = 1.0;
		outputColor.rgb = texture(gEmission, textureCoords.xy).rgb * emissionPower;
	} else outputColor = vec4(1,1,1,1);
	
	if(useTexture)
		outputColor *= texture(gColor, textureCoords.xy) * color;
	else 
		outputColor *= color;
	
	if(useBloom) {
		float brightness = dot(outputColor.rgb, vec3(0.2126, 0.7152, 0.0722));
		if(brightness > 1.0 + gamma / 3.0)
			bloomColor = vec4(outputColor.rgb, 1.0);
		else
			bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
	} else bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
	
	if(useFog) {
		float dist = length(position);
		if(fogType == 0.0) {
			float visibility = (fogEnd - dist) / (fogEnd - fogStart);
			visibility = clamp(visibility, 0.0, 1.0);
			outputColor.rgb = mix(fogColor, outputColor.rgb, visibility);
		} else {
			//float visibility = exp(-pow((dist*density), gradient));
			float visibility = exp(-dist*density);
			visibility = clamp(visibility, 0.0, 1.0);
			outputColor.rgb = mix(fogColor, outputColor.rgb, visibility);
		}
	}
}