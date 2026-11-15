//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Sky shader
//Krogenit 
#version 120

varying vec2 textureCoords;
varying vec4 color;
varying vec3 normal;
varying vec3 position;

void main() {
	color = gl_Color;
	
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
	normal = normalize(gl_NormalMatrix * gl_Normal);
	textureCoords = gl_MultiTexCoord0.xy;

	position = pos.xyz;
	gl_Position = gl_ProjectionMatrix * pos;
}