package com.mercandalli.jarvis.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.listener.IModelFileListener;
import com.mercandalli.jarvis.model.ModelFile;
import com.mercandalli.jarvis.model.ModelFileTypeENUM;

public class AdapterModelFile extends ArrayAdapter<ModelFile> {

	Application app;
	List<ModelFile> files;
	IModelFileListener clickListener, moreListener;
	
	public AdapterModelFile(Application app, int resource, List<ModelFile> files, IModelFileListener clickListener, IModelFileListener moreListener) {
		super(app, resource, files);
		this.app = app;
		this.files = files;
		this.clickListener = clickListener;
		this.moreListener = moreListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater inflater = app.getLayoutInflater();
		
		if(position<files.size()) {		
			final ModelFile file = files.get(position);			
			convertView = inflater.inflate(R.layout.tab_file, parent, false);
			
			if(file.name!=null)
				((TextView) convertView.findViewById(R.id.title)).setText(file.name);
			else
				((TextView) convertView.findViewById(R.id.title)).setText(file.url);
			
			if(file.type.equals(ModelFileTypeENUM.AUDIO.type))
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getDrawable(R.drawable.file_audio));
			else if(file.type.equals(ModelFileTypeENUM.PDF.type))
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getDrawable(R.drawable.file_pdf));
			else if(file.type.equals(ModelFileTypeENUM.APK.type))
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getDrawable(R.drawable.file_apk));
						
			if(file.bitmap!=null)
				((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(file.bitmap);
			
			((ImageView) convertView.findViewById(R.id.more)).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(moreListener!=null)
						moreListener.execute(file);
				}
			});
			
		}
		return convertView;
	}
	
	@Override
	public int getCount() {
		return files.size();
	}
}
