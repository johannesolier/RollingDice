package com.example.glcube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.gles20cube.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class MyRenderer implements Renderer {

	volatile public float dx = 45, dy = 45, dz;

	int mProgram;
	int position;
	int vPMat;
	int textureID;
	int textureLocation;
	int textureCoordinations;

	Context context;

	float[] projMatrix = new float[16];
	float[] viewMatrix = new float[16];
	float[] viewProjMatrix = new float[16];

	private float[] rotation = new float[16];

	float[] cube = {
			// FRONT
			1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1,

			// RIGHT
			1, 1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1,

			// BACK
			1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1,

			// LEFT
			-1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1,

			// TOP
			1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1,

			// BOTTOM
			1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1, };

	short[] indeces = { 0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 13, 14, 12, 14, 15, 16, 17, 18, 16, 18, 19, 20, 21, 22, 20, 22, 23 };

	float[] texture = {
			// FRONT
			1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1,

			// RIGHT
			1, 1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1,

			// BACK
			1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1,

			// LEFT
			-1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1,

			// TOP
			1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1,

			// BOTTOM
			1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1 };

	final String vertexShaderCode = "attribute vec4 a_position;" + "attribute vec4 a_color;" + "attribute vec3 a_normal;" + "uniform mat4 u_VPMatrix;" + "uniform vec3 u_LightPos;" + "varying vec3 v_texCoords;" + "attribute vec3 a_texCoords;" + "void main()" + "{" + "v_texCoords = a_texCoords;" + "gl_Position = u_VPMatrix * a_position;" + "}";

	final String fragmentShaderCode = "precision mediump float;" + "uniform samplerCube u_texId;" + "varying vec3 v_texCoords;" + "void main()" + "{" + "gl_FragColor = textureCube(u_texId, v_texCoords);" + "}";

	FloatBuffer bytebuffer = null;
	ShortBuffer indexBuffer = null;
	FloatBuffer textureBuffer = null;

	public MyRenderer(Context context) {
		this.context = context;

		bytebuffer = ByteBuffer.allocateDirect(cube.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		bytebuffer.put(cube).position(0);

		indexBuffer = ByteBuffer.allocateDirect(indeces.length * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
		indexBuffer.put(indeces).position(0);

		textureBuffer = ByteBuffer.allocateDirect(texture.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		textureBuffer.put(texture).position(0);
	}

	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);

		bytebuffer.position(0);
		GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 0, bytebuffer);
		GLES20.glEnableVertexAttribArray(position);

		textureBuffer.position(0);
		GLES20.glVertexAttribPointer(textureCoordinations, 3, GLES20.GL_FLOAT, false, 0, textureBuffer);
		GLES20.glEnableVertexAttribArray(textureCoordinations);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		GLES20.glUniform1i(textureLocation, 0);

		Matrix.rotateM(rotation, 0, dx, 1.0f, 0.0f, 0.0f);
		Matrix.rotateM(rotation, 0, dy, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(rotation, 0, dz, 0.0f, 0.0f, 1.0f);

		dx = 0.0f;
		dy = 0.0f;
		dz = 0.0f;

		Matrix.multiplyMM(viewProjMatrix, 0, viewMatrix, 0, rotation, 0);
		Matrix.multiplyMM(viewProjMatrix, 0, projMatrix, 0, viewProjMatrix, 0);

		GLES20.glUniformMatrix4fv(vPMat, 1, false, viewProjMatrix, 0);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
	}

	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		Matrix.frustumM(projMatrix, 0, -(float) width / height, (float) width / height, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		GLES20.glClearColor(0, 0, 0, 0);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glFrontFace(GLES20.GL_CCW);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 6, 0, 0, 0, 0, 1, 0);
		Matrix.setIdentityM(rotation, 0);

		mProgram = MyView.loadProgram(vertexShaderCode, fragmentShaderCode);
		position = GLES20.glGetAttribLocation(mProgram, "a_position");
		vPMat = GLES20.glGetUniformLocation(mProgram, "u_VPMatrix");
		textureLocation = GLES20.glGetUniformLocation(mProgram, "u_texId");
		textureCoordinations = GLES20.glGetAttribLocation(mProgram, "a_texCoords");
		textureID = CreateCubeTexture();
	}

	public int CreateCubeTexture() {
		int[] textureId = new int[1];

		ByteBuffer cubePixels = null;

		GLES20.glGenTextures(1, textureId, 0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId[0]);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		Bitmap one = BitmapFactory.decodeResource(context.getResources(), R.drawable.one);
		Bitmap two = BitmapFactory.decodeResource(context.getResources(), R.drawable.two);
		Bitmap three = BitmapFactory.decodeResource(context.getResources(), R.drawable.three);
		Bitmap four = BitmapFactory.decodeResource(context.getResources(), R.drawable.four);
		Bitmap five = BitmapFactory.decodeResource(context.getResources(), R.drawable.five);
		Bitmap six = BitmapFactory.decodeResource(context.getResources(), R.drawable.six);

		cubePixels = ByteBuffer.allocateDirect(one.getHeight() * one.getWidth() * 4);

		one.copyPixelsToBuffer(cubePixels);
		cubePixels.position(0);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLES20.GL_RGBA, one.getWidth(), one.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, cubePixels);
		one.recycle();

		two.copyPixelsToBuffer(cubePixels);
		cubePixels.position(0);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLES20.GL_RGBA, two.getWidth(), two.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, cubePixels);
		two.recycle();

		three.copyPixelsToBuffer(cubePixels);
		cubePixels.position(0);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLES20.GL_RGBA, three.getWidth(), three.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, cubePixels);
		three.recycle();

		four.copyPixelsToBuffer(cubePixels);
		cubePixels.position(0);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLES20.GL_RGBA, four.getWidth(), four.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, cubePixels);
		four.recycle();

		five.copyPixelsToBuffer(cubePixels);
		cubePixels.position(0);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLES20.GL_RGBA, five.getWidth(), five.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, cubePixels);
		five.recycle();

		six.copyPixelsToBuffer(cubePixels);
		cubePixels.position(0);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLES20.GL_RGBA, six.getWidth(), six.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, cubePixels);
		six.recycle();

		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_CUBE_MAP);

		return textureId[0];
	}
}