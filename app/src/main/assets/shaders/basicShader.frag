precision mediump float;
varying vec2 texCoords;
uniform sampler2D texture;
uniform sampler2D texture2;

void main(){
    gl_FragColor = texture2D(texture, texCoords);
    if(gl_FragColor.a<=0.3)discard;
    gl_FragColor = texture2D(texture2, texCoords);
}