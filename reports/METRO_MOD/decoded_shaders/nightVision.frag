//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//NightVision shader
//Krogenit 
#version 330 core
in vec2 TexCoords1;
out vec4 color;
 
uniform sampler2D screenTexture;
uniform sampler2D noiseTexture;
uniform sampler2D googleTexture;
uniform sampler2D bloomTexture; 
uniform float elapsedTime; // seconds for noise effect
 
float contrast = 0.5;
uniform float intensityAdjust; // = 1;
uniform float noiseAmplification; // 1
uniform float bufferAmplication; // 1

uniform bool useNightVision;
uniform float exposure;
uniform bool useBloom;
 
void main()
{
	if(useNightVision) {
		// uv coord for noise texture and swirl effect
		vec2 uv;
		uv.x = 0.35*sin(elapsedTime*50.0);
		uv.y = 0.35*cos(elapsedTime*50.0);           
		
		// noise texture color + rotation by time
		vec3 noise = texture2D(noiseTexture, TexCoords1.st + uv).rgb * noiseAmplification;
		
		// google mask
		vec3 googleColor = texture2D(googleTexture, TexCoords1.st).rgb;
		
		// frame buffer + litle swirl
		vec3 sceneColor = texture2D(screenTexture, TexCoords1.st + (noise.xy*0.005)).rgb * bufferAmplication;      
		//vec4 sceneColor = texture2D(screenTexture, TexCoords1.st + (noise.xy*0.005))* bufferAmplication;      
		
		//color intensity - color dominant
		const vec3 lumvec = vec3(0.30, 0.19, 0.11);
		float intentisy = dot(lumvec,sceneColor) ;
		
		// adjust contrast - 0...1
		intentisy = clamp(contrast * (intentisy - 0.5) + 0.5, 0.0, 1.0);
		
		// final green result 0...1
		float green = clamp(intentisy / 0.19, 0.0, 1.0) * intensityAdjust;
		
		// vision color - getting green max
		vec3 visionColor = vec3(0.1,green,0.2);//vec3(0.1, 0.95, 0.2); 
		
		// final color
		color = vec4((sceneColor + (noise*0.02)) * visionColor * googleColor * 3.0,1.0);
	} else {
		if(useBloom) {
			//const float gamma = 1.0;
			vec3 hdrColor = texture(screenTexture, TexCoords1.st).rgb;      
			vec3 bloomColor = texture(bloomTexture, TexCoords1.st).rgb;
			hdrColor += bloomColor; // additive blending

			//vec3 result = vec3(1.0) - exp(-hdrColor * exposure);

			//result = pow(result, vec3(1.0 / gamma));
			color = vec4(hdrColor, 1.0);
			//color = texture2D(screenTexture, TexCoords1.st);   
		} else {
			color = texture2D(screenTexture, TexCoords1.st); 
		}
	}
}