precision mediump float;
varying vec2 texCoords;
uniform sampler2D texture;

void main(){
    vec4 color = texture2D(texture, texCoords);
    gl_FragColor = color;
    if(color.a<=0.3)discard;

}