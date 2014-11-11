/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.dialog.DialogUpload;
import com.mercandalli.jarvis.listener.IListener;
import com.mercandalli.jarvis.listener.IPostExecuteListener;

public class FileManagerFragment extends Fragment {
	
	private final int NB_FRAGMENT = 2;
	public Fragment listFragment[] = new Fragment[NB_FRAGMENT];
	private Application app;
	private ViewPager mViewPager;
	private FileManagerFragmentPagerAdapter mPagerAdapter;
	
	public FileManagerFragment(Application app) {
		this.app = app;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_filemanager, container, false);
		mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager());
		
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int arg0) {
				FileManagerFragment.this.app.invalidateOptionsMenu();
			}			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
        return rootView;
	}
	
	public int getCurrentFragmentIndex() {
		return mViewPager.getCurrentItem();
	}
	
	public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {

		public FileManagerFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
        public Fragment getItem(int i) {
			Fragment fragment = null;
			switch(i) {
			case 0:		fragment = new FileManagerFragmentServer(FileManagerFragment.this.app); 	break;
			case 1:		fragment = new FileManagerFragmentLocal(FileManagerFragment.this.app);		break;
			default:	fragment = new FileManagerFragmentLocal(FileManagerFragment.this.app);		break;
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
			case 0:		title = "SERVER";		break;
			case 1:		title = "LOCAL";		break;
			}
			return title;
        }
    }
	
	public void refreshListServer() {
		if(this.listFragment[0]!=null)
			if(this.listFragment[0] instanceof FileManagerFragmentServer) {
				FileManagerFragmentServer fragmentFileManagerFragment = (FileManagerFragmentServer) this.listFragment[0];
				fragmentFileManagerFragment.refreshList();
			}
	}
	
	public void add() {
		app.dialog = new DialogUpload(app, new IPostExecuteListener() {
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
				
			}
		}, "No", null);
	}

	public void upload() {
		this.app.alert("Upload", "Upload all files ?", "Yes", new IListener() {			
			@Override
			public void execute() {
				
			}
		}, "No", null);
	}
}
