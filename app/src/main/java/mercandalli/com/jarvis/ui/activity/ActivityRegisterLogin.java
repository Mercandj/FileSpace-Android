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
package mercandalli.com.jarvis.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.fragment.InscriptionFragment;
import mercandalli.com.jarvis.ui.fragment.LoginFragment;
import mercandalli.com.jarvis.ui.view.PagerSlidingTabStrip;

public class ActivityRegisterLogin extends Application {

    private final int NB_FRAGMENT = 2;
    private int INIT_FRAGMENT = 1;
    public Fragment listFragment[] = new Fragment[NB_FRAGMENT];
    private ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        RegisterLoginPagerAdapter mPagerAdapter = new RegisterLoginPagerAdapter(this.getFragmentManager(), this);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        mViewPager = (ViewPager) this.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                ActivityRegisterLogin.this.invalidateOptionsMenu();
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT - 1);

        if(this.getConfig().getUserUsername()==null || this.getConfig().getUserPassword()==null)
            this.INIT_FRAGMENT = 0;
        else if(this.getConfig().getUserUsername().equals("") || this.getConfig().getUserPassword().equals(""))
            this.INIT_FRAGMENT = 0;

        mViewPager.setCurrentItem(this.INIT_FRAGMENT);

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.white));

        if(this.getConfig().isAutoConncetion() && this.getConfig().getUrlServer()!=null && this.getConfig().getUserUsername()!=null && this.getConfig().getUserPassword()!=null)
            if(!this.getConfig().getUserUsername().equals("") && !this.getConfig().getUserPassword().equals("") && this.getConfig().getUserId() != -1)
        	    connectionSucceed();

        (this.findViewById(R.id.signin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listFragment[getCurrentFragmentIndex()] != null) {
                    if(listFragment[getCurrentFragmentIndex()] instanceof InscriptionFragment) {
                        ((InscriptionFragment)listFragment[getCurrentFragmentIndex()]).clickSignIn();
                    }
                    else if(listFragment[getCurrentFragmentIndex()] instanceof LoginFragment) {
                        ((LoginFragment)listFragment[getCurrentFragmentIndex()]).clickSignIn();
                    }
                }
            }
        });
	}
	
	public void connectionSucceed() {
		Intent intent = new Intent(this, ActivityMain.class);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		this.finish();
	}

	@Override
	public void updateAdapters() {
		
	}

    @Override
    public View getFab() {
        return null;
    }

    @Override
	public void refreshAdapters() {
		
	}

    public int getCurrentFragmentIndex() {
        return mViewPager.getCurrentItem();
    }

    public class RegisterLoginPagerAdapter extends FragmentPagerAdapter {
        Application app;

        public RegisterLoginPagerAdapter(FragmentManager fm, Application app) {
            super(fm);
            this.app = app;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch(i) {
                case 0:		fragment = new InscriptionFragment();  	break;
                case 1:		fragment = new LoginFragment(); 	    break;
                default:	fragment = new InscriptionFragment();	break;
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
                case 0:		title = "REGISTER";		break;
                case 1:		title = "LOGIN";		break;
            }
            return title;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        Fragment fr = listFragment[getCurrentFragmentIndex()];
        if(fr instanceof LoginFragment)
        {
            LoginFragment fr_ = (LoginFragment) fr;
            fr_.onActivityResult(requestCode, responseCode, intent);
        }
    }

}
