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
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
                System.arraycopy(event.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);
            }
            else if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)) {
                System.arraycopy(event.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
            }
            updateOrientationAngles();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

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

        mSensorManager=(SensorManager)context.getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(mSensorListener);
    }

    public void updateOrientationAngles() {
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        float x = mOrientationAngles[0];
        float y = mOrientationAngles[1];
        float z = mOrientationAngles[2];

        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        myRender.setRotx(mOrientationAngles[0]);
        myRender.setRoty(mOrientationAngles[1]);
        myRender.setRotz(mOrientationAngles[2]);
        float dist = (float)(Math.sqrt(Math.pow(x-mOrientationAngles[0],2)+Math.pow(y-mOrientationAngles[1],2)+Math.pow(z-mOrientationAngles[2],2)));
        if(Math.abs(dist)>0.001f)
            myRender.setmAngle(myRender.getmAngle()+(dist*0.5f));
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
                        myRender.setX(myRender.getX() - (dx * TOUCH_SCALE_FACTOR * map(myRender)));
                        myRender.setY(myRender.getY() - (dy * TOUCH_SCALE_FACTOR * map(myRender)));
                    }
                }
                else if(e.getPointerCount()>1) {
                    if(mPreviousDist==-1)
                        mPreviousDist=dist(e);
                    float dz = dist(e) - mPreviousDist;
                    if(myRender.getZ()>-1.2 || dz<0)
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

}