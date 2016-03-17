package nl.baboea.android.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;

import javax.microedition.khronos.opengles.GL10;

import nl.baboea.android.tictactoe.game.Game;

/**
 * Created by Laurens on 13-9-2015.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static final String TAG = "MyGLRenderer";
    private float zRot;
    private Game game;
    private TexturedSquare b;
    private long time;
    private Activity context;

    public MyGLRenderer(Activity context){
        this.context = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//Make a white background
        new Text("Loading the game").draw();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
        time = System.currentTimeMillis();
        game = new Game(context);
    }

    public void onDrawFrame(GL10 unused) {
        long newTime = System.currentTimeMillis();
        long milliSecondsPassed = newTime - time;
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        if (game != null) {
            game.update(milliSecondsPassed);
            game.draw();
        }

        time = newTime;
    }


    public void onStart(){
        //Log.d(TAG, "onStart of renderer called");
        time = System.currentTimeMillis();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
}
