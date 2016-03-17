package nl.baboea.android.tictactoe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by Laurens on 15-9-2015.
 */
public class Texture {


    private int id;
    private int width;
    private int height;
    private static final int DENOMINATOR = 1024;
    private CollisionMesh collisionMesh;

    private static final float[] vertices = new float[]{
            -1f, 1f, 0,
            -1f, -1f, 0,
            1f, -1f, 0,
            1f, 1f, 0
    };

    protected float[] getVertices() {
        float width = (float)this.width / DENOMINATOR;
        float height = (float)this.height / DENOMINATOR;
        //Log.d("Texture","Width = " + width + "; Height = " + height);
        float[] toReturn = new float[vertices.length];
        for(int i = 0 ; i < vertices.length ; i +=3){
            toReturn[i] = vertices[i]*width;
            toReturn[i+1] = vertices[i+1]*height;
            toReturn[i+2] = vertices[i+2];
        }
        return toReturn;
    }

    public Texture(int id) {
        this(id,-1);
    }

    public Texture(int id, int collisionMapId){
        try {
            this.id = loadTexture(MainActivity.resources.openRawResource(id),collisionMapId);
        } catch (Exception e) {
            //Log.d("Texture", e.toString());
        }
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private int loadTexture ( InputStream is , int collisionMapId)
    {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeStream(is);
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        ByteBuffer byteBuffer = setCorrectColorFormat(bitmap);

        int[] textureId = loadToOpenGL(bitmap, byteBuffer);

        this.collisionMesh = collisionMapId==-1?new CollisionMesh(bitmap):new CollisionMesh(collisionMapId);
        bitmap.recycle();
        return textureId[0];
    }

    private ByteBuffer setCorrectColorFormat(Bitmap bitmap) {
        byte[] buffer = new byte[bitmap.getWidth() * bitmap.getHeight() * 4];
        for ( int y = 0; y < bitmap.getHeight(); y++ )
            for ( int x = 0; x < bitmap.getWidth(); x++ )
            {
                int pixel = bitmap.getPixel(x, y);
                buffer[(y * bitmap.getWidth() + x) * 4 + 0] = (byte)((pixel >> 16) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 4 + 1] = (byte)((pixel >> 8) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 4 + 2] = (byte)((pixel >> 0) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 4 + 3] = (byte)((pixel >> 24) & 0xFF);
            }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        byteBuffer.put(buffer).position(0);
        return byteBuffer;
    }

    private int[] loadToOpenGL(Bitmap bitmap, ByteBuffer byteBuffer) {
        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );
        return textureId;
    }

    public CollisionMesh getCollisionMesh() {
        return collisionMesh;
    }
}
