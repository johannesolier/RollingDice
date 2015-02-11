package com.example.glcube;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.gles20cube.R;

public class MainActivity extends Activity {

	MyView diceView;

	private SensorManager mSensorManager;
	private float mAccel;
	private float mAccelCurrent;
	private float mAccelLast;

	private Random random = new Random();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dice_layout);
		LinearLayout layout = (LinearLayout) findViewById(R.id.dice_layout);
		
		diceView = new MyView(this);
		
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				roll();
			}
		});
		
		layout.addView(diceView);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
	}

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter

			if (mAccel > 5) {
				roll();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	public void roll(){
		diceView.rotateDice(90 * random.nextInt(4), 90 * random.nextInt(4), 90 * random.nextInt(4));
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}

}