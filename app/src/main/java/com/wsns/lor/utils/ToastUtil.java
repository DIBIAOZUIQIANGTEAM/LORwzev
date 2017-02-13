/**
 * 
 */
package com.wsns.lor.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}
	
	public static void showerror(Context context, int rCode){
		try {
	        switch (rCode) {
	        //服务错误码

	        }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
	}
}
