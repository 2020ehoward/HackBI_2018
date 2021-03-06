package com.example.evanphoward.hackbi; /**
 * Created by Evan Howard and Nishanth Alladi on 1/20/18 */


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;
import android.util.Log;

public class Cube {
    private int mProgramObject;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private boolean isFloor;
    private FloatBuffer mVertices;

    //initial sizes of the prism.  set here, so it is easier to change later.
    float length;
    float width;
    float height;
    float x,y,z;
    int shade;
    //this is the initial data, which will need to translated into the mVertices variable in the consturctor.
    private float[] mVerticesData;
    private void vertData() {
        mVerticesData = new float[]{
                ////////////////////////////////////////////////////////////////////
                // top
                ////////////////////////////////////////////////////////////////////
                // FRONT-LOWER
                length, height, width, // top
                -length, -height, width, // front-left
                length, -height, width, // front-right
                // FRONT-UPPER
                -length, height, width, // top
                -length, -height, width, // front-left
                length, height, width, // front-right
                // RIGHT-LOWER
                length, height, width, // top
                length, -height, width, // front-right
                length, -height, -width, // back-right
                // RIGHT-UPPER
                length, height, -width, // top
                length, height, width, // front-right
                length, -height, -width, // back-right
                // BACK-LOWER
                length, height, -width, // top
                length, -height, -width, // back-right
                -length, -height, -width, // back-left
                // BACK-UPPER
                -length, height, -width, // top
                length, height, -width, // back-right
                -length, -height, -width, // back-left
                // LEFT-LOWER
                -length, height, -width, // top
                -length, -height, -width, // back-left
                -length, -height, width, // front-left
                // LEFT-UPPER
                -length, height, width, // top
                -length, height, -width, // back-left
                -length, -height, width, // front-left
                // TOP-LOWER
                length, height, -width, // top
                -length, height, -width, // back-left
                -length, height, width, // front-left
                // TOP-UPPER
                length, height, -width, // top
                length, height, width, // back-left
                -length, height, width, // front-left

                ////////////////////////////////////////////////////////////////////
                // BOTTOM
                ////////////////////////////////////////////////////////////////////
                // Triangle 1
                -length, -height, -width, // back-left
                -length, -height, width, // front-left
                length, -height, width, // front-right
                // Triangle 2
                length, -height, width, // front-right
                length, -height, -width, // back-right
                -length, -height, -width // back-left
        };
    }

    float[] color;

    //vertex shader code
    String vShaderStr =
            "#version 300 es 			  \n"
                    + "uniform mat4 uMVPMatrix;     \n"
                    + "in vec4 vPosition;           \n"
                    + "void main()                  \n"
                    + "{                            \n"
                    + "   gl_Position = uMVPMatrix * vPosition;  \n"
                    + "}                            \n";
    //fragment shader code.
    String fShaderStr =
            "#version 300 es		 			          	\n"
                    + "precision mediump float;					  	\n"
                    + "uniform vec4 vColor;	 			 		  	\n"
                    + "out vec4 fragColor;	 			 		  	\n"
                    + "void main()                                  \n"
                    + "{                                            \n"
                    + "  fragColor = vColor;                    	\n"
                    + "}                                            \n";

    String TAG = "Cube";

    //finally some methods
    //constructor
    public Cube(float length, float width, float height, float[] color, float x, float y, float z, int shade) {
        if(length>15 && width>15)
            isFloor=true;
        this.length=length;
        this.width=width;
        this.height=height;
        vertData();
        this.color=color;
        this.x=x;
        this.y=y;
        this.z=z;
        this.shade=shade;
        //first setup the mVertices correctly.
        mVertices = ByteBuffer
                .allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVerticesData);
        mVertices.position(0);

        //setup the shaders
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = MyGLRenderer.LoadShader(GLES30.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = MyGLRenderer.LoadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr);

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            Log.e(TAG, "So some kind of error, but what?");
            return;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // Bind vPosition to attribute 0
        GLES30.glBindAttribLocation(programObject, 0, "vPosition");

        // Link the program
        GLES30.glLinkProgram(programObject);

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return;
        }

        // Store the program object
        mProgramObject = programObject;

        //now everything is setup and ready to draw.
    }


    public void draw(float[] mvpMatrix) {

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgramObject, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgramObject, "vColor");


        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        int VERTEX_POS_INDX = 0;
        mVertices.position(VERTEX_POS_INDX);  //just in case.  We did it already though.

        //add all the points to the space, so they can be correct by the transformations.
        //would need to do this even if there were no transformations actually.
        GLES30.glVertexAttribPointer(VERTEX_POS_INDX, 3, GLES30.GL_FLOAT,
                false, 0, mVertices);
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);


        //Now we are ready to draw the cube finally.
        int startPos = 0;
        int verticesPerface = 3;
        float[] d = {color[0]*0.8f,color[1]*0.8f,color[2]*0.8f,1.0f};
        //draw front face
        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        //draw right face
        if(shade!=1) {
            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;


            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;
        }
        else {
            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;


            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;
        }

        //draw back face
        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        //draw left face
        if(shade!=3) {
            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;

            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;
        }
        else {
            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;

            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;
        }

        //draw bottom face 1 tri square, so 6 faces.
        if(shade!=0) {
            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;

            //draw bottom face 2 tri square, so 6 faces.
            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;
        }
        else {
            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;

            //draw bottom face 2 tri square, so 6 faces.
            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;
        }

        if(shade!=2) {
            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;

            GLES30.glUniform4fv(mColorHandle, 1, color, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            //last face, so no need to increment.
        }
        else {
            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
            startPos += verticesPerface;

            GLES30.glUniform4fv(mColorHandle, 1, d, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        }

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

}