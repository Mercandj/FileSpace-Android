/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis_android.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import mercandalli.com.jarvis_android.Application;
import mercandalli.com.jarvis_android.ApplicationDrawer;
import mercandalli.com.jarvis_android.R;
import mercandalli.com.jarvis_android.dialog.DialogUpload;
import mercandalli.com.jarvis_android.listener.IListener;
import mercandalli.com.jarvis_android.listener.IPostExecuteListener;

public class FileManagerFragment extends Fragment {
	
	private static final int NB_FRAGMENT = 2;
	public static Fragment listFragment[] = new Fragment[NB_FRAGMENT];
	private Application app;
	private ViewPager mViewPager;
	private FileManagerFragmentPagerAdapter mPagerAdapter;
	
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
	
	public static class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
		Application app;
		
		public FileManagerFragmentPagerAdapter(FragmentManager fm, Application app) {
			super(fm);
			this.app = app;
		}
		
		@Override
        public Fragment getItem(int i) {
			Fragment fragment = null;
			switch(i) {
			case 0:		fragment = new FileManagerFragmentOnline(this.app); 	break;
			case 1:		fragment = new FileManagerFragmentLocal(this.app);		break;
			default:	fragment = new FileManagerFragmentLocal(this.app);		break;
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
		refreshListServer(null);
	}
	
	public void refreshListServer(String search) {
		if(listFragment[0]!=null)
			if(listFragment[0] instanceof FileManagerFragmentOnline) {
				FileManagerFragmentOnline fragmentFileManagerFragment = (FileManagerFragmentOnline) listFragment[0];
				fragmentFileManagerFragment.refreshList(search);
			}
	}	
	
	public void updateAdapterListServer() {
		if(listFragment[0]!=null)
			if(listFragment[0] instanceof FileManagerFragmentOnline) {
				FileManagerFragmentOnline fragmentFileManagerFragment = (FileManagerFragmentOnline) listFragment[0];
				fragmentFileManagerFragment.updateAdapter();
			}		
		if(listFragment.length>1)
			if(listFragment[1]!=null)
				if(listFragment[1] instanceof FileManagerFragmentLocal) {
					FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[1];
					fragmentFileManagerFragment.refreshList();
				}
	}
	
	public void refreshAdapterListServer() {
		if(listFragment[0]!=null)
			if(listFragment[0] instanceof FileManagerFragmentOnline) {
				FileManagerFragmentOnline fragmentFileManagerFragment = (FileManagerFragmentOnline) listFragment[0];
				fragmentFileManagerFragment.refreshList();
			}		
		if(listFragment.length>1)
			if(listFragment[1]!=null)
				if(listFragment[1] instanceof FileManagerFragmentLocal) {
					FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[1];
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
