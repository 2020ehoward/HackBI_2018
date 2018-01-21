package com.example.evanphoward.hackbi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Evan Howard and Nishanth Alladi on 1/20/2018.
 */
public class myGLSurfaceView extends GLSurfaceView {

    MyGLRenderer myRender;

    public myGLSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3);

        super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        myRender = new MyGLRenderer(context);
        setRenderer(myRender);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private static final float TOUCH_SCALE_FACTOR = 0.0015f;
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousDist=-1;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(e.getPointerCount()==1) {
                    if(mPreviousDist!=-1)
                        mPreviousDist=-1;
                    else {
                        float dx = x - mPreviousX;
                        float dy = y - mPreviousY;
                        if((myRender.getX()>0 || dx<0)&&(myRender.getX()<(myRender.getLength()) || dx>0))
                            myRender.setX(myRender.getX() - (dx * TOUCH_SCALE_FACTOR * map(myRender)));
                        if((myRender.getY()<0 || dy>0)&&(myRender.getY()>(0-myRender.getHeight()) || dy<0))
                            myRender.setY(myRender.getY() - (dy * TOUCH_SCALE_FACTOR * map(myRender)));
                    }
                }
                else if(e.getPointerCount()>1) {
                    if(mPreviousDist==-1)
                        mPreviousDist=dist(e);
                    float dz = dist(e) - mPreviousDist;
                    if((myRender.getZ()>-0.5 || dz<0) && (myRender.getZ()<40 || dz>0))
                        myRender.setZ(myRender.getZ() - (dz * TOUCH_SCALE_FACTOR));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mPreviousDist=dist(e);
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    private static float dist(MotionEvent e) {
        return (float)(Math.sqrt(Math.pow(e.getX(0)-e.getX(1),2)+Math.pow(e.getY(0)-e.getY(1),2)));
    }

    private static float map(MyGLRenderer myRender) {
        return (myRender.getZ()+1.2f) * (4.5f) / (11.2f) + 0.5f;
    }

    public void rotate(int i) {
        myRender.setRot(i);
    }

}