/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.dialog;

import java.io.File;

import android.app.Dialog;
import android.widget.TextView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.model.ModelFile;

public class DialogShowTxt extends Dialog {
	
	DialogFileChooser dialogFileChooser;
	Application app;
	File file;
	ModelFile modelFile;
	
	public DialogShowTxt(final Application app, String txt) {
		super(app);
		this.app = app;
		
		this.setContentView(R.layout.view_edit_txt);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
	    
        ((TextView) this.findViewById(R.id.txt)).setText(""+txt);
        
        DialogShowTxt.this.show();
	}
}
