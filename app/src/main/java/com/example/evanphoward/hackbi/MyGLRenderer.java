package com.example.evanphoward.hackbi;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by evanphoward on 1/20/18.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {

//    private static final String TAG = "MyGLRenderer";
//    private Triangle mTriangle;
//    //private Square   mSquare;
//
//    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
//    private final float[] mMVPMatrix = new float[16];
//    private final float[] mProjectionMatrix = new float[16];
//    private final float[] mViewMatrix = new float[16];
//    private final float[] mRotationMatrix = new float[16];
//
//    private float mAngle;
//
//    @Override
//    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
//
//        // Set the background frame color
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//
//        mTriangle = new Triangle();
//        //mSquare   = new Square();
//    }
//
//    @Override
//    public void onDrawFrame(GL10 unused) {
//        float[] scratch = new float[16];
//
//        // Draw background color
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//
//        // Set the camera position (View matrix)
//        Matrix.setLookAtM(mViewMatrix, 0, 1, 2, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//
//        // Calculate the projection and view transformation
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//
//        // Draw square
//       // mSquare.draw(mMVPMatrix);
//
//        // Create a rotation for the triangle
//
//        // Use the following code to generate constant rotation.
//        // Leave this code out when using TouchEvents.
//         //long time = SystemClock.uptimeMillis() % 4000L;
//         //float angle = 0.090f * ((int) time);
//
//        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
//
//        // Combine the rotation matrix with the projection and camera view
//        // Note that the mMVPMatrix factor *must be first* in order
//        // for the matrix multiplication product to be correct.
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
//
//        // Draw triangle
//        mTriangle.draw(scratch);
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 unused, int width, int height) {
//        // Adjust the viewport based on geometry changes,
//        // such as screen rotation
//        GLES20.glViewport(0, 0, width, height);
//
//        float ratio = (float) width / height;
//
//        // this projection matrix is applied to object coordinates
//        // in the onDrawFrame() method
//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//
//    }
//
//    /**
//     * Utility method for compiling a OpenGL shader.
//     *
//     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
//     * method to debug shader coding errors.</p>
//     *
//     * @param type - Vertex or fragment shader type.
//     * @param shaderCode - String containing the shader code.
//     * @return - Returns an id for the shader.
//     */
//    public static int loadShader(int type, String shaderCode){
//
//        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
//        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
//        int shader = GLES20.glCreateShader(type);
//
//        // add the source code to the shader and compile it
//        GLES20.glShaderSource(shader, shaderCode);
//        GLES20.glCompileShader(shader);
//
//        return shader;
//    }
//
//    /**
//     * Utility method for debugging OpenGL calls. Provide the name of the call
//     * just after making it:
//     *
//     * <pre>
//     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
//     *
//     * If the operation is not successful, the check throws an error.
//     *
//     * @param glOperation - Name of the OpenGL call to check.
//     */
//    public static void checkGlError(String glOperation) {
//        int error;
//        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
//            Log.e(TAG, glOperation + ": glError " + error);
//            throw new RuntimeException(glOperation + ": glError " + error);
//        }
//    }
//
//    /**
//     * Returns the rotation angle of the triangle shape (mTriangle).
//     *
//     * @return - A float representing the rotation angle.
//     */
//    public float getAngle() {
//        return mAngle;
//    }
//
//    /**
//     * Sets the rotation angle of the triangle shape (mTriangle).
//     */
//    public void setAngle(float angle) {
//        mAngle = angle;
//    }

    private int mWidth;
    private int mHeight;
    private static String TAG = "myRenderer";
    public Pyramid mPyramid;
    private float mAngle =0;
    private float mTransY=0;
    private float mTransX=0;
    private static final float Z_NEAR = 1f;
    private static final float Z_FAR = 40f;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    //
    public MyGLRenderer(Context context) {
        //cube can not be instianated here, because of "no egl context"  no clue.
        //do it in onSurfaceCreate and it is fine.  odd, but workable solution.
    }
    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    public static int LoadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, "Erorr!!!!");
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    ///
    // Initialize the shader and program object
    //
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        //set the clear buffer color to light gray.
        GLES30.glClearColor(0.9f, .9f, 0.9f, 0.9f);
        //initialize the cube code for drawing.
        mPyramid = new Pyramid();
        //if we had other objects setup them up here as well.
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);
        float aspect = (float) width / height;

        // this projection matrix is applied to object coordinates
        //no idea why 53.13f, it was used in another example and it worked.
        Matrix.perspectiveM(mProjectionMatrix, 0, 53.13f, aspect, Z_NEAR, Z_FAR);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        // Set the camera position (View matrix)  note Matrix is an include, not a declared method.
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Create a rotation and translation for the cube
        Matrix.setIdentityM(mRotationMatrix, 0);

        //move the cube up/down and left/right
        Matrix.translateM(mRotationMatrix, 0, mTransX, mTransY, 0);

        //mangle is how fast, x,y,z which directions it rotates.
        Matrix.rotateM(mRotationMatrix, 0, mAngle, 0.4f, 1.0f, 0.6f);

        // combine the model with the view matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);

        // combine the model-view with the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        mPyramid.draw(mMVPMatrix);

        //change the angle, so the cube will spin.
        mAngle+=.4;
    }


    //used the touch listener to move the cube up/down (y) and left/right (x)
    public float getY() {
        return mTransY;
    }

    public void setY(float mY) {
        mTransY = mY;
    }

    public float getX() {
        return mTransX;
    }

    public void setX(float mX) {
        mTransX = mX;
    }
}
