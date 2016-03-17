package nl.baboea.android.tictactoe.math;

import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import nl.baboea.android.tictactoe.Constants;

import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.tan;

/**
 * Created by Laurens on 14-9-2015.
 */
public class Mat4f {

    public static final float RAD = 0.01745329251f;
    public static final int ZNEAR = 1;
    public static final int ZFAR = 1000;
    public static final float NEAR_Z = 1;
    public static final float FAR_Z = 1000;
    private static Mat4f projectionMatrix = null;
    float[] data = new float[16];

    public static Mat4f identity(){
        Mat4f toReturn = new Mat4f();
        toReturn.data[0] = 1;
        toReturn.data[5] = 1;
        toReturn.data[10] = 1;
        toReturn.data[15] = 1;
        return toReturn;
    }

    public static Mat4f multiplyMat4Mat4(Mat4f matrix1,Mat4f matrix2){
        Mat4f newMat = new Mat4f();
        int row = 0;
        for(;row<16;row+=4){
            int column = 0;
            for(;column<4;column++){
                int counter = 0;
                float sum = 0;
                for(;counter<4;counter++){
                    float product = matrix1.data[row+counter] * matrix2.data[column + counter*4];
                    sum+=product;
                }
                newMat.data[row+column] = sum;
            }
        }
        return newMat;
    }

    public void multiply(Mat4f other){
        Mat4f together = multiplyMat4Mat4(this,other);
        this.data = null; //Can we free this?
        this.data = together.data;
        together = null;
    }

    public void rotate(float x,float y,float z){
        this.multiply(rotatation(x, y, z));
    }

    public void scale(float x,float y,float z){
        this.multiply(scaleMatrix(x, y, z));
    }

    public void translate(float x,float y,float z){
        this.multiply(translation(x, y, z));
    }

    public void rotate(Vec3 v){
        if(v==null)return;
        rotate(v.getX(), v.getY(), v.getZ());
    }

    public void scale(Vec3 v){
        if(v==null)return;
        scale(v.getX(), v.getY(), v.getZ());
    }

    public void translate(Vec3 v){
        if(v==null)return;
        translate(v.getX(), v.getY(), v.getZ());
    }

    public static Mat4f scaleMatrix(Vec3 v){
        if(v==null)return identity();
        return scaleMatrix(v.getX(),v.getY(),v.getZ());
    }

    public static Mat4f translation(Vec3 v){
        if(v==null)return identity();
        return translation(v.getX(), v.getY(), v.getZ());
    }

    public static Mat4f rotatation(Vec3 v){
        if(v==null)return identity();
        return rotatation(v.getX(), v.getY(), v.getZ());
    }

    public static Mat4f scaleMatrix(float x, float y, float z){
        Mat4f translationMatrix = new Mat4f();
        translationMatrix.data[0] = x;
        translationMatrix.data[5] = y;
        translationMatrix.data[10] = z;
        translationMatrix.data[15] = 1;
        return translationMatrix;
    }

    public static Mat4f translation(float x, float y, float z){
        Mat4f translationMatrix = identity();
        translationMatrix.data[12] = x;
        translationMatrix.data[13] = y;
        translationMatrix.data[14] = z;
        return translationMatrix;
    }

    public static Mat4f rotatation(float x,float y,float z){
        Mat4f  xRot = new Mat4f();
        Mat4f yRot = new Mat4f();
        Mat4f zRot = new Mat4f();
        xRot.data[0] = 1;
        xRot.data[5] = (float) cos((x * RAD));
        xRot.data[6] = (float) -sin((x*RAD));
        xRot.data[9] = (float)sin((x * RAD));
        xRot.data[10] = (float)cos((x * RAD));
        xRot.data[15] = 1;
        yRot.data[0] = (float)cos((y * RAD));
        yRot.data[2] = (float)sin((y * RAD));
        yRot.data[5] = 1;
        yRot.data[8] = (float)-sin((y*RAD));
        yRot.data[10] = (float)cos((y*RAD));
        yRot.data[15] = 1;
        zRot.data[0] = (float)cos((z*RAD));
        zRot.data[1] = (float)-sin((z*RAD));
        zRot.data[4] = (float)sin((z * RAD));
        zRot.data[5] = (float)cos((z * RAD));
        zRot.data[10] = 1;
        zRot.data[15] = 1;
        Mat4f xTimesY = multiplyMat4Mat4(xRot,yRot);
        return  multiplyMat4Mat4(xTimesY,zRot);
    }

    public FloatBuffer toFloatBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);//Each float consists of 4 bytes
        bb.order(ByteOrder.nativeOrder()); //Set the endianness right
        FloatBuffer fBuffer = bb.asFloatBuffer(); //Convert the buffer to a float buffer
        fBuffer.put(data); //Put the array in the float buffer
        fBuffer.position(0); // Reset the position
        return fBuffer;//Return the float buffer we put the result in
    }

    public static Mat4f getProjectionMatrix(){
        if(projectionMatrix==null)loadUpProjectionMatrix();
        return projectionMatrix;
    }

    public static void loadUpProjectionMatrix(){
        float x = Constants.WIDTH;
        float y = Constants.HEIGHT;
        projectionMatrix = identity();
        projectionMatrix.data[5] = x/y;//This way the shape remains its intended form and doesn't get stretched when the screen is not square
//        projectionMatrix.data[10] = (-(NEAR_Z+FAR_Z))/(FAR_Z-NEAR_Z);
//        projectionMatrix.data[11] = -(2*FAR_Z*NEAR_Z)/(FAR_Z-NEAR_Z);
//        projectionMatrix.data[14] = -1;
    }

}
