#version 330 core

in vec2 textCoord;

uniform sampler2D texture_d;

void main()
{             
	vec4 color1 = texture2D(texture_d, textCoord);
	
	if(color1.a <= 0.5)
		discard;
	
	//if(enableTest && color.a == 0.0)
		//discard;
	//gl_FragColor = vec4(0, 0, 0, color.w);
	//gl_FragDepth = 1.0 - color.w;
}