package nl.baboea.android.tictactoe;

import android.opengl.GLES20;
import android.util.Log;

import java.util.HashMap;

import nl.baboea.android.tictactoe.math.Mat4f;

/**
 * Created by Laurens on 13-9-2015.
 */
public class ShaderProgram {

    public static final String SHADER_PROGRAM = "ShaderProgram";
    private int vertexID;
    private int fragmentID;
    private int programID;
    private HashMap<String,Integer> uniformLocations = new HashMap<>();
    public static final ShaderProgram multiTextured = new ShaderProgram("shaders/shader.vert", "shaders/basicShader.frag");
    public static final ShaderProgram basicShader = new ShaderProgram("shaders/shader.vert", "shaders/shader.frag");
    public static final ShaderProgram noTexture = new ShaderProgram("shaders/basicShader.vert", "shaders/colorVertex.frag");

    //TODO optimize this shader by caching shader paths with the opengl id
    public ShaderProgram(String vertexPath, String fragmentPath) {
        programID = GLES20.glCreateProgram();
        vertexID = loadShader(GLES20.GL_VERTEX_SHADER, Utils.getStringFromFile(vertexPath),vertexPath);
        fragmentID = loadShader(GLES20.GL_FRAGMENT_SHADER, Utils.getStringFromFile(fragmentPath),fragmentPath);
        GLES20.glAttachShader(programID, vertexID);
        GLES20.glAttachShader(programID, fragmentID);
        GLES20.glLinkProgram(programID);
    }


    public static int loadShader(int type, String shaderCode, String toCompile) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        //Log.d("ShaderProgram",toCompile + ": " + GLES20.glGetShaderInfoLog(shader));
        return shader;
    }

    public void load(String place, Mat4f matrix){
        GLES20.glUseProgram(programID);
        int location = getUniformLocation(place);
        if(location == -1 )return;//There is no such uniform field in the shader.
        GLES20.glUniformMatrix4fv(location, 1, false, matrix.toFloatBuffer());
        GLES20.glUseProgram(0);
    }

    public int getUniformLocation(String place){
        Integer toRet = uniformLocations.get(place);
        if(toRet!=null)return toRet;
        int value = GLES20.glGetUniformLocation(programID,place);
        uniformLocations.put(place,value);
        return value;
    }

    public int getProgramID() {
        return programID;
    }

    public void use() {
        GLES20.glUseProgram(getProgramID());
    }
}
