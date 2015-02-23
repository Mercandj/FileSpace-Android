package mercandalli.com.jarvis.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.listener.IModelFileListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelFileTypeENUM;

public class AdapterModelFile extends ArrayAdapter<ModelFile> {

	private Application app;
	private List<ModelFile> files;
	private IModelFileListener moreListener;
	
	public AdapterModelFile(Application app, int resource, List<ModelFile> files, IModelFileListener moreListener) {
		super(app, resource, files);
		this.app = app;
		this.files = files;
		this.moreListener = moreListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater inflater = app.getLayoutInflater();
		
		if(position<files.size()) {		
			final ModelFile file = files.get(position);			
			convertView = inflater.inflate(R.layout.tab_file, parent, false);
			
			if(file.name!=null)
				((TextView) convertView.findViewById(R.id.title)).setText(file.getNameExt());
			else
				((TextView) convertView.findViewById(R.id.title)).setText(file.url);

            if(file.directory)
                ((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getDrawable(R.drawable.directory));
			else if(file.type!=null)
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
