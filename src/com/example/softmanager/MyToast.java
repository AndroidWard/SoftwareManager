package com.example.softwaremanager;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public  class MyToast {
	
	public static void myToastShow(Context context,int imageResId,String content,int duration) {
		Toast toast = new Toast(context);
		toast.setDuration(duration);
		toast.setGravity(Gravity.CENTER, 0, 25);
		LinearLayout toastLayout = new LinearLayout(context);
		toastLayout.setOrientation(LinearLayout.VERTICAL);
		toastLayout.setGravity(Gravity.CENTER_VERTICAL);
		ImageView imageView = new ImageView(context);
		imageView.setImageResource(imageResId);
		toastLayout.addView(imageView);
		TextView tv_content = new TextView(context);
		tv_content.setText(content);
		toastLayout.addView(tv_content);
		toast.setView(toastLayout);
		toast.show();
		
		
	}

}