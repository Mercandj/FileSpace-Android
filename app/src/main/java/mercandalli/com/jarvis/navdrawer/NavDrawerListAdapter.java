/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.navdrawer;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mercandalli.com.jarvis.Font;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.config.Const;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerListAdapter extends BaseAdapter {
	
	private Application app;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Application app, ArrayList<NavDrawerItem> navDrawerItems){
		this.app = app;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = app.getLayoutInflater();
		
		NavDrawerItem item = navDrawerItems.get(position);
		
		switch(navDrawerItems.get(position).viewType) {
		case Const.TAB_VIEW_TYPE_PROFIL:
			convertView = inflater.inflate(R.layout.tab_navdrawer_profil, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			Font.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");
			
			((TextView) convertView.findViewById(R.id.subtitle)).setText(item.subtitle);
			Font.applyFont(app, ((TextView) convertView.findViewById(R.id.subtitle)), "fonts/Roboto-Light.ttf");

			Bitmap icon_profile_online = app.getConfig().getUserProfiePicture();
			if(icon_profile_online!=null)
				((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(icon_profile_online);
			else if(navDrawerItems.get(position).containsImage)
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon));
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;		
		
		case Const.TAB_VIEW_TYPE_NORMAL:
			convertView = inflater.inflate(R.layout.tab_navdrawer, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			if(item.isSelected)
				Font.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");
			else
				Font.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Light.ttf");

			if(navDrawerItems.get(position).containsImage) {
				if(item.isSelected && item.icon_pressed != -1)
					((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon_pressed));
				else
					((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon));
			}
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;
			
		case Const.TAB_VIEW_TYPE_SECTION:
			convertView = inflater.inflate(R.layout.tab_navdrawer_section, parent, false);
			break;
			
		case Const.TAB_VIEW_TYPE_SETTING:
        case Const.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE:
			convertView = inflater.inflate(R.layout.tab_navdrawer_setting, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			if(item.isSelected)
				Font.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
			else
				Font.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAM.TTF");
			
			if(navDrawerItems.get(position).containsImage)
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon));
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;
		}
        
        return convertView;
	}
}
