package nl.baboea.android.tictactoe;

import android.util.Log;

import nl.baboea.android.tictactoe.math.Vec3;

/**
 * Created by Laurens on 14-12-2015.
 */
public class Square extends AbstractShape {

    private Model model = new Model();


    public Square(float width, float height){
        super(Square.getVertices(width,height),new float[0],indices,ShaderProgram.noTexture,null);
        model = new Model();
    }

    private static final float[] vertices = new float[]{
            0f, -1f, 0,
            0f, 0f, 0,
            1f, 0f, 0,
            1f, -1f, 0
    };

    private static final short[] indices = new short[]{
            0, 1, 2,
            0, 2, 3
    };

    public void draw(){
        this.draw(model);
    }

    public Model getModel() {
        return model;
    }

    public void setPosition(Vec3 position){
        model.setPosition(position);
    }

    public static float[] getVertices(float width, float height) {
        float[] toReturn = new float[vertices.length];
        for(int i = 0 ; i < vertices.length ; i +=3){
            toReturn[i] = vertices[i]*width;
            toReturn[i+1] = vertices[i+1]*height;
            toReturn[i+2] = vertices[i+2];
        }
        return toReturn;
    }

}
