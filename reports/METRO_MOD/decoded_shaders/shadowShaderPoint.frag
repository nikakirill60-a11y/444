#version 330 core
layout (location = 0) out vec4 color;

in vec4 FragPos;

uniform mat4 shadowMatrices[6];
uniform int layerAdd;

in vec2 textCoord1;
uniform sampler2D texture_d;

void main()
{
	vec4 color1 = texture2D(texture_d, textCoord1);
	
	if(color1.a <= 0.5)
		discard;
		
	color = color1;

    //float lightDistance = length(FragPos.xyz - lightPos);
    
    // map to [0;1] range by dividing by far_plane
    //lightDistance = lightDistance / far_plane;
    
    // write this as modified depth
    //gl_FragDepth = lightDistance;
}