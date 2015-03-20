/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.dialog;

import android.app.Dialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.listener.IStringListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.net.TaskPost;

public class DialogAddFileManager extends Dialog {

	DialogFileChooser dialogFileChooser;
	Application app;
	File file;
	ModelFile modelFile;

	public DialogAddFileManager(final Application app, final IPostExecuteListener listener) {
		super(app, android.R.style.Theme_Translucent_NoTitleBar);
		this.app = app;
		
		this.setContentView(R.layout.view_add_file);
		this.setCancelable(true);

        Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
        ((RelativeLayout) this.findViewById(R.id.relativeLayout)).startAnimation(animOpen);

        ((RelativeLayout) this.findViewById(R.id.relativeLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddFileManager.this.dismiss();
            }
        });

        ((RelativeLayout) this.findViewById(R.id.uploadFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.dialog = new DialogUpload(app, listener);
                DialogAddFileManager.this.dismiss();
            }
        });

        ((RelativeLayout) this.findViewById(R.id.addDirectory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.prompt("Create Folder", "Name ?", "Create", new IStringListener() {
                    @Override
                    public void execute(String text) {
                        ModelFile folder = new ModelFile(DialogAddFileManager.this.app);
                        folder.name = text;
                        folder.directory = true;
                        List<BasicNameValuePair> parameters = folder.getForUpload();
                        (new TaskPost(app, app.getConfig().getUrlServer()+app.getConfig().routeFile, new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                if(listener!=null)
                                    listener.execute(json, body);
                            }
                        }, parameters, file)).execute();
                    }
                }, "Cancel", null);
                DialogAddFileManager.this.dismiss();
            }
        });

        ((ImageButton) this.findViewById(R.id.txtFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.prompt("Create TXT File", "Name ?", "Create", new IStringListener() {
                    @Override
                    public void execute(String text) {
                        //TODO
                    }
                }, "Cancel", null);
                DialogAddFileManager.this.dismiss();
            }
        });
        
        DialogAddFileManager.this.show();
	}
}
