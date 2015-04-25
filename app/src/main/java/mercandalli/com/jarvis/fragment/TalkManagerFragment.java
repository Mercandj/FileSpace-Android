/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.activity.ApplicationDrawer;
import mercandalli.com.jarvis.dialog.DialogAddFileManager;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.view.PagerSlidingTabStrip;

public class TalkManagerFragment extends Fragment {

    private static final int NB_FRAGMENT = 2;
    private static final int INIT_FRAGMENT = 0;
    public static Fragment listFragment[] = new Fragment[NB_FRAGMENT];
    private Application app;
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private PagerSlidingTabStrip tabs;

    public TalkManagerFragment() {
        super();
    }

    public TalkManagerFragment(ApplicationDrawer app) {
        super();
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_talkmanager, container, false);
        mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager(), app);

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                TalkManagerFragment.this.app.invalidateOptionsMenu();
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT - 1);
        mViewPager.setCurrentItem(this.INIT_FRAGMENT);

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
                case 0:		fragment = new UserFragment();  	break;
                case 1:		fragment = new TalkFragment(); 	    break;
                default:	fragment = new UserFragment();		break;
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
                case 0:		title = "USERS";	break;
                case 1:		title = "TALKS";    break;
                default:	title = "USERS";	break;
            }
            return title;
        }
    }


    public void refreshListServer() {
        refreshListServer(null);
    }

    public void refreshListServer(String search) {
        if(listFragment[0]!=null)
            if(listFragment[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) listFragment[0];
                fragmentFileManagerFragment.refreshList(search);
            }
        if(listFragment[1]!=null)
            if(listFragment[1] instanceof TalkFragment) {
                TalkFragment fragmentFileManagerFragment = (TalkFragment) listFragment[1];
                fragmentFileManagerFragment.refreshList(search);
            }
    }

    public void updateAdapterListServer() {
        if(listFragment[0]!=null)
            if(listFragment[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) listFragment[0];
                fragmentFileManagerFragment.updateAdapter();
            }
        if(listFragment.length>1)
            if(listFragment[1]!=null)
                if(listFragment[1] instanceof TalkFragment) {
                    TalkFragment fragmentFileManagerFragment = (TalkFragment) listFragment[1];
                    fragmentFileManagerFragment.updateAdapter();
                }
    }

    public void refreshAdapterListServer() {
        if(listFragment[0]!=null)
            if(listFragment[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) listFragment[0];
                fragmentFileManagerFragment.refreshList();
            }
        if(listFragment.length>1)
            if(listFragment[1]!=null)
                if(listFragment[1] instanceof TalkFragment) {
                    TalkFragment fragmentFileManagerFragment = (TalkFragment) listFragment[1];
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
}
