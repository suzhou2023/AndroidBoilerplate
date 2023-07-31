attribute vec4 a_Position;
attribute vec2 a_TexCoord;
varying vec2 v_TexCoord;
uniform mat4 matrix;
void main()
{
    gl_Position = matrix * a_Position;
    v_TexCoord = a_TexCoord;
    //v_TexCoord = vec2(a_TexCoord.x, 1.0 - a_TexCoord.y);
}


#version 300 es
precision mediump float;
in vec2 fTexCoord;
out vec4 color;
uniform sampler2D layer;
void main()
{
    vec4 rgb = texture(layer, fTexCoord);
    float gray = rgb.r * 0.2125 + rgb.g * 0.7154 + rgb.b * 0.0721;
    color = vec4(gray, gray, gray, 1.0);
}