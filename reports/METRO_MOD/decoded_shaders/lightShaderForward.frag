//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Light Fragment Shader 1.6 UBO's, Shadows update, MRT, Deffered Rendering
//Krogenit
//GLSL 3.30
#version 330
layout (location = 0) out vec4 outputColor;
layout (location = 1) out vec4 bloomColor;

#define MAX_LIGHTS 64
#define MAX_SPOT_LIGHTS 64
#define POINT_SHADOWS 144
#define DIRECT_LIGHTS 1

uniform sampler2D lightmap;
uniform sampler2DArray shadowMap;
uniform sampler2DArray pointShadowMap;
uniform sampler2D shadowMapDirect;

uniform float gamma;
uniform bool useShadows;
uniform float shadowRenderDistance;

uniform bool useOnlyFlashLight;
uniform bool useSpecular;
uniform bool useEmission;
uniform bool useLightMap;
uniform bool useDirectLight;
uniform bool useBloom;
uniform float directHeight, curHeight;
uniform bool useTexture;
uniform bool useFog;
uniform vec3 fogColor;
uniform bool useNormal;

uniform float density;
uniform float fogStart;
uniform float fogEnd;
uniform int fogType;
uniform int pointCount;
uniform int spotCount;

uniform struct DirectLight {
	vec3 dir;
	vec3 color;
	mat4 shadViewMat;
} directLight[DIRECT_LIGHTS];

struct PointLight {
   vec3 position;//0,1,2
   float attenuation;//3
   vec3 color;//4,5,6
   float mapId;//7
};// Size: 8 * sizeof (float)

struct SpotLight {
   vec3 position;//0,1,2
   float attenuation;//3
   vec3 color;//4,5,6
   float angle;//7
   vec3 coneDirection;//8,9,10
   float dist;//11
   vec3 padding;
   float mapId;
};// Size: 16 * sizeof (float)

struct LightMatrix {
	   mat4 viewMat;//16
};// Size: 16 * sizeof (float)

layout(std140)uniform Lights {
	PointLight lights[MAX_LIGHTS];
};

layout(std140)uniform SpotLights {
	SpotLight spotLights[MAX_SPOT_LIGHTS];
};

layout(std140)uniform SpotLightsMatrix {
	LightMatrix spotLightsMatrix[MAX_SPOT_LIGHTS];
};

layout(std140)uniform plMat {
	LightMatrix plm[POINT_SHADOWS];
};

float PointShadowCalculation(in vec3 delta, in PointLight light, in float mapIndex, 
in vec3 projCoords, in vec3 normal, in vec3 pos)
{
	float error = 0.0005;
	int count = 1;
	float step = 0.5;
	float dotL = dot(normal, delta);
	if(dotL < 0)
	{
		return 0.0;
	}
	else
	{
		projCoords = projCoords * 0.5 + 0.5;
		float currentDepth = projCoords.z; 
		float shadow = 0.0;
		vec2 texelSize = 1.0 / textureSize(pointShadowMap, 0).xy;
		
		float distance = length(pos);
		distance = distance - (shadowRenderDistance+2.0);
		//distance = distance / transitionDistance;
		distance = clamp(1.0 - distance, 0.0, 1.0);
		
		if(projCoords.x <= 0.99 && projCoords.y <= 0.99 && projCoords.x > 0.01 && projCoords.y > 0.01)
		{
			for(float x = -count; x <= count; ++x)
			{
				for(float y = -count; y <= count; ++y)
				{
					float pcfDepth = texture(pointShadowMap, vec3(projCoords.xy + vec2(x, y) * texelSize, mapIndex)).r; 
					shadow += currentDepth > pcfDepth + error ? 1.0 * distance : 0.0;
				}    
			}
			shadow /= 9.0;
		}
		else
		{
			float pcfDepth = texture(pointShadowMap, vec3(projCoords.xy, mapIndex)).r; 
			shadow += currentDepth > pcfDepth + error ? 1.0 : 0.0;        
		}
		
		if(projCoords.z > 1.0)
			shadow = 0.0;

		return shadow;
	}
}

float ShadowCalculation(in vec3 delta, in SpotLight sLight, in float mapIndex, 
in int shadowProjIndex, in vec3 normal, in vec3 pos)
{
	float dotL = dot(normal, delta);
	if(dotL < 0)
	{
		return 0.0;
	}
	else
	{
		vec4 lightForShadowPos = spotLightsMatrix[shadowProjIndex].viewMat * vec4(pos, 1.0);
		vec3 projCoords = lightForShadowPos.xyz / lightForShadowPos.w;
		projCoords = projCoords * 0.5 + 0.5;
		float closestDepth = texture(shadowMap, vec3(projCoords.xy, mapIndex)).r; 
		float currentDepth = projCoords.z;
		float shadow = 0.0;
		vec2 texelSize = 1.0 / textureSize(shadowMap, 0).xy;
		for(int x = -1; x <= 1; ++x)
		{
			for(int y = -1; y <= 1; ++y)
			{
				float pcfDepth = texture(shadowMap, vec3(projCoords.xy + vec2(x, y) * texelSize, mapIndex)).r; 
				shadow += currentDepth > pcfDepth ? 1.0 : 0.0;        
			}    
		}
		shadow /= 9.0;
		
		if(projCoords.z > 1.0)
			shadow = 0.0;

		return shadow;
	}
}

float DirectShadowCalculation(in vec3 delta, in DirectLight sLight, in vec3 normal, in vec3 pos)
{
	float dotL = dot(normal, delta);
	if(dotL < 0)
	{
		return 0.0;
	}
	else
	{
		float shadowDistance = 32.0;
		float transitionDistance = 20.0;
		float distance = length(pos);
		distance = distance - (shadowDistance - transitionDistance);
		distance = distance / transitionDistance;
	
		vec4 lightForShadowPos = sLight.shadViewMat * vec4(pos, 1.0);
		distance = clamp(1.0 - distance, 0.0, 1.0);
		vec3 projCoords = lightForShadowPos.xyz / lightForShadowPos.w;
		projCoords = projCoords * 0.5 + 0.5;
		float closestDepth = texture2D(shadowMapDirect, projCoords.xy).r; 
		float currentDepth = projCoords.z;
		float shadow = 0.0;
		vec2 texelSize = 1.0 / textureSize(shadowMapDirect, 0).xy;
		int pcfCount = 2;
		for(int x = -pcfCount; x <= pcfCount; ++x) {
			for(int y = -pcfCount; y <= pcfCount; ++y) {
				float pcfDepth = texture2D(shadowMapDirect, vec2(projCoords.xy + vec2(x, y) * texelSize)).r; 
				shadow += currentDepth > pcfDepth + 0.0001 ? 1.0 * distance : 0.0;        
			}    
		}
		//float pcfDepth = texture2D(shadowMapDirect, vec2(projCoords.xy)).r; 
				//shadow += currentDepth > pcfDepth + 0.00 ? 1.0 * (distance) : 0.0;        
		shadow /= 25.0;
		
		if(projCoords.z > 1.0)
			shadow = 0.0;

		return shadow;
	}
}

vec3 calculateSpotLight(in SpotLight light, in int shadowIndex, in float std_bright, in vec3 E, 
in vec3 delta, in float dist, in vec3 normal, in vec3 pos, in float specularUnit)
{
	vec3 L = normalize(delta);
	vec3 R = normalize(-reflect(L,normal));
	
	float att = 1.0 / (light.attenuation +
        light.attenuation * dist +
        light.attenuation * (dist * dist));
		
	att = clamp(att, 0.0, 2.0);		
	vec3 final_color = vec3(0,0,0);

	vec3 D = normalize(light.coneDirection);
	float cos_cur_angle = dot(-L, D);
	float cos_inner_cone_angle = -1.0;
	float cos_inner_minus_outer_angle = cos_inner_cone_angle + light.angle;
	float spot = clamp((cos_cur_angle + light.angle) / cos_inner_minus_outer_angle, 0.0, 1.0);
	float lambertTerm = max(dot(normal,L), 0.0);

	if(lambertTerm > 0.0)
	{
		//float maxvar = max(max(outputColor.x, outputColor.y),outputColor.z);
		//float smoothFactor = 1.25 - maxvar;
		//vec3 smoothVector = vec3(smoothFactor,smoothFactor,smoothFactor);
		//smoothVector = clamp(smoothVector, 0.0, 1.0);
		float shadow = 0;
		if(useShadows && light.mapId != 0.0)
		{
			float shadowMapIndex = (light.mapId-1.0);
			shadow = ShadowCalculation(delta, light, shadowMapIndex, shadowIndex, normal, pos);
		}
		final_color += light.color * (1.0 - shadow) * lambertTerm * spot * att;
			
		float specular = pow( max(dot(R, E), 0.0), 50.0);    
		final_color += light.color * (1.0 - shadow) * 2.0 * specular * spot * att * specularUnit;		
	}
	
	return final_color * std_bright;
}

vec3 calculatePointLight(in PointLight light, in int shadowIndex, in float std_bright, in vec3 E, 
in vec3 delta, in float dist, in vec3 normal, in vec3 pos, in float specularUnit)
{
	vec3 L = normalize(delta);
	vec3 R = normalize(-reflect(L,normal));
	float nDotL = max(dot(normal,L), 0.0);
	
	float att = 1.0 - dist / light.attenuation;
	
	if(att > 0)
	{
		if(nDotL > 0)
		{
			float rDotE = max(dot(R,E),0.0);
			float power = pow(rDotE, 50.0);
			vec3 amb = light.color * att / 2.0;		
			vec3 dif = light.color * nDotL * att * 2.0; 
			vec3 spec = 6 * light.color * power * att * specularUnit;
			//vec3 spec = specularUnit.rgb * light.color * 10 * att;

			vec3 final = (amb+dif+spec) * std_bright;
			//vec3 amb_final = (amb) * std_bright;
			
			if(useShadows && shadowIndex < 32 && light.mapId != 0.0)
			{
				for(int i=0;i<6;i++)
				{
					int shadowProjIndex = shadowIndex*6+i;
					float shadowMapIndex = (light.mapId-1.0)*6+i;
					
					vec4 lightForShadowPos = plm[shadowProjIndex].viewMat * vec4(pos, 1.0);
					vec3 projCoords = lightForShadowPos.xyz / lightForShadowPos.w;
					//vec3 delta1 =  projCoords.xyz - pos.xyz;
					
					//if(delta1.x <= shadowDist && delta1.y <= shadowDist && delta1.z <= shadowDist)
					{
						float shadow = PointShadowCalculation(delta, light, shadowMapIndex, projCoords, normal, pos);
					
						//shadow = clamp(shadow, 0.0, att);
						if(shadow > 0)
						{
							shadow *= (1.0-att/2.0);
						}
						
						final *= (1.0 - shadow);
					}
					
					//amb_final *= (1.0 - shadow*(1.0 - att*att));
				}
			}
			return final;
		}
		else
		{
			float att1 = pow(att, 3);
			return light.color*att1;
			//return vec3(0,0,0);
		}
	}
	else
	{
		return vec3(0,0,0);
	}
}

vec3 calculateDirectLight(in DirectLight light, in float std_bright, in vec3 E, 
in vec3 delta, in vec3 normal, in vec3 pos, in float specularUnit)
{
	//vec3 N = perturb_normal();
	vec3 L = normalize(delta);
	vec3 R = normalize(-reflect(L,normal));
	float nDotL = max(dot(normal,L), 0.0);

	if(nDotL > 0)
	{
		float rDotE = max(dot(R,E),0.0);
		float power = pow(rDotE, 50.0);
		vec3 amb = light.color / 6.0;		
		vec3 dif = light.color * nDotL; 
		vec3 spec = 3.0 * light.color * power * specularUnit;
		//vec3 spec = specularUnit.rgb * light.color * 10 * att;

		vec3 final = (amb+dif+spec) * std_bright;
		//vec3 amb_final = (amb) * std_bright;
			
		if(useShadows)
		{
			//vec4 lightForShadowPos = light.viewMat * vec4(pos, 1.0);
			//vec3 projCoords = lightForShadowPos.xyz / lightForShadowPos.w;
			//vec3 delta1 =  projCoords.xyz - pos.xyz;
					
			//if(delta1.x <= shadowDist && delta1.y <= shadowDist && delta1.z <= shadowDist)
			{
				float shadow = DirectShadowCalculation(delta, light, normal, pos);						
				final *= (1.0 - shadow);
			}
		}
		return final;
	} else return vec3(0,0,0);
}

uniform sampler2D gColor;
uniform sampler2D gEmission;
uniform sampler2D gNormalMap;
uniform sampler2D gSpecularMap;

uniform float emissionPower;
uniform bool useFont;
uniform bool useNightVision;
uniform bool useLightning;

in vec2 TexCoords1;
in vec2 TexCoords2;
in vec3 glFragPos;
in vec4 glColor;
in vec3 glNormal;
in vec3 glTangent;

vec3 smoothLight(in vec3 outputColor, in float max)
{
	if(outputColor.x > max)
	{
		float dif = outputColor.x - max;
		float per = dif / outputColor.x;
		outputColor.y -= (outputColor.y*per);
		outputColor.z -= (outputColor.z*per);
		outputColor.x -= dif;
		
	}
	
	if(outputColor.y > max)
	{
		float dif = outputColor.y - max;
		float per = dif / outputColor.y;
		outputColor.x -= (outputColor.x*per);
		outputColor.z -= (outputColor.z*per);
		outputColor.y -= dif;
	}
	
	if(outputColor.z > max)
	{
		float dif = outputColor.z - max;
		float per = dif / outputColor.z;
		outputColor.x -= (outputColor.x*per);
		outputColor.y -= (outputColor.y*per);
		outputColor.z -= dif;
	}
	
	return outputColor;
}

void main()
{
	vec3 pos = glFragPos;
	vec3 normal = glNormal;
	vec4 color1 = vec4(0,0,0,0);
	vec3 color2 = vec3(1,1,1);
	float specularUnit = 1.0;
	
	if(useTexture) {
		color1 = texture2D(gColor, TexCoords1.xy)*glColor;
		if(useFont) color1.rgb = vec3(1,1,1);
	} else color1 = glColor;
	
	if(useLightMap) {
		if(!useOnlyFlashLight) color2 = texture2D(lightmap, vec2(0.05, TexCoords2.y)).xyz;
		else color2 = texture2D(lightmap, TexCoords2.xy).xyz;	
	}
	
	if(useSpecular) specularUnit = texture2D(gSpecularMap, TexCoords1.xy).r;

	outputColor.xyz = color2;
	outputColor.w = color1.w;
	
	if(useEmission) outputColor.rgb += texture2D(gEmission, TexCoords1.xy).rgb * emissionPower;
	
	if(useNormal) {
		vec4 normalTex = texture2D(gNormalMap, TexCoords1.xy);
		normalTex.g = 1.0-normalTex.g;
		vec4 normalMapValue = 2.0 * normalTex - 1.0;
		vec3 tang = normalize(glTangent - dot(glTangent, normal) * normal);
		mat3 toTangentSpace = mat3(
			1, 0, 0,
			0, 1, 0,
			0, 0, 1
		);
		vec3 bitang = cross(normal, tang);
		normal = normalize(normalMapValue.rgb);
	
		toTangentSpace = mat3(
			tang, bitang, normal
		);
		
		normal = normalize(toTangentSpace * normal);
	}
	
	vec3 E = normalize(-pos.xyz); 
	
	if(useDirectLight) {
		float directHeightDif = 4.0;
		float heightDif = directHeight - directHeightDif;
		if(curHeight > heightDif) {
			if(useLightMap) {
				float intensity = clamp((curHeight - heightDif) / directHeightDif, 0.0, 1.0);
				vec3 delta = directLight[0].dir;
				outputColor.rgb += calculateDirectLight(directLight[0], 1.0, E, delta, normal, pos, specularUnit) * intensity;
				outputColor.rgb = smoothLight(outputColor.rgb, 2.5);
			}
		}
	}
	
	if(useLightning) {
		int shadowIndex = 0;
		for(int i = 0; i < spotCount; ++i) {
			vec3 delta = (spotLights[i].position - pos.xyz);
			float dist = length(delta);
					
			if(dist < spotLights[i].dist)
				outputColor.rgb += calculateSpotLight(spotLights[i], shadowIndex, 1.0, E, delta, dist, normal, pos, specularUnit);
					
			outputColor.rgb = smoothLight(outputColor.rgb, 2.5);
					
			if(spotLights[i].mapId != 0.0)
				shadowIndex++;
		}
			
		shadowIndex = 0;
		for(int i = 0; i < pointCount; ++i) {
			vec3 delta = (lights[i].position - pos.xyz);
			float dist = length(delta);
				//outputColor = vec4(pos.xyz ,1.0);
			if(dist < lights[i].attenuation)
				outputColor.rgb += calculatePointLight(lights[i], shadowIndex, 1.0, E, delta, dist, normal, pos, specularUnit);

			outputColor.rgb = smoothLight(outputColor.rgb, 2.5);
						
			if(lights[i].mapId != 0.0)
				shadowIndex++;
		}
	}
	
	outputColor.rgb *= color1.rgb;
	
	if(useNightVision) {
		outputColor.rgb *= vec3(2,4,2.5);
	}
	
	if(useBloom) {
		float brightness = dot(outputColor.rgb, vec3(0.2126, 0.7152, 0.0722));
		if(brightness > 1 + gamma / 3.0)
			bloomColor = vec4(outputColor.rgb, 1.0);
		else
			bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
	} else bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
	
	if(useFog) {
		float dist = length(pos);

		if(fogType == 0) {
			float visibility = (fogEnd - dist) / (fogEnd - fogStart);
			visibility = clamp(visibility, 0.0, 1.0);
			outputColor.rgb = mix(fogColor, outputColor.rgb, visibility);
		} else {
			//float visibility = exp(-pow((dist*density), gradient));
			float visibility = exp(-dist*density);
			visibility = clamp(visibility, 0.0, 1.0);
			outputColor.rgb = mix(fogColor, outputColor.rgb, visibility);
		}
	}
}

















