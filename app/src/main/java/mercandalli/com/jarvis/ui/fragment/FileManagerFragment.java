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
package mercandalli.com.jarvis.ui.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.ui.activity.ApplicationDrawer;
import mercandalli.com.jarvis.ui.dialog.DialogAddFileManager;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.ui.view.PagerSlidingTabStrip;

import static mercandalli.com.jarvis.util.NetUtils.isInternetConnection;

public class FileManagerFragment extends Fragment {
	
	private static final int NB_FRAGMENT = 3;
    private static final int INIT_FRAGMENT = 1;
	public static Fragment listFragment[] = new Fragment[NB_FRAGMENT];
	private Application app;
	private ViewPager mViewPager;
	private FileManagerFragmentPagerAdapter mPagerAdapter;
    private PagerSlidingTabStrip tabs;
	
	public FileManagerFragment() {
		super();
	}
	
	public FileManagerFragment(ApplicationDrawer app) {
		super();
		this.app = app;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_filemanager, container, false);
		mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager(), app);

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                FileManagerFragment.this.app.invalidateOptionsMenu();
            }
        });
		if(isInternetConnection(app) && app.isLogged()) {
			mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT - 1);
			mViewPager.setCurrentItem(this.INIT_FRAGMENT);
		}
		else {
			mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT);
			mViewPager.setCurrentItem(this.INIT_FRAGMENT + 1);
		}

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.white));
		
        return rootView;
	}
	
	public int getCurrentFragmentIndex() {
        if(mViewPager == null)
            return -1;
        int result = mViewPager.getCurrentItem();
		if(result >= listFragment.length)
            return -1;
        return mViewPager.getCurrentItem();
	}

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if(listFragment == null || currentFragmentId== -1)
            return false;
        Fragment fragment = listFragment[currentFragmentId];
        if(fragment==null)
            return false;
        return fragment.back();
    }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
		Application app;
		
		public FileManagerFragmentPagerAdapter(FragmentManager fm, Application app) {
			super(fm);
			this.app = app;
		}
		
		@Override
        public Fragment getItem(int i) {
			Fragment fragment = null;
			switch(i) {
			case 0:		fragment = new FileManagerFragmentCloud();  	break;
            case 1:		fragment = new FileManagerFragmentMyCloud(); 	break;
			case 2:		fragment = new FileManagerFragmentLocal();		break;
			default:	fragment = new FileManagerFragmentLocal();		break;
			}
			listFragment[i] = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
        	String title = "null";
			switch(i) {
			case 0:		title = "PUBLIC CLOUD";		break;
            case 1:		title = "MY CLOUD";		break;
			case 2:		title = "LOCAL";		break;
			}
			return title;
        }
    }
	

	public void refreshListServer() {
		refreshListServer(null);
	}
	
	public void refreshListServer(String search) {
		if(listFragment[0]!=null)
			if(listFragment[0] instanceof FileManagerFragmentCloud) {
                FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) listFragment[0];
				fragmentFileManagerFragment.refreshList(search);
			}
        if(listFragment[1]!=null)
            if(listFragment[1] instanceof FileManagerFragmentMyCloud) {
                FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) listFragment[1];
                fragmentFileManagerFragment.refreshList(search);
            }
        if(listFragment[2]!=null)
            if(listFragment[2] instanceof FileManagerFragmentLocal) {
                FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
                fragmentFileManagerFragment.refreshList(search);
            }
	}
	
	public void updateAdapterListServer() {
		if(listFragment[0]!=null)
			if(listFragment[0] instanceof FileManagerFragmentCloud) {
				FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) listFragment[0];
				fragmentFileManagerFragment.updateAdapter();
			}
        if(listFragment.length>1)
            if(listFragment[1]!=null)
                if(listFragment[1] instanceof FileManagerFragmentMyCloud) {
                    FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) listFragment[1];
                    fragmentFileManagerFragment.updateAdapter();
                }
		if(listFragment.length>2)
			if(listFragment[2]!=null)
				if(listFragment[2] instanceof FileManagerFragmentLocal) {
					FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
					fragmentFileManagerFragment.updateAdapter();
				}
	}
	
	public void refreshAdapterListServer() {
		if(listFragment[0]!=null)
			if(listFragment[0] instanceof FileManagerFragmentCloud) {
                FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) listFragment[0];
				fragmentFileManagerFragment.refreshList();
			}		
		if(listFragment.length>1)
			if(listFragment[1]!=null)
				if(listFragment[1] instanceof FileManagerFragmentMyCloud) {
                    FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) listFragment[1];
					fragmentFileManagerFragment.refreshList();
				}
        if(listFragment.length>2)
            if(listFragment[2]!=null)
                if(listFragment[2] instanceof FileManagerFragmentLocal) {
                    FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
                    fragmentFileManagerFragment.refreshList();
                }
	}

	public void add() {
		app.dialog = new DialogAddFileManager(app, -1, new IPostExecuteListener() {
			@Override
			public void execute(JSONObject json, String body) {
				if(json!=null)
					refreshListServer();
			}
		});
	}

	public void download() {
		this.app.alert("Download", "Download all files ?", "Yes", new IListener() {
			@Override
			public void execute() {
                // TODO download all
                Toast.makeText(getActivity(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
			}
		}, "No", null);
	}

	public void upload() {
		this.app.alert("Upload", "Upload all files ?", "Yes", new IListener() {			
			@Override
			public void execute() {
                // TODO Upload all
                Toast.makeText(getActivity(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
			}
		}, "No", null);
	}

	public View getFab() {
        if(listFragment[0]!=null)
            if(listFragment[0] instanceof FileManagerFragmentCloud) {
                FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) listFragment[0];
                return fragmentFileManagerFragment.getFab();
            }
        if(listFragment.length>1)
            if(listFragment[1]!=null)
                if(listFragment[1] instanceof FileManagerFragmentMyCloud) {
                    FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) listFragment[1];
                    return fragmentFileManagerFragment.getFab();
                }
        if(listFragment.length>2)
            if(listFragment[2]!=null)
                if(listFragment[2] instanceof FileManagerFragmentLocal) {
                    FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
                    return fragmentFileManagerFragment.getFab();
                }
		return null;
	}

    public void goHome() {
        if(listFragment.length>2)
            if(listFragment[2]!=null)
                if(listFragment[2] instanceof FileManagerFragmentLocal) {
                    FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
                    fragmentFileManagerFragment.goHome();
                }
    }
}
