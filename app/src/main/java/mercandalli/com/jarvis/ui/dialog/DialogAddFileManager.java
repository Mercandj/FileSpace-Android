/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.listener.IStringListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.net.TaskPost;

public class DialogAddFileManager extends Dialog {

	DialogFileChooser dialogFileChooser;
	Application app;
	File file;
	ModelFile modelFile;

	public DialogAddFileManager(final Application app, final int id_file_parent, final IPostExecuteListener listener) {
		super(app, android.R.style.Theme_Translucent_NoTitleBar);
		this.app = app;
		
		this.setContentView(R.layout.view_add_file);
		this.setCancelable(true);

        Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.dialog_add_file_open);
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
                app.dialog = new DialogUpload(app, id_file_parent, listener);
                DialogAddFileManager.this.dismiss();
            }
        });

        ((RelativeLayout) this.findViewById(R.id.addDirectory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.prompt(app.getString(R.string.dialog_file_create_folder), app.getString(R.string.dialog_file_name_interrogation), app.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        ModelFile folder = new ModelFile(DialogAddFileManager.this.app);
                        folder.name = text;
                        folder.directory = true;
                        folder.id_file_parent = id_file_parent;
                        List<BasicNameValuePair> parameters = folder.getForUpload();
                        (new TaskPost(app, app.getConfig().getUrlServer()+app.getConfig().routeFile, new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                if(listener!=null)
                                    listener.execute(json, body);
                            }
                        }, parameters, file)).execute();
                    }
                }, app.getString(R.string.cancel), null);
                DialogAddFileManager.this.dismiss();
            }
        });

        ((RelativeLayout) this.findViewById(R.id.txtFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.prompt(app.getString(R.string.dialog_file_create_txt), app.getString(R.string.dialog_file_name_interrogation), app.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        //TODO
                    }
                }, app.getString(R.string.cancel), null);
                DialogAddFileManager.this.dismiss();
            }
        });

        ((RelativeLayout) this.findViewById(R.id.scan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(app.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    app.photoFile = new ModelFile(app);
                    try {
                        app.photoFile = app.createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (app.photoFile != null) {
                        if(listener!=null)
                            app.photoFileListener = listener;
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(app.photoFile.file));
                        app.startActivityForResult(takePictureIntent, app.REQUEST_TAKE_PHOTO);
                    }
                }
                DialogAddFileManager.this.dismiss();
            }
        });

        ((RelativeLayout) this.findViewById(R.id.addTimer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();

                DialogDatePicker dialogDate = new DialogDatePicker(app, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {

                        Calendar currentTime = Calendar.getInstance();

                        DialogTimePicker dialogTime = new DialogTimePicker(app, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Log.d("TIme Picker", hourOfDay + ":" + minute);

                                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                                SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                String nowAsISO = dateFormatGmt.format(new Date());

                                JSONObject json = new JSONObject();
                                try {
                                    json.put("type", "timer");
                                    json.put("date_creation", nowAsISO);
                                    json.put("timer_date", "" + dateFormatGmt.format(dateFormatLocal.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute + ":00")));

                                    SimpleDateFormat dateFormatGmtTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm'Z'");
                                    dateFormatGmtTZ.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    nowAsISO = dateFormatGmtTZ.format(new Date());

                                    List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                                    parameters.add(new BasicNameValuePair("content",json.toString()));
                                    parameters.add(new BasicNameValuePair("name","TIMER_"+nowAsISO));
                                    parameters.add(new BasicNameValuePair("id_file_parent",""+id_file_parent));
                                    new TaskPost(DialogAddFileManager.this.app,
                                            app.getConfig().getUrlServer()+app.getConfig().routeFile,
                                            new IPostExecuteListener() {
                                                @Override
                                                public void execute(JSONObject json, String body) {
                                                    if(listener!=null)
                                                        listener.execute(json, body);
                                                }
                                            }
                                            ,parameters).execute();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
                        dialogTime.show();

                    }
                },mcurrentTime.get(Calendar.YEAR), mcurrentTime.get(Calendar.MONTH), mcurrentTime.get(Calendar.DAY_OF_MONTH));
                dialogDate.show();

                DialogAddFileManager.this.dismiss();
            }
        });

        
        DialogAddFileManager.this.show();
	}
}
