/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis_android.dialog;

import android.app.Dialog;
import android.widget.TextView;

import java.io.File;

import mercandalli.com.jarvis_android.Application;
import mercandalli.com.jarvis_android.R;
import mercandalli.com.jarvis_android.model.ModelFile;

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
