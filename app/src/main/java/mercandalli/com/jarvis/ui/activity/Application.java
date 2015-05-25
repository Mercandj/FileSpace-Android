/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */

package mercandalli.com.jarvis.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.config.Config;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.listener.IStringListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.net.TaskPost;

public abstract class Application extends AppCompatActivity {

	private Config config;
	public Dialog dialog;

    /* OnResult code */
    public final int REQUEST_TAKE_PHOTO = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	
	public void alert(String title, String message, String positive, final IListener positiveListener, String negative, final IListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
        if(positive != null)
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(positiveListener!=null)
                        positiveListener.execute();
                    dialog.dismiss();
                }
            });
        if(negative != null)
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

	public abstract void refreshAdapters();
	public abstract void updateAdapters();


    private String buildTagViews(NdefMessage[] msgs){
        if (msgs == null || msgs.length == 0) {
            return null;
        } else{
            return new String(msgs[0].getRecords()[0].getPayload());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if(photoFile!=null) {
                if (photoFile.file != null) {
                    List<BasicNameValuePair> parameters = null;
                    if (photoFile != null)
                        parameters = photoFile.getForUpload();
                    (new TaskPost(this, getConfig().getUrlServer() + getConfig().routeFile, new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            if (photoFileListener != null)
                                photoFileListener.execute(json, body);
                        }
                    }, parameters, photoFile.file)).execute();
                }
            }
            else
                Toast.makeText(this, this.getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        }
    }

    public ModelFile photoFile = null;
    public IPostExecuteListener photoFileListener = null;

    public ModelFile createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_Jarvis_";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+this.getConfig().localFolderName);
        ModelFile result = new ModelFile(this);
        result.name = imageFileName + ".jpg";
        result.file = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return result;
    }

}
