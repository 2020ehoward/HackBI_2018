package com.example.evanphoward.hackbi;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Evan Howard and Nishanth Alladi on 1/20/18.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {


    private int mWidth;
    private int mHeight;
    private static String TAG = "myRenderer";
    public ArrayList<Cube> cubes;
    private final float DEPTH = 1.5f;
    private float mAngle =0;
    private float mTransY=0;
    private float mTransX=0;
    private float mTransZ=0;
    private float rotx=0.4f;
    private float roty=1.0f;
    private float rotz=0.6f;
    private int rotF;
    private float length,height;
    private static final float Z_NEAR = 1f;
    private static final float Z_FAR = 160f;

    float[][] colors = {
    myColor.cyan(),
    myColor.blue(),
    myColor.red(),
    myColor.gray(),
    myColor.green(),
    myColor.yellow(),
};

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    //
    Context context;
    public MyGLRenderer(Context context) {
        //cube can not be instianated here, because of "no egl context"  no clue.
        //do it in onSurfaceCreate and it is fine.  odd, but workable solution.
        this.context = context;
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
        setRot(0);
        Scanner infile = null;
        try {
            infile = new Scanner(context.getAssets().open("blocks"));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        GLES30.glClearColor(0.65f, 0.19f, 0.19f, 0.9f);
        //initialize the cube code for drawing.
        this.length=17.55f;
        this.height=16.0f;
        cubes = new ArrayList<>();
        cubes.add(new Cube(0f,0f,0f,colors[3],0f,0f,0f,-1));
        cubes.add(new Cube((length/2)+0.1f,DEPTH,0.1f,colors[3],length/2,0f,0f,0));
        cubes.add(new Cube(0.1f,DEPTH,height/2,colors[3],length,height/2,0f,1));
        cubes.add(new Cube((length/2)+0.1f,DEPTH,0.1f,colors[3],length/2,height,0f,2));
        cubes.add(new Cube(0.1f,DEPTH,height/2,colors[3],0f,height/2,0f,3));
        cubes.add(new Cube((length/2)+0.1f,0.1f,(height/2)+0.1f,myColor.cyan(),length/2,height/2,DEPTH,0));
        cubes.add(new Cube(0.3f,DEPTH,0.3f,myColor.green(),length-0.8f,height/2,0.3f,0));


        infile.nextLine();
        while(infile.hasNext()) {
            String[] vals = infile.nextLine().split(", ");
            cubes.add(new Cube((Float.parseFloat(vals[0])/2.0f),DEPTH, Float.parseFloat(vals[2])/2.0f,colors[3],Float.parseFloat(vals[4]), Float.parseFloat(vals[5]),0f,Integer.parseInt(vals[7])));
        }
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

        for(int i=0;i<cubes.size();i++) {
            // Set the camera position (View matrix)  note Matrix is an include, not a declared method.
            Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Create a rotation and translation for the cube
            Matrix.setIdentityM(mRotationMatrix, 0);

            //move the cube up/down and left/right
                Matrix.translateM(mRotationMatrix, 0, mTransX-cubes.get(i).getX()+0.7f, mTransY+cubes.get(i).getY()-1.2f, mTransZ);

            //mangle is how fast, x,y,z which directions it rotates.
            Matrix.rotateM(mRotationMatrix, 0, mAngle, rotx, roty, rotz);

            // combine the model with the view matrix
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);

            // combine the model-view with the projection matrix
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

            cubes.get(i).draw(mMVPMatrix);
        }


        //change the angle, so the cube will spin.
        if(rotF!=0)
            mAngle+=(1.6*rotF);
    }

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

    public float getZ() {
        return mTransZ;
    }

    public void setZ(float mTransZ) {
        this.mTransZ = mTransZ;
    }

    public float getLength() {
        return length;
    }

    public float getHeight() {
        return height;
    }

    public void setRot(int i) {
        switch(i) {
            case 0:
                    rotF=0;
                break;
            case 1: roty=-1;
                rotz=rotx=0;
                rotF=-1;
                break;
            case 2: rotx=1;
                    roty=rotz=0;
                    rotF=1;
                break;
            case 3: roty=-1;
                    rotz=rotx=0;
                    rotF=1;
                break;
            case 4:
                rotx=1;
                roty=rotz=0;
                rotF=-1;
                break;
        }
    }
}
