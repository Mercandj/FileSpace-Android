/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.fragment.genealogy;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IModelGenealogyUserListener;
import mercandalli.com.filespace.model.ModelGenealogyPerson;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.fragment.Fragment;
import mercandalli.com.filespace.ui.fragment.FragmentFab;
import mercandalli.com.filespace.ui.view.NonSwipeableViewPager;
import mercandalli.com.filespace.ui.view.PagerSlidingTabStrip;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;


public class GenealogyFragment extends Fragment {

    private static final int NB_FRAGMENT = 4;
    private static final int INIT_FRAGMENT = 0;
    public static FragmentFab listFragment[] = new FragmentFab[NB_FRAGMENT];
    private NonSwipeableViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private PagerSlidingTabStrip tabs;

    private FloatingActionButton circle, circle2;
    private View coordinatorLayoutView;
    private Snackbar snackbar;

    public GenealogyFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_genealogy, container, false);

        this.coordinatorLayoutView = (View) rootView.findViewById(R.id.snackBarPosition);

        mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager(), app);

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        mViewPager = (NonSwipeableViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setNonSwipeableItem(2);
        mViewPager.setAdapter(mPagerAdapter);
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                GenealogyFragment.this.app.invalidateOptionsMenu();
                if (listFragment[position] instanceof GenealogyTreeFragment) {
                    ((GenealogyTreeFragment) listFragment[position]).update();
                }
                if (position < NB_FRAGMENT)
                    if (listFragment[position] != null)
                        listFragment[position].onFocus();
                updateNoInternet();
                refreshFab(position);
            }
        });
        mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
        mViewPager.setCurrentItem(INIT_FRAGMENT);

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.white));

        this.circle = ((FloatingActionButton) rootView.findViewById(R.id.circle));
        this.circle.setVisibility(View.GONE);

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

    @Override
    public void onFocus() {

    }

    private void refreshFab() {
        refreshFab(getCurrentFragmentIndex());
    }

    private void refreshFab(int currentFragmentId) {
        if(listFragment == null || currentFragmentId== -1)
            return;
        FragmentFab fragment = listFragment[currentFragmentId];
        if(fragment==null)
            return;
        refreshFab(fragment);
    }

    private void refreshFab(final FragmentFab currentFragment) {
        if(currentFragment.isFabVisible(0))
            this.circle.show();
        else
            this.circle.hide();
        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFragment.onFabClick(0, circle);
            }
        });
        if(currentFragment.getFabDrawable(0) != null)
            this.circle.setImageDrawable(currentFragment.getFabDrawable(0));
        else
            this.circle.setImageDrawable(app.getDrawable(android.R.drawable.ic_input_add));
    }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
        Application app;

        public FileManagerFragmentPagerAdapter(FragmentManager fm, Application app) {
            super(fm);
            this.app = app;
        }

        @Override
        public Fragment getItem(int i) {
            FragmentFab fragment;
            switch(i) {
                case 0:
                    GenealogyListFragment fr = GenealogyListFragment.newInstance();
                    fr.setOnSelect(new IModelGenealogyUserListener() {
                        @Override
                        public void execute(ModelGenealogyPerson modelPerson) {
                            for(Fragment fr : listFragment) {
                                if (fr instanceof GenealogyTreeFragment)
                                    ((GenealogyTreeFragment) fr).select(modelPerson);
                                else if (fr instanceof GenealogyBigTreeFragment)
                                    ((GenealogyBigTreeFragment) fr).select(modelPerson);
                            }
                        }
                    });
                    fragment = fr;
                    break;
                case 1:		fragment = GenealogyTreeFragment.newInstance();         break;
                case 2:		fragment = GenealogyBigTreeFragment.newInstance();      break;
                case 3:		fragment = GenealogyStatisticsFragment.newInstance();   break;
                default:    fragment = GenealogyTreeFragment.newInstance();
            }
            fragment.setRefreshFab(new IListener() {
                @Override
                public void execute() {
                    refreshFab();
                }
            });
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
                case 0:		title = "LIST";         break;
                case 1:		title = "TREE";	        break;
                case 2:		title = "BIG TREE";	    break;
                case 3:		title = "STATISTICS";   break;
            }
            return title;
        }
    }

    public void refreshListServer() {
        refreshListServer(null);
    }

    public void refreshListServer(String search) {
        if(listFragment[0]!=null)
            if(listFragment[0] instanceof GenealogyListFragment) {
                GenealogyListFragment fragmentFileManagerFragment = (GenealogyListFragment) listFragment[0];
                fragmentFileManagerFragment.refreshList(search);
            }
    }

    private void updateNoInternet() {
        if(!isInternetConnection(app)) {
            this.snackbar = Snackbar.make(this.coordinatorLayoutView, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.refresh), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isInternetConnection(app))
                                listFragment[getCurrentFragmentIndex()].onFocus();
                            else
                                updateNoInternet();
                        }
                    });
            this.snackbar.show();
        }
    }
}