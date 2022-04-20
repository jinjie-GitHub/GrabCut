/**
 * 
 */
package com.ltzk.mbsf.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.ltzk.mbsf.MainApplication;

public class ToastUtil {

	/**
	 * 自定义Toast
	 * @param context
	 * @param message
	 */
	public static void showToast(Context context, String message){
		Toast toast = Toast.makeText(MainApplication.getInstance(), message,0);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	/**
	 * 自定义Toast
	 * @param context
	 * @param resId
	 */
	public static void showToast(Context context, int resId){
		Toast toast = Toast.makeText(MainApplication.getInstance(), context.getResources().getString(resId),0);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	/**
	 * 自定义Toast
	 * @param activity
	 * @param message
	 */
	public static void showToast(Activity activity, String message){
		showToast(activity,message,1);
	}
	/**
	 * 自定义Toast
	 * @param activity
	 * @param message
	 * @param time
	 */
	public static void showToast(Activity activity, String message, int time){
		showToast(activity,message,time, Gravity.CENTER,0,0);
	}
	/**
	 * 自定义Toast
	 * @param activity
	 * @param message
	 * @param time
	 * @param gravity
	 * @param xOffset
	 * @param yOffset
	 */
	public static void showToast(Activity activity, String message, int time, int gravity, int xOffset, int yOffset){
		Toast toast = Toast.makeText(MainApplication.getInstance(), message, 0);
		toast.setGravity(gravity, xOffset, yOffset);
		toast.show();
	}
}
