package com.lonuery.play;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

public class MainActivity extends Activity implements OnClickListener{
	
	MyApplication application;
	Button btn1,btn2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		application = (MyApplication)getApplication();
		
		
		btn1 = (Button)findViewById(R.id.button1);
		btn2 = (Button)findViewById(R.id.button2);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
	}
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button1){
			new Thread(new Test()).start();
		}
	}
	
	
	class Test implements Runnable{

		@Override
		public void run() {
			btn1.setText("·¢µ½¸¶");
		}
		
	}
}
