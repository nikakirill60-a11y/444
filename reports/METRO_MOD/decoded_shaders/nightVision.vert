//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//NightVision shader
//Krogenit 

varying vec2 TexCoords1;

void main()
{
	TexCoords1 = gl_MultiTexCoord0.xy;
	gl_Position = gl_Vertex;
}