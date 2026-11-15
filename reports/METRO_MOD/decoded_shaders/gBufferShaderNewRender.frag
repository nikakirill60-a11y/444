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
//layout (location = 6) out vec2 gSpecular;
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
//uniform bool useLightMap;
//uniform bool useTexture;
//uniform bool isSky;
//uniform bool useFog;

void main()
{
	if(useEmission) 
		gEmission = texture2D(texture_emission, TexCoords1.xy) * emissionPower;
	else gEmission = vec4(0,0,0,0);

	gPosition = vec4(FragPos, 1.0);

	//if(isSky) {
	//	gNormal = vec4(1.0,1.0,1.0,1.0);
	//} else {
		gNormal = vec4(Normal, specPower);
		if(useSpecular) gEmission.w = texture2D(texture_specular, TexCoords1.xy).x;//gSpecular = texture2D(texture_specular, TexCoords1.xy).xy;
		else gEmission.w = 0.25;//gSpecular = vec2(0.25,0.25);
	//}
	
	gAlbedoSpec = texture2D(texture_diffuse1, TexCoords1.xy)*color;
	//else gAlbedoSpec = color;

	//if(useLightMap) {
		gLightmap = vec2(TexCoords2.xy);
	//} else {
	//	gLightmap = vec2(1,1);
	//}
	
	if(useNormal) {
		gTangent = tangent;
		gNormalMap = texture2D(texture_normal, TexCoords1.xy).xyz;
	} else {
		gTangent = vec3(0,0,0);
		gNormalMap = vec3(0,0,0);
	}
}