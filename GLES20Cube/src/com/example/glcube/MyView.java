package com.example.glcube;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

@SuppressLint("ClickableViewAccessibility")
public class MyView extends GLSurfaceView {

	MyRenderer mRenderer;
	private float mPreviousX;
	private float mPreviousY;
	private float mPreviousDeg;

	public MyView(Context context) {
		super(context);

		mRenderer = new MyRenderer(context);

		setEGLContextClientVersion(2);
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event != null) {
			System.out.println();
			if (event.getPointerCount() == 1) {
				float x = event.getX();
				float y = event.getY();

				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (mRenderer != null) {
						float deltaX = (x - mPreviousX) / this.getWidth() * 360;
						float deltaY = (y - mPreviousY) / this.getHeight() * 360;
						mRenderer.dx += deltaY;
						mRenderer.dy += deltaX;
					}
				}
				mPreviousX = x;
				mPreviousY = y;
			} else if (event.getPointerCount() == 2) {
				float dx = event.getX(1) - event.getX(0);
				float dy = event.getY(1) - event.getY(0);
				float deg = (float) Math.toDegrees(Math.atan2(dy, dx));
				if (event.getAction() != MotionEvent.ACTION_MOVE) {
					mPreviousDeg = deg;
					mPreviousX = event.getX();
					mPreviousY = event.getY();
					return true;
				}
				float ddeg = deg - mPreviousDeg;
				mRenderer.dz -= ddeg;
				mPreviousDeg = deg;
			}
			requestRender();
		}
		return true;
	}
	
	public void setCubeOrientation(float x, float y, float z){
		mRenderer.dx = x;
		mRenderer.dy = y;
		mRenderer.dz = z;
		requestRender();
	}
	
	public void rotateDice(float dx, float dy, float dz){
		mRenderer.dx += dx;
		mRenderer.dy += dy;
		mRenderer.dz += dz;
		requestRender();
	}

	public static int loadShader(String strSource, int iType) {
		int[] compiled = new int[1];
		int shader = GLES20.glCreateShader(iType);
		GLES20.glShaderSource(shader, strSource);
		GLES20.glCompileShader(shader);
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			return 0;
		}
		return shader;
	}

	public static int loadProgram(String strVSource, String strFSource) {
		int vertexShader;
		int fragmentShader;
		int programID;
		int[] link = new int[1];
		vertexShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
		if (vertexShader == 0) {
			Log.d("Load Program", "Vertex Shader Failed");
			return 0;
		}
		fragmentShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
		if (fragmentShader == 0) {
			Log.d("Load Program", "Fragment Shader Failed");
			return 0;
		}

		programID = GLES20.glCreateProgram();

		GLES20.glAttachShader(programID, vertexShader);
		GLES20.glAttachShader(programID, fragmentShader);

		GLES20.glLinkProgram(programID);

		GLES20.glGetProgramiv(programID, GLES20.GL_LINK_STATUS, link, 0);
		if (link[0] <= 0) {
			Log.d("Load Program", "Linking Failed");
			return 0;
		}
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);
		return programID;
	}
}