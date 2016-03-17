package nl.baboea.android.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import nl.baboea.android.tictactoe.math.Mat4f;

/**
 * Created by Laurens on 13-9-2015.
 */
public class MyGLSurfaceView extends GLSurfaceView {


    private boolean done;
    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Activity context){
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer(context);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

    }



    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Constants.HEIGHT = getHeight();
        Constants.WIDTH = getWidth();
        Constants.RATIO = (float)getWidth()/getHeight();
        Mat4f.loadUpProjectionMatrix();
    }

    public void onStart(){
        mRenderer.onStart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if(action==MotionEvent.ACTION_UP){
            Input.pressed = false;
            return false;
        }
        Input.lastX = event.getX();
        Input.lastY = event.getY();
        Input.pressed = true;
        return true;
    }
}
