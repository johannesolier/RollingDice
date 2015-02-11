package com.example.glcube;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity{
	
	MyView diceView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diceView = new MyView(this);
        setContentView(diceView);
    }
}