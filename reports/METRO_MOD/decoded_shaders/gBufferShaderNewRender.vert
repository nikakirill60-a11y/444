//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//gBuffer shader
//Krogenit 
#version 120

attribute vec3 in_position;
attribute vec2 in_textureCoords;
attribute vec3 in_normal;
attribute vec3 in_tangent;

varying vec2 TexCoords1, TexCoords2;
varying vec3 Normal;
varying vec3 FragPos;
varying vec4 color;
varying vec3 tangent;

void main() {
	TexCoords2 = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
	color = gl_Color;
	
	vec4 pos = gl_ModelViewMatrix * vec4(in_position, 1.0);
	Normal = normalize(gl_NormalMatrix * in_normal);
	TexCoords1 = in_textureCoords;
		
	tangent = gl_NormalMatrix * in_tangent;
	FragPos = pos.xyz;
	gl_Position = gl_ProjectionMatrix * pos;
}