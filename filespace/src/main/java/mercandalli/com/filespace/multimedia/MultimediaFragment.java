package mercandalli.com.filespace.multimedia;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.ui.activities.Application;
import mercandalli.com.filespace.ui.activities.ApplicationDrawer;
import mercandalli.com.filespace.ui.fragments.BackFragment;

public class MultimediaFragment extends BackFragment {

    private MaterialViewPager mViewPager;

    private Toolbar mToolbar;

    private ApplicationDrawer app;

    public static MultimediaFragment newInstance() {
        Bundle args = new Bundle();
        MultimediaFragment fragment = new MultimediaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof Application)
            app = (ApplicationDrawer) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.multmedia_activity, container, false);

        app.setTitle("");

        mViewPager = (MaterialViewPager) rootView.findViewById(R.id.materialViewPager);

        mToolbar = mViewPager.getToolbar();

        if (mToolbar != null) {
            app.setToolbar(mToolbar);
        }

        final ActionBar actionBar = app.getSupportActionBar();
        if (actionBar != null) {
            /*
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            */
        }


        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(app.getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return RecyclerViewFragment.newInstance();
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                        return "Selection";
                    case 1:
                        return "Actualités";
                    case 2:
                        return "Professionnel";
                    case 3:
                        return "Divertissement";
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.red,
                                "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        View logo = rootView.findViewById(R.id.logo_white);
        if (logo != null)
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    Toast.makeText(app, "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });

        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_search)	.setVisible(true);
        menu.findItem(R.id.action_delete)	.setVisible(false);
        menu.findItem(R.id.action_add)		.setVisible(false);
        menu.findItem(R.id.action_download)	.setVisible(false);
        menu.findItem(R.id.action_upload)	.setVisible(false);
        menu.findItem(R.id.action_home) 	.setVisible(false);
        menu.findItem(R.id.action_sort)	    .setVisible(true);
    }
}
