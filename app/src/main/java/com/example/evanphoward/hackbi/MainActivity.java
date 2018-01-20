package com.example.evanphoward.hackbi;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
//
//    private GLSurfaceView mGLView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mGLView = new MyGLSurfaceView(this);
//        setContentView(mGLView);
//    }
//
//    private class MyGLSurfaceView extends GLSurfaceView {
//        private final MyGLRenderer mRenderer;
//
//        public MyGLSurfaceView(Context context) {
//            super(context);
//
//            // Create an OpenGL ES 2.0 context.
//            setEGLContextClientVersion(2);
//
//            // Set the Renderer for drawing on the GLSurfaceView
//            mRenderer = new MyGLRenderer(getApplicationContext());
//            setRenderer(mRenderer);
//
//            // Render the view only when there is a change in the drawing data
//            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        }
//
//        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
//        private float mPreviousX;
//        private float mPreviousY;
//
//        @Override
//        public boolean onTouchEvent(MotionEvent e) {
//            // MotionEvent reports input details from the touch screen
//            // and other input controls. In this case, you are only
//            // interested in events where the touch position changed.
//
//            float x = e.getX();
//            float y = e.getY();
//
//            switch (e.getAction()) {
//                case MotionEvent.ACTION_MOVE:
//
//                    float dx = x - mPreviousX;
//                    float dy = y - mPreviousY;
//
//                    // reverse direction of rotation above the mid-line
//                    if (y > getHeight() / 2) {
//                        dx = dx * -1 ;
//                    }
//
//                    // reverse direction of rotation to left of the mid-line
//                    if (x < getWidth() / 2) {
//                        dy = dy * -1 ;
//                    }
//
//                    mRenderer.setAngle(
//                            mRenderer.getAngle() +
//                                    ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
//                    requestRender();
//            }
//
//            mPreviousX = x;
//            mPreviousY = y;
//            return true;
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (detectOpenGLES30()) {
            //so we know it a opengl 3.0 and use our extended GLsurfaceview.
            setContentView(new myGLSurfaceView(this));
        } else {
            // This is where you could create an OpenGL ES 2.0 and/or 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...");
            finish();

        }

    }
    private boolean detectOpenGLES30() {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x30000);
    }
}
