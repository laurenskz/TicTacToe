attribute vec4 vertexPosition;
attribute vec2 textureCoordinate;
varying vec2 texCoords;
uniform mat4 mvpMatrix;

void main(){
    texCoords = textureCoordinate;
    gl_Position = mvpMatrix* vertexPosition;
}