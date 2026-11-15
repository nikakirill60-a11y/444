//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//gBuffer shader with bones
//Krogenit 
#version 120

attribute vec3 in_position;
attribute vec3 in_jointIndices;
attribute vec3 in_weights;
attribute vec2 in_textureCoords;
attribute vec3 in_normal;
attribute vec4 in_tangent;

varying vec2 TexCoords1, TexCoords2;
varying vec3 Normal;
varying vec3 FragPos;
varying vec4 color;
varying vec3 tangent;

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex
uniform mat4 jointTransforms[MAX_JOINTS];

void main() {
	TexCoords2 = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
	color = gl_Color;
	
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	for(int i=0;i<MAX_WEIGHTS;i++){
		mat4 jointTransform = jointTransforms[int(in_jointIndices[i])];
		vec4 posePosition = jointTransform * vec4(in_position, 1.0);
		totalLocalPos += posePosition * in_weights[i];
		
		vec4 worldNormal = jointTransform * vec4(in_normal, 0.0);
		totalNormal += worldNormal * in_weights[i];
	}
		
	TexCoords1 = in_textureCoords;
	
	Normal = normalize(gl_NormalMatrix * totalNormal.xyz);
	vec4 pos = gl_ModelViewMatrix * totalLocalPos;
	tangent = gl_NormalMatrix * in_tangent.xyz;
	
	FragPos = pos.xyz;
	gl_Position = gl_ProjectionMatrix * pos;
}