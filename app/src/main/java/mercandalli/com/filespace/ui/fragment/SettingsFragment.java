/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.fragment;

import android.content.Intent;
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

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.ia.action.ENUM_Action;
import mercandalli.com.filespace.model.ModelSetting;
import mercandalli.com.filespace.ui.activity.ActivityRegisterLogin;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.adapter.AdapterModelSetting;
import mercandalli.com.filespace.util.TimeUtils;

public class SettingsFragment extends Fragment {

	private Application app;
	private View rootView;
	
	private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ModelSetting> list;
    private int click_version;
    private boolean isDevelopper = false;

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
		list = new ArrayList<>();
		list.add(new ModelSetting(app, "Settings", Const.TAB_VIEW_TYPE_SECTION));
        if(app.getConfig().isLogged()) {
            list.add(new ModelSetting(app, "Auto connection", new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    app.getConfig().setAutoConnection(isChecked);
                }
            }, app.getConfig().isAutoConncetion()));
            list.add(new ModelSetting(app, "Web application", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ENUM_Action.WEB_SEARCH.action.action(app, app.getConfig().webApplication);
                }
            }));
        }
        list.add(new ModelSetting(app, "Welcome on home screen", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(app, "Welcome message enabled.", Toast.LENGTH_SHORT).show();
                app.getConfig().setHomeWelcomeMessage(true);
            }
        }));
        if(app.getConfig().isLogged()) {
            list.add(new ModelSetting(app, "Change password", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO Change password
                    Toast.makeText(app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                }
            }));
        }
        if(isDevelopper) {
            list.add(new ModelSetting(app, "Login / Sign in", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(app, ActivityRegisterLogin.class);
                    app.startActivity(intent);
                    app.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    app.finish();
                }
            }));
        }

		try {
			PackageInfo pInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            list.add(new ModelSetting(app, "Last update date GMT", TimeUtils.getGMTDate(pInfo.lastUpdateTime)));
            list.add(new ModelSetting(app, "Version", pInfo.versionName, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (click_version == 11) {
                        Toast.makeText(app, "Development settings activated.", Toast.LENGTH_SHORT).show();
                        isDevelopper = true;
                        refreshList();
                    } else if (click_version < 11) {
                        if (click_version >= 1) {
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
            }));
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
                    if (position < list.size()) {
                        if(list.get(position).onClickListener != null)
                            list.get(position).onClickListener.onClick(view);
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

    @Override
    public void onFocus() {

    }
}
