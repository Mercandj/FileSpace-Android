/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mercandalli.jarvis.config.Config;
import com.mercandalli.jarvis.listener.IListener;

public class Application extends Activity {
	
	public Config config;
	public Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		config = new Config(this);
	}	
	
	public void alert(String title, String message, String positive, final IListener positiveListener, String negative, final IListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	if(positiveListener!=null)
		    		positiveListener.execute();
		        dialog.dismiss();
		    }
		});
		builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	if(negativeListener!=null)
		    		negativeListener.execute();
		        dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}

