package com.example.evanphoward.hackbi;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private myGLSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (detectOpenGLES30()) {
            surfaceView = new myGLSurfaceView(this);
            setContentView(new FrameLayout(this));
            addContentView(surfaceView,  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            LayoutInflater li = LayoutInflater.from(this);
            final View myView = li.inflate(R.layout.activity_main,null);
            addContentView(myView,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            View.OnTouchListener touchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    switch(e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            switch(v.getId()) {
                                case R.id.right: surfaceView.rotate(1);
                                    break;
                                case R.id.left: surfaceView.rotate(3);
                                    break;
                                case R.id.forward: surfaceView.rotate(2);
                                    break;
                                case R.id.back: surfaceView.rotate(4);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            surfaceView.rotate(0);
                            break;
                    }
                    return true;
                }
            };
            findViewById(R.id.right).setOnTouchListener(touchListener);
            findViewById(R.id.left).setOnTouchListener(touchListener);
            findViewById(R.id.forward).setOnTouchListener(touchListener);
            findViewById(R.id.back).setOnTouchListener(touchListener);
        }
            else {
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
