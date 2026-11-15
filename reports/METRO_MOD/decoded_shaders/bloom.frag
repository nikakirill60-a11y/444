//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Bloom shader
//Krogenit 

#version 330 core
out vec4 FragColor;
  
in vec2 textCoords;

uniform sampler2D image;
  
uniform bool horizontal;
uniform float offset[5] = float[](0.0, 1.0, 2.0, 3.0, 4.0);
uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
//uniform float offset[3] = float[](0.0, 1.3846153846, 3.2307692308);
//uniform float weight[3] = float[](0.2270270270, 0.3162162162, 0.0702702703);

void main()
{             
    vec2 tex_offset = 1.0 / textureSize(image, 0); 

    vec3 result = texture(image, textCoords).rgb * weight[0]; 
    if(horizontal) {
        for(int i = 1; i < 5; ++i) {
            result += texture(image, textCoords + vec2(tex_offset.x * offset[i], 0.0)).rgb * weight[i];
            result += texture(image, textCoords - vec2(tex_offset.x * offset[i], 0.0)).rgb * weight[i];
        }
    } else {
		for(int i = 1; i < 5; ++i) {
            result += texture(image, textCoords + vec2(0.0, tex_offset.y * offset[i])).rgb * weight[i];
            result += texture(image, textCoords - vec2(0.0, tex_offset.y * offset[i])).rgb * weight[i];
        }
    }
    FragColor = vec4(result, 1.0);
}