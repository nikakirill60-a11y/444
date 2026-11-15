#version 120

attribute vec3 in_position;
attribute vec3 in_jointIndices;
attribute vec3 in_weights;
attribute vec2 in_textureCoords;
attribute vec3 in_normal;

varying vec2 textCoord;

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex
uniform bool dynBones;
uniform bool newRenderer;
uniform mat4 jointTransforms[MAX_JOINTS];

void main()
{
	if(dynBones) {
		vec4 totalLocalPos = vec4(0.0);
	
		for(int i=0;i<MAX_WEIGHTS;i++){
			mat4 jointTransform = jointTransforms[int(in_jointIndices[i])];
			vec4 posePosition = jointTransform * (vec4(in_position, 1.0));
			totalLocalPos += posePosition * in_weights[i];
		}
		
		//textCoord = vec2(in_textureCoords.x, -in_textureCoords.y);
		textCoord = in_textureCoords;
		gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * totalLocalPos;
	} else if(newRenderer) {
		//textCoord = vec2(in_textureCoords.x, -in_textureCoords.y);
		textCoord = in_textureCoords;
		gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * vec4(in_position, 1.0);
	} else {
		textCoord = gl_MultiTexCoord0.xy;
		gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;
	}
}