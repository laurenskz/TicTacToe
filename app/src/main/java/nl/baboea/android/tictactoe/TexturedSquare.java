package nl.baboea.android.tictactoe;

/**
 * Created by Laurens on 14-9-2015.
 */
public class TexturedSquare extends AbstractShape {


    public static final float[] textureCoordinates = new float[]{
            0,0,
            0,1,
            1,1,
            1,0
    };

    public static final short[] indices = new short[]{
            0, 1, 2,
            0, 2, 3
    };



    public TexturedSquare(Texture texture) {
        super(texture.getVertices(), textureCoordinates ,indices, ShaderProgram.basicShader,texture);
    }


}
