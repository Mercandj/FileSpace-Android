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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import mercandalli.com.jarvis.config.Config;
import mercandalli.com.jarvis.library.Library;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.listener.IStringListener;

public abstract class Application extends ActionBarActivity {

    private Library lib;
	private Config config;
	public Dialog dialog;
    public Toolbar toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        lib = new Library(this);
		config = new Config(this);

        //region Handle NFC
        Intent intent = getIntent();
        String action = intent.getAction();
        NdefMessage[] msgs = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            Toast.makeText(this, ""+type, Toast.LENGTH_SHORT).show();

            // Check the MIME
            if (type.equals("text/plain")) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMsgs != null) {
                    msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                }
                Toast.makeText(this, ""+buildTagViews(msgs), Toast.LENGTH_SHORT).show();
            }
        }
        //endregion
	}

	public Config getConfig() {
		if(config == null)
			config = new Config(this);
		return config;
	}

    public Library getLibrary() {
        if(lib == null)
            lib = new Library(this);
        return lib;
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


    private String buildTagViews(NdefMessage[] msgs){
        if (msgs == null || msgs.length == 0) {
            return null;
        } else{
            return new String(msgs[0].getRecords()[0].getPayload());
        }
    }
}
