#version 300 es
precision mediump float;

in vec2 tex_coord2;
out vec4 color;

// 代表一个2D纹理
uniform sampler2D tex2d;

void main()
{
    color = texture(tex2d, tex_coord2);
}