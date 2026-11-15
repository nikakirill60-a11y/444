//#version 330 core
//layout (location = 0) in vec3 aPos;
//in vec2 aTexCoords;

varying vec2 TexCoords;

void main()
{
    TexCoords = gl_MultiTexCoord0.xy;
    gl_Position = gl_ModelViewMatrix * gl_Vertex;
}