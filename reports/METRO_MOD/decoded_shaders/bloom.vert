//Minecraft Metro 2033 Server (c) https://metro2033server.ru
//Bloom shader
//Krogenit 

varying vec2 textCoords;

void main()
{
	textCoords = gl_MultiTexCoord0.xy;
	gl_Position = gl_Vertex;
}