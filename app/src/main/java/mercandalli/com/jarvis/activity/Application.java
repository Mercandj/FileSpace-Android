/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import mercandalli.com.jarvis.config.Config;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.listener.IStringListener;

public abstract class Application extends ActionBarActivity {
	
	private Config config;
	public Dialog dialog;
    public Toolbar toolbar;
	
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
        prompt(title, message, positive, positiveListener, negative, negativeListener, null);
	}

    public void prompt(String title, String message, String positive, final IStringListener positiveListener, String negative, final IListener negativeListener, String preTex) {
        prompt(title, message, positive, positiveListener, negative, negativeListener, preTex, null);
    }

    public void prompt(String title, String message, String positive, final IStringListener positiveListener, String negative, final IListener negativeListener, String preText, String hint) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        if(message!=null)
        alert.setMessage(message);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        if(preText!=null)
            input.setText(preText);
        if(hint!=null)
            input.setHint(hint);

        alert.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(positiveListener!=null)
                            positiveListener.execute(input.getText().toString());
                    }
                }
        );

        alert.setNegativeButton(negative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(negativeListener!=null)
                            negativeListener.execute();
                    }
                }
        );

        alert.show();
    }

    public final boolean isInternetConnection() {
        final ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null)
            if (activeNetwork.isConnected())
                return true;
        return false;
    }

	public abstract void refreshAdapters();
	public abstract void updateAdapters();
}
