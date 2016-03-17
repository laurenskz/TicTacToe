package nl.baboea.android.tictactoe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import nl.baboea.android.tictactoe.ShaderProgram;

/**
 * Created by Laurens on 22-12-2015.
 * This class is the root shape, it is the root of all shapes, it just needs the vertices which want to be drawn.
 * Furthermore it has the ability  to set a color.
 */
public class RootShape {

    /**
     * We need a shaderprogram to de the rendering, even for our most basic shape.
     */
    private ShaderProgram shaderProgram;
    /**
     * We need the vertices that can be drawn
     */
    private float[] vertices;
    /**
     * If we want to draw anything we are gonna need a color. this is a float array of RGBA.
     */
    private float[] colour;
    /**
     * The default color for our shapes. The shape will be drawn in this color if no other color is specified (Black)
     */
    private static final float[] DEFAULT_COLOUR = new float[] {0,0,0,1f};
    /**
     * The default shaderprogram for RootShape objects
     */
    private static final ShaderProgram DEFAULT_SHADER = ShaderProgram.basicShader;
    /**
     * The default name of vertex position in our shader.
     */
    private static final String DEFAULT_VERTEX_NAME = "vertexPosition";


    /**
     * Constructor for RootShape objects.
     * @param shaderProgram
     * @param vertices
     * @param colour
     */
    public RootShape(ShaderProgram shaderProgram, float[] vertices, float[] colour) {
        this.shaderProgram = shaderProgram;
        this.vertices = vertices;
        this.colour = colour;
    }

    public RootShape(float[] vertices){
        this(DEFAULT_SHADER,vertices,DEFAULT_COLOUR);
    }

    FloatBuffer loadFloatArray(float[] array){
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);//Each float consists of 4 bytes
        bb.order(ByteOrder.nativeOrder()); //Set the endianness right
        FloatBuffer fBuffer = bb.asFloatBuffer(); //Convert the buffer to a float buffer
        fBuffer.put(array); //Put the array in the float buffer
        fBuffer.position(0); // Reset the position
        return fBuffer;//Return the float buffer we put the result in
    }
}
