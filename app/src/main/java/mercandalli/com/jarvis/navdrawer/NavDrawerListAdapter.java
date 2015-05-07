/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.navdrawer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mercandalli.com.jarvis.Font;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.config.Const;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerListAdapter extends BaseAdapter {
	
	private Activity context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Activity context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
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
		LayoutInflater inflater = context.getLayoutInflater();
		
		NavDrawerItem item = navDrawerItems.get(position);
		
		switch(navDrawerItems.get(position).viewType) {
		case Const.TAB_VIEW_TYPE_PROFIL:
			convertView = inflater.inflate(R.layout.tab_navdrawer_profil, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			Font.applyFont(context, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
			
			((TextView) convertView.findViewById(R.id.subtitle)).setText(item.subtitle);
			Font.applyFont(context, ((TextView) convertView.findViewById(R.id.subtitle)), "fonts/MYRIADAM.TTF");
			
			if(navDrawerItems.get(position).containsImage)
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(context.getResources().getDrawable(item.icon));
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;		
		
		case Const.TAB_VIEW_TYPE_NORMAL:
			convertView = inflater.inflate(R.layout.tab_navdrawer, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			if(item.isSelected)
				Font.applyFont(context, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
			else
				Font.applyFont(context, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAM.TTF");

			if(navDrawerItems.get(position).containsImage) {
				if(item.isSelected && item.icon_pressed != -1)
					((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(context.getResources().getDrawable(item.icon_pressed));
				else
					((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(context.getResources().getDrawable(item.icon));
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
				Font.applyFont(context, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
			else
				Font.applyFont(context, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAM.TTF");
			
			if(navDrawerItems.get(position).containsImage)
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(context.getResources().getDrawable(item.icon));
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;
		}
        
        return convertView;
	}
}
