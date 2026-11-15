//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//gBuffer shader
//Krogenit 
#version 330 core
layout (location = 0) out vec4 gAlbedoSpec;
layout (location = 1) out vec2 gLightmap;
layout (location = 2) out vec4 gPosition;
layout (location = 3) out vec4 gNormal;
layout (location = 4) out vec3 gTangent;
layout (location = 5) out vec3 gNormalMap;
layout (location = 6) out vec4 gEmission;

in vec2 TexCoords1;
in vec2 TexCoords2;
in vec3 Normal;
in vec3 FragPos;
in vec4 color;
in vec3 tangent;

uniform float specPower;

uniform sampler2D texture_diffuse1;
uniform sampler2D texture_emission;
uniform float emissionPower;
uniform sampler2D texture_normal;
uniform sampler2D texture_specular;

uniform bool useNormal;
uniform bool useSpecular;
uniform bool useEmission;
uniform bool useLightMap;
uniform bool useTexture;
uniform bool isSky;
uniform bool useFog;
uniform bool useBlocks;

void main()
{
	if(isSky) {
		gNormal = vec4(1.0,1.0,1.0,1.0);
	} else {
		gNormal = vec4(Normal, specPower);
		if(useSpecular) {
			if(!useBlocks) {
				gEmission.w = texture(texture_specular, TexCoords1).r;//gSpecular = texture2D(texture_specular, TexCoords1.xy).xy;
			} else if(!useEmission) {
				gEmission.w = texture(texture_emission, TexCoords1).a;
			}
		} else {
			gEmission.w = 0.25;//gSpecular = vec2(0.25,0.25);
		}
	}
	
	if(useEmission) {
		gEmission = texture(texture_emission, TexCoords1);
		gEmission.rgb *= emissionPower;
	} else {
		gEmission = vec4(0,0,0,0);
	}

	gPosition = vec4(FragPos, useFog);
	
	if(useTexture) {
		gAlbedoSpec = texture(texture_diffuse1, TexCoords1) * color;
	} else { 
		gAlbedoSpec = color;
	}

	if(useLightMap) {
		gLightmap = TexCoords2;
	} else {
		gLightmap = vec2(1,1);
	}
	
	if(useNormal) {
		gTangent = tangent;
		gNormalMap = texture(texture_normal, TexCoords1).xyz;
	} else {
		gTangent = vec3(0,0,0);
		gNormalMap = vec3(0,0,0);
	}
}