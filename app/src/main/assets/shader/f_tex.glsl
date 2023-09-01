#version 300 es
precision mediump float;

in vec2 texCoord2;
uniform sampler2D layer;

out vec4 color;

void main()
{
    color = texture(layer, texCoord2);
}