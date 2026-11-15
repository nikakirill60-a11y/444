//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//gBuffer shader
//Krogenit 
#version 120

attribute vec3 in_tangent;

varying vec2 TexCoords1, TexCoords2;
varying vec3 Normal;
varying vec3 FragPos;
varying vec4 color;
varying vec3 tangent;

void main() {
	TexCoords2 = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
	color = gl_Color;
	
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
	Normal = normalize(gl_NormalMatrix * gl_Normal);
	TexCoords1 = gl_MultiTexCoord0.xy;
		
	tangent = gl_NormalMatrix * in_tangent;
	FragPos = pos.xyz;
	gl_Position = gl_ProjectionMatrix * pos;
}