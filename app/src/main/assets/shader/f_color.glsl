#version 300 es
precision mediump float;

in vec4 color2;
out vec4 color;

void main()
{
    color = color2;
}