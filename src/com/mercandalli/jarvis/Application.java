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
import android.widget.EditText;

import com.mercandalli.jarvis.config.Config;
import com.mercandalli.jarvis.listener.IListener;
import com.mercandalli.jarvis.listener.IStringListener;

public abstract class Application extends Activity {
	
	private Config config;
	public Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		config = new Config(this);
	}
	
	public Config getConfig() {
		if(config == null)
			config = new Config(this);
		return config;
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
	
	public void prompt(String title, String message, String positive, final IStringListener positiveListener, String negative, final IListener negativeListener) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(title);
		alert.setMessage(message);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(positive, 
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					positiveListener.execute(input.getText().toString());
				}
			}
		);

		alert.setNegativeButton(negative,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			}
		);

		alert.show();
	}
	
	public abstract void refreshAdapters();
	public abstract void updateAdapters();
}
