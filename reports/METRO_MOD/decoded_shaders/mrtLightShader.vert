//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Light Vertex Shader 1.4 MRT rendering
//Krogenit 

varying vec2 TexCoords1;

uniform sampler2D texture;
uniform sampler2D lightmap;
uniform sampler2D lightmapTexture;
uniform sampler2D shadowMap;
uniform sampler2D gPosition;
uniform sampler2D gNormal;

//uniform mat4 projMat;
uniform mat4 viewMat;

void main()
{
    TexCoords1 = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewMatrix * gl_Vertex;
}

/*varying vec3 normal, mSpecular;
varying	vec4 pos;
varying vec4 color;
varying vec4 lightForShadowPos;

uniform mat4 testMat;

varying vec4 textCoord1;
varying vec4 textCoord2;

varying float shininess;

varying vec2 TexCoords;

void main() {
	TexCoords = gl_MultiTexCoord0;
	
	pos = gl_ModelViewMatrix * gl_Vertex;
    normal = normalize(gl_NormalMatrix * gl_Normal);

	textCoord1 = gl_MultiTexCoord0;
	textCoord2 = gl_TextureMatrix[1] * gl_MultiTexCoord1;
	lightForShadowPos = testMat * gl_ModelViewMatrix * gl_Vertex;
	
	color = gl_Color;
	shininess = gl_FrontMaterial.shininess;
	mSpecular = gl_FrontMaterial.specular.xyz;
	
	gl_Position = gl_ProjectionMatrix * pos;
	//gl_Position = gl_Vertex;
}*/