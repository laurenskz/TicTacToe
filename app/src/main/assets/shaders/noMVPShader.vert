attribute vec4 vertexPosition;
attribute vec2 textureCoordinate;
varying vec2 texCoords;

void main(){
    texCoords = textureCoordinate;
    gl_Position = vertexPosition;
}