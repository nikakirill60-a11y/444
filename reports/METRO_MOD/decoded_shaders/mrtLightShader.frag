//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Light Fragment Shader 1.4 UBO's, Shadows, Deffered Shading, MRT update
//Krogenit
//GLSL 3.30
#version 330

in vec2 TexCoords1;
out vec4 outputColor;

uniform sampler2D textureColor;
uniform sampler2D lightmap;
uniform sampler2D lightmapTexture;
uniform sampler2D shadowMap;
uniform sampler2D gPosition;
uniform sampler2D gNormal;

//uniform mat4 projMat;
uniform mat4 viewMat;

void main()
{
	vec4 pos = viewMat * vec4(texture2D(gPosition, TexCoords1).rgb, 1.0);
    vec3 normal = texture2D(gNormal, TexCoords1).rgb;
	
    vec4 color1 = texture2D(textureColor, TexCoords1);
	vec4 test = texture2D(lightmap, TexCoords1);
	vec4 color2 = texture2D(lightmapTexture, test.xy);
	//float std_bright = gamma / 2.0 + 0.8;
	//vec3 E = normalize(-pos.xyz); 
	
	//outputColor = vec4(normal.xyz, 1.0);
	
	outputColor = color2 * color1;
}

















