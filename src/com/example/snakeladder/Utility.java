package com.example.snakeladder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Utility {
	public static void showDialog(final Activity activity, final String title, final String msg, final CallBack listener) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(activity);
				alert.setCancelable(false);
				alert.setTitle(title);
				alert.setMessage(msg);
				alert.setPositiveButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onCallBack();
					}
				});

				alert.show();
			}
		});
	}
}
