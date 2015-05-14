/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ia.action.ENUM_Action;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.adapter.AdapterModelSetting;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.model.ModelSetting;

public class SettingsFragment extends Fragment {

	private Application app;
	private View rootView;
	
	private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelSetting> list;
	int click_version;

	public SettingsFragment(Application app) {
		this.app = app;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		
		recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
		recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
		click_version = 0;
        
        refreshList();
		
        return rootView;
	}
	
	public void refreshList() {
		list = new ArrayList<ModelSetting>();
		list.add(new ModelSetting(app, "Settings", Const.TAB_VIEW_TYPE_SECTION));
		list.add(new ModelSetting(app, "Auto connection", new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				app.getConfig().setAutoConnection(isChecked);
			}
		}, app.getConfig().isAutoConncetion()));
        list.add(new ModelSetting(app, "Web application"));

		try {
			PackageInfo pInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
			list.add(new ModelSetting(app, "Version "+pInfo.versionName));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		updateAdapter();		
	}
	
	public void updateAdapter() {
		if(recyclerView!=null && list!=null) {
            AdapterModelSetting adapter = new AdapterModelSetting(app, list);
            adapter.setOnItemClickListener(new AdapterModelSetting.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(position < list.size()) {
                        if(position==2) {
                            ENUM_Action.WEB_SEARCH.action.action(app, app.getConfig().webApplication);
                        }
						if(position==3) {
							if(click_version==11) {
								Toast.makeText(app, "Development settings activated.", Toast.LENGTH_SHORT).show();
							}
							else if(click_version<11) {
								if(click_version>=1) {
									final Toast t = Toast.makeText(app, "" + (11 - click_version), Toast.LENGTH_SHORT);
									t.show();
									Handler handler = new Handler();
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											t.cancel();
										}
									}, 700);
								}
								click_version++;
							}
						}
                    }
                }
            });
			recyclerView.setAdapter(adapter);
		}
	}

    @Override
    public boolean back() {
        return false;
    }
}
