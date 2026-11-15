//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Light Vertex Shader 1.4 Deffered Rendering
//Krogenit 
#version 120

attribute vec3 in_position;
attribute vec2 in_textureCoords;
attribute vec3 in_normal;
attribute vec3 in_tangent;

varying vec2 TexCoords1;
varying vec2 TexCoords2;
varying vec3 glFragPos;
varying vec4 glColor;
varying vec3 glNormal;
varying vec3 glTangent;

uniform bool newRenderer;
uniform bool forwardRender;

uniform mat4 projMat, viewMat;

void main() 
{	
	if(forwardRender) {
		if(newRenderer) {
			vec4 pos = gl_ModelViewMatrix * vec4(in_position, 1.0);
			glNormal = gl_NormalMatrix * in_normal;
			//TexCoords1 = vec2(in_textureCoords.x, -in_textureCoords.y);
			TexCoords1 = in_textureCoords;
			TexCoords2 = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
			glFragPos = pos.xyz;
			glColor = gl_Color;	
			gl_Position = gl_ProjectionMatrix * pos;
			glTangent = gl_NormalMatrix * in_tangent;	
		} else {
			vec4 pos = gl_ModelViewMatrix * gl_Vertex;
			glNormal = gl_NormalMatrix * gl_Normal;
			TexCoords1 = gl_MultiTexCoord0.xy;
			TexCoords2 = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
			glFragPos = pos.xyz;
			glColor = gl_Color;	
			gl_Position = gl_ProjectionMatrix * pos;
			glTangent = gl_NormalMatrix * in_tangent;	
		}	
	} else {
		TexCoords1 = gl_MultiTexCoord0.xy;
		gl_Position = gl_Vertex;
	}
}