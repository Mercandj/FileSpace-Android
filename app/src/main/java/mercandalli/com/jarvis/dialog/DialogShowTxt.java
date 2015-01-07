/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.dialog;

import android.app.Dialog;
import android.widget.EditText;

import java.io.File;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.model.ModelFile;

public class DialogShowTxt extends Dialog {
	
	DialogFileChooser dialogFileChooser;
	Application app;
	File file;
	ModelFile modelFile;
	
	public DialogShowTxt(final Application app, String txt) {
		super(app);
		this.app = app;
		
		this.setContentView(R.layout.view_file_text);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
	    
        ((EditText) this.findViewById(R.id.txt)).setText(""+txt);
        
        DialogShowTxt.this.show();
	}
}
