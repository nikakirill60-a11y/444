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
uniform sampler2D shadowMapDirect0, shadowMapDirect1, shadowMapDirect2, shadowMapDirect3, shadowMapDirect4;

uniform float gamma;
uniform bool useShadows;
uniform float shadowRenderDistance;
uniform bool forwardRender;

uniform bool useOnlyFlashLight;
uniform bool useSpecular;
uniform bool useEmission;
uniform bool useLightMap;
uniform bool useDirectLight;
uniform bool useBloom;
uniform float directHeight, curHeight;
uniform int directQuality;
uniform bool useTexture;

uniform bool useFog;
uniform vec3 fogColor;
uniform float density;
uniform float fogStart;
uniform float fogEnd;
uniform int fogType;

uniform int pointCount;
uniform int spotCount;

uniform float emissionPower;
uniform bool useFont;
uniform bool useNightVision;
uniform bool useLightning;
uniform bool useNormal;

uniform sampler2D gColor;
uniform sampler2D gLightMap;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gEmission;
uniform sampler2D gTangent;
uniform sampler2D gNormalMap;
uniform sampler2D gSpecularMap;

in vec2 TexCoords1;
in vec2 TexCoords2;
in vec3 glFragPos;
in vec4 glColor;
in vec3 glNormal;
in vec3 glTangent;

uniform struct DirectLight {
	vec3 dir;
	vec3 color;
	mat4 shadViewMat0;
	mat4 shadViewMat1;
	mat4 shadViewMat2;
	mat4 shadViewMat3;
	mat4 shadViewMat4;
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
	float error = 0.0;
	if(forwardRender) error = 0.0005;
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
		float transitionDistance = 5.0;
		distance = distance - (shadowRenderDistance+2.0 - transitionDistance);
		distance = distance / transitionDistance;
		distance = clamp(1.0 - distance, 0.0, 1.0);
		
		if(projCoords.z > 1.0)
			shadow = 0.0;
		else
		//if(projCoords.x <= 0.99 && projCoords.y <= 0.99 && projCoords.x > 0.01 && projCoords.y > 0.01)
		{
			float count = 1.0;
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
		/*else
		{
			float pcfDepth = texture(pointShadowMap, vec3(projCoords.xy, mapIndex)).r; 
			shadow += currentDepth > pcfDepth + error ? 1.0 : 0.0;        
		}*/
		

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
		float dot = dot(normal, delta);
		float distance = length(pos);
		mat4 projMat = sLight.shadViewMat0;
		int mapNumber = 0;
		float shadowDistance = 8.0;
		float minDistance = 0.0;
		
		if(directQuality == 0) {
			mapNumber = 2;
			projMat = sLight.shadViewMat2;
			shadowDistance = 64.0;
			minDistance = 1.0;
			
			distance = distance - (shadowDistance - 20.0);
			distance = distance / 20.0;
			distance = clamp(1.0 - distance, 0.0, 1.0);
		} else if(directQuality == 1) {
			if(distance > 21) {
				mapNumber = 3;
				projMat = sLight.shadViewMat3;
				shadowDistance = 100.0;
				minDistance = 21.0;
				
				minDistance = 1.0 - (minDistance - distance);
				distance = distance - (shadowDistance - 20.0);
				distance = distance / 20.0;
				distance = clamp(1.0 - distance, 0.0, 1.0);

				if(minDistance < 1.0) minDistance = 0;
				else if(minDistance > 1.0) minDistance = 1;
			} else if(distance > 9) {
				mapNumber = 2;
				projMat = sLight.shadViewMat2;
				shadowDistance = 21.0;
				minDistance = 9.0;
				
				minDistance = 1.0 - (minDistance - distance);
				distance = 1.0 - (distance - (shadowDistance));
		
				if(distance < 1.0) distance = 0;
				else if(distance > 1.0) distance = 1;
				if(minDistance < 1.0) minDistance = 0;
				else if(minDistance > 1.0) minDistance = 1;
			} else {
				mapNumber = 1;
				shadowDistance = 18.0;
				projMat = sLight.shadViewMat1;
				
				minDistance = 1.0 - (minDistance - distance);
				distance = 1.0 - (distance - (shadowDistance));
		
				if(distance < 1.0) distance = 0;
				else if(distance > 1.0) distance = 1;
				if(minDistance < 1.0) minDistance = 0;
				else if(minDistance > 1.0) minDistance = 1;
			}
		} else {
			if(distance > 50) {
				mapNumber = 4;
				projMat = sLight.shadViewMat4;
				shadowDistance = 256.0;
				minDistance = 50.0;
			} else if(distance > 21) {
				mapNumber = 3;
				projMat = sLight.shadViewMat3;
				shadowDistance = 100.0;
				minDistance = 21.0;
			} else if(distance > 9) {
				mapNumber = 2;
				projMat = sLight.shadViewMat2;
				shadowDistance = 21.0;
				minDistance = 9.0;
			} else if(distance > 4) {
				mapNumber = 1;
				projMat = sLight.shadViewMat1;
				shadowDistance = 18.0;
				minDistance = 4.0;
			}
			
			minDistance = 1.0 - (minDistance - distance);
			distance = 1.0 - (distance - (shadowDistance));
		
			if(distance < 1.0) distance = 0;
			else if(distance > 1.0) distance = 1;
			if(minDistance < 1.0) minDistance = 0;
			else if(minDistance > 1.0) minDistance = 1;
		}
		
		//float bias = biasByQuality;
		float bias = 0.0;//-0.000000001 * (sqrt(1 - dot) / dot);
		//float bias = 0.0005 * tan(acos(dot));
		//if(forwardRender) bias = 0.00005;

		vec4 lightForShadowPos = projMat * vec4(pos, 1.0);
		vec3 projCoords = lightForShadowPos.xyz / lightForShadowPos.w;
		projCoords = projCoords * 0.5 + 0.5;
		float closestDepth = 0;
		float currentDepth = projCoords.z;
		float shadow = 0.0;
		vec2 texelSize = 1.0 / textureSize(shadowMapDirect0, 0).xy;
		if(mapNumber == 1) {
			//closestDepth = texture(shadowMapDirect1, projCoords.xy).r; 
			texelSize = 1.0 / textureSize(shadowMapDirect1, 0).xy;
		} else if(mapNumber == 2) {
			//closestDepth = texture(shadowMapDirect2, projCoords.xy).r; 
			texelSize = 1.0 / textureSize(shadowMapDirect2, 0).xy;
		} else if(mapNumber == 3) {
			//closestDepth = texture(shadowMapDirect3, projCoords.xy).r; 
			texelSize = 1.0 / textureSize(shadowMapDirect3, 0).xy;
		} else if(mapNumber == 4) {
			//closestDepth = texture(shadowMapDirect3, projCoords.xy).r; 
			texelSize = 1.0 / textureSize(shadowMapDirect4, 0).xy;
		}
		
		int pcfCount = 2;
		for(int x = -pcfCount; x <= pcfCount; ++x) {
			for(int y = -pcfCount; y <= pcfCount; ++y) {
				float pcfDepth = 0;
				if(mapNumber == 0) {
					pcfDepth = texture(shadowMapDirect0, vec2(projCoords.xy + vec2(x, y) * texelSize)).r; 
				} else if(mapNumber == 1) {
					pcfDepth = texture(shadowMapDirect1, vec2(projCoords.xy + vec2(x, y) * texelSize)).r; 
				} else if(mapNumber == 2) {
					pcfDepth = texture(shadowMapDirect2, vec2(projCoords.xy + vec2(x, y) * texelSize)).r; 
				} else if(mapNumber == 3) {
					pcfDepth = texture(shadowMapDirect3, vec2(projCoords.xy + vec2(x, y) * texelSize)).r; 
				} else if(mapNumber == 4) {
					pcfDepth = texture(shadowMapDirect4, vec2(projCoords.xy + vec2(x, y) * texelSize)).r; 
				}
				
				shadow += currentDepth > pcfDepth + bias ? 1.0 * distance * minDistance : 0.0;        
			}    
		} 

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
		vec3 colorL = light.color;
		//if(colorL.r > 5.0) colorL.r -= 5.0;
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
		final_color += colorL * (1.0 - shadow) * lambertTerm * spot * att;
			
		float specular = pow( max(dot(R, E), 0.0), 50.0);    
		final_color += colorL * (1.0 - shadow) * 2.0 * specular * spot * att * specularUnit;		
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
		vec3 colorL = light.color;
		//if(colorL.r > 5.0) colorL.r -= 5.0;
		if(nDotL > 0)
		{
			float rDotE = max(dot(R,E),0.0);
			float power = pow(rDotE, 50.0);
			vec3 amb = colorL * att / 2.0;		
			vec3 dif = colorL * nDotL * att * 2.0; 
			vec3 spec = 6 * colorL * power * att * specularUnit;
			//vec3 spec = specularUnit.rgb * light.color * 10 * att;

			vec3 final = (amb+dif);
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
						spec *= (1.0 - shadow);
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
			return (final + spec) * std_bright;
		}
		else
		{
			float att1 = pow(att, 3);
			return colorL*att1;
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

void main() {
	vec3 pos = vec3(0.0,0.0,0.0);
	vec3 tang = vec3(0.0,0.0,0.0);
	bool hasEmission = false;
	bool hasNormal = false;
	float specularUnit = 1.0;
	vec4 color1 = vec4(0.0,0.0,0.0,0.0);
	vec3 color2 = vec3(1.0,1.0,1.0);
	//float color_lightmap = 1.0;
	vec3 normal = vec3(0.0,0.0,0.0);
	bool fog = true;
	vec3 emissionValue = vec3(0.0,0.0,0.0);
	
	if(forwardRender) {
		pos = glFragPos;
		normal = glNormal;
		tang = glTangent;
		hasNormal = useNormal;
		specularUnit = 1.0;
		
		if(useTexture) {
			color1 = texture(gColor, TexCoords1) * glColor;
			if(useFont) color1.rgb = glColor.rgb;
		} else color1 = glColor;
		
		if(useLightMap) {
			if(!useOnlyFlashLight) color2 = texture(lightmap, vec2(0.05, TexCoords2.y)).rgb;
			else color2 = texture(lightmap, TexCoords2).rgb;	
			//color_lightmap = texture(lightmap, vec2(TexCoords2.x, 0.05)).r;
		}
		
		if(useSpecular) specularUnit = texture(gSpecularMap, TexCoords1).r;
		if(useEmission) {
			hasEmission = true;
			emissionValue = texture(gEmission, TexCoords1).rgb * emissionPower;
		}
	} else {
		vec4 position = texture(gPosition, TexCoords1);
		pos = position.xyz;

		fog = position.w == 1.0;
			
		vec4 nor = texture(gNormal, TexCoords1);
		normal = nor.xyz;

		vec4 emission = texture(gEmission ,TexCoords1);
		emissionValue = emission.rgb;
		color1 = texture(gColor, TexCoords1.xy);
		vec4 lightMapCoord = texture(gLightMap, TexCoords1);
		//color_lightmap = texture(lightmap, vec2(lightMapCoord.x, 0.0)).r;
		if(!useOnlyFlashLight) lightMapCoord.x = 0.05;
		color2 = texture(lightmap, lightMapCoord.xy).xyz;

		vec4 tang1 = texture(gTangent, TexCoords1);
		tang = tang1.xyz;
		hasNormal = tang.x != 0.0 || tang.y != 0.0 || tang.z != 0.0;
		specularUnit = emission.w;//texture(gSpecularMap, TexCoords1.xy);
		hasEmission = emission.x != 0.0 || emission.y != 0.0 || emission.z != 0.0;
	}
	
	bool isSky = normal.x >= 1.0 && normal.y >= 1.0 && normal.z >= 1.0;
	
	if(isSky) outputColor.xyz = vec3(0.6,0.6,0.6);
	else outputColor.xyz = color2;
	outputColor.w = color1.w;
	
	if(hasEmission) outputColor.rgb += emissionValue;
	vec3 unitNormal = normal;
	
	if(hasNormal) {
		//tang = normalize(tang - dot(tang, normal) * normal);
		vec4 normalTex = texture(gNormalMap, TexCoords1);
		normalTex.g = 1.0 - normalTex.g;
		//normalTex.r = 1.0-normalTex.r;
		vec4 normalMapValue = 2.0 * normalTex - 1.0;
		vec3 bitang = cross(normal, tang);
		unitNormal = normalize(normalMapValue.rgb);
	
		mat3 toTangentSpace = mat3(
			tang, bitang, normal
		);
		
		unitNormal = normalize(toTangentSpace * unitNormal);
	}
	
	vec3 E = normalize(-pos.xyz); 
	
	if(useDirectLight && !isSky && (!forwardRender || useLightMap)) {
		float directHeightDif = 4.0;
		float heightDif = directHeight - directHeightDif;
		if(curHeight > heightDif) {
			float intensity = clamp((curHeight - heightDif) / directHeightDif, 0.0, 1.0);
			vec3 delta = directLight[0].dir;
			
			//if(color2.r > 0.171) {
				outputColor.rgb += calculateDirectLight(directLight[0], 1.0, E, delta, unitNormal, pos, specularUnit) * intensity;
			//} else {
				//outputColor.rgb += calculateDirectLight(directLight[0], 1.0, E, delta, unitNormal, pos, specularUnit) * intensity * color2.r;
				//outputColor.rgb *= color2.r;
			//}
			outputColor.rgb = smoothLight(outputColor.rgb, 2.5);
		}
	}
	
	if(!isSky && useLightning) {
		int shadowIndex = 0;
		for(int i = 0; i < spotCount; ++i) {
			//if(color_lightmap > 0.0275 || spotLights[i].color.r > 5.0) {
				vec3 delta = (spotLights[i].position - pos.xyz);
				float dist = length(delta);
					
				if(dist < spotLights[i].dist)
					outputColor.rgb += calculateSpotLight(spotLights[i], shadowIndex, 1.0, E, delta, dist, unitNormal, pos, specularUnit);
					
				outputColor.rgb = smoothLight(outputColor.rgb, 2.5);
					
				if(spotLights[i].mapId != 0.0)
					shadowIndex++;
			//}
		}
		
		shadowIndex = 0;
		for(int i = 0; i < pointCount; ++i) {
			//if(color_lightmap > 0.0275 || lights[i].color.r > 5.0) {
				vec3 delta = (lights[i].position - pos.xyz);
				float dist = length(delta);
					//outputColor = vec4(pos.xyz ,1.0);
				if(dist < lights[i].attenuation)
					outputColor.rgb += calculatePointLight(lights[i], shadowIndex, 1.0, E, delta, dist, unitNormal, pos, specularUnit);

				outputColor.rgb = smoothLight(outputColor.rgb, 2.5);
						
				if(lights[i].mapId != 0.0)
					shadowIndex++;
			//}
		}
	}
	
	outputColor.rgb *= color1.rgb;
	
	if(useNightVision) {
		outputColor.rgb *= vec3(2,4,2.5);
	}
	
	if(useBloom) {
		float brightness = dot(outputColor.rgb, vec3(0.2126, 0.7152, 0.0722));
		if(brightness > 1.0 + gamma / 3.0)
			bloomColor = vec4(outputColor.rgb, 1.0);
		else
			bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
	} else bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
	
	if(useFog && fog) {
		float dist = length(pos);
		if(fogType == 0.0) {
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

















