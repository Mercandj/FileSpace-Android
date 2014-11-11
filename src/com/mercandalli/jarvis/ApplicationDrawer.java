/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mercandalli.jarvis.fragment.FileManagerFragment;
import com.mercandalli.jarvis.fragment.RequestFragment;
import com.mercandalli.jarvis.fragment.SettingsFragment;
import com.mercandalli.jarvis.fragment.WebFragment;
import com.mercandalli.jarvis.listener.IListener;
import com.mercandalli.jarvis.navdrawer.NavDrawerItem;
import com.mercandalli.jarvis.navdrawer.NavDrawerItemListe;
import com.mercandalli.jarvis.navdrawer.NavDrawerListAdapter;

public class ApplicationDrawer extends Application {	
	
	public static final int TYPE_PROFIL		= 0;
	public static final int TYPE_NORMAL	 	= 1;
	public static final int TYPE_SECTION	= 2;
	public static final int TYPE_SETTING	= 3;
	
	public static final int[] noSelectable 	= new int[] {TYPE_PROFIL, TYPE_SECTION};
    
    Fragment fragment;
    
	protected DrawerLayout 				mDrawerLayout;
	protected ListView 					mDrawerList;
	protected NavDrawerItemListe 		navDrawerItems;
	protected ActionBarDrawerToggle 	mDrawerToggle;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        mDrawerLayout 	= (DrawerLayout) 	findViewById(R.id.drawer_layout);
        mDrawerList 	= (ListView) 		findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItems = new NavDrawerItemListe();
        
        // Tab 0
        navDrawerItems.add(
        		new NavDrawerItem( config.getUserUsername(), config.getUrlServer(), R.drawable.ic_launcher, TYPE_PROFIL)
        		);
        
        // Tab 1
        navDrawerItems.add(
        		new NavDrawerItem( "", R.drawable.ic_launcher, TYPE_SECTION)
        		);
     
        // Tab 2
        navDrawerItems.add(
        		new NavDrawerItem( "Explore", new IListener() {
						@Override
						public void execute() {
							fragment = new FileManagerFragment(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						}
			        }, TYPE_NORMAL)
        		);
        
        // Tab 3
        navDrawerItems.add(
        		new NavDrawerItem( "Request", new IListener() {
						@Override
						public void execute() {
							fragment = new RequestFragment(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						}
			        }, TYPE_NORMAL)
        		);
        
        // Tab 4
        navDrawerItems.add(
        		new NavDrawerItem( "", R.drawable.ic_launcher, TYPE_SECTION)
        		);
        
        // Tab 5
        navDrawerItems.add(
        		new NavDrawerItem( "Settings", new IListener() {
					@Override
					public void execute() {
						fragment = new SettingsFragment(ApplicationDrawer.this);
				        FragmentManager fragmentManager = getFragmentManager();
				        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
					}
		        }, TYPE_SETTING)
        		);
        
        // Tab 6
        navDrawerItems.add(
        		new NavDrawerItem( "About Dev", new IListener() {
					@Override
					public void execute() {
						fragment = new WebFragment(ApplicationDrawer.this, ApplicationDrawer.this.config.aboutURL);
				        FragmentManager fragmentManager = getFragmentManager();
				        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
					}
		        }, TYPE_SETTING)
        		);
        
        // Initial Fragment
        selectItem(2);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.transparent);
        
        mDrawerList.setAdapter(new NavDrawerListAdapter(this, navDrawerItems.getListe()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
            	super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
            	super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	private void selectItem(int position) {
		for(NavDrawerItem nav : navDrawerItems.getListe())
			if(navDrawerItems.get(position).equals(nav))
				for(int i : noSelectable)
					if(nav.SLIDING_MENU_TAB == i)
						return;
    	for(NavDrawerItem nav : navDrawerItems.getListe()) {
    		nav.isSelected = false;
    		if(navDrawerItems.get(position).equals(nav)) {
    			nav.isSelected = true;
    			if(nav.listenerClick!=null)
    				nav.listenerClick.execute();
    		}
    	}
        mDrawerLayout.closeDrawer(mDrawerList);
        mDrawerList.setAdapter(new NavDrawerListAdapter(this, navDrawerItems.getListe()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }
    
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        switch (item.getItemId()) {
        case android.R.id.home:
        	if(drawerOpen) 	mDrawerLayout.closeDrawer(mDrawerList);
        	else 			mDrawerLayout.openDrawer(mDrawerList);
        	return true;
        case R.id.action_delete:
        	if(fragment instanceof RequestFragment)
        		((RequestFragment)fragment).deleteConsole();
        	return true;
	    case R.id.action_add:
	    	if(fragment instanceof FileManagerFragment)
	    		((FileManagerFragment)fragment).add();
	    	return true;
	    case R.id.action_download:
	    	if(fragment instanceof FileManagerFragment)
	    		((FileManagerFragment)fragment).download();
	    	return true;
	    case R.id.action_upload:
	    	if(fragment instanceof FileManagerFragment)
	    		((FileManagerFragment)fragment).upload();
	    	return true;
	    }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main, menu);        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    	
    	menu.findItem(R.id.action_delete)	.setVisible(false);
		menu.findItem(R.id.action_add)		.setVisible(false);
		menu.findItem(R.id.action_download)	.setVisible(false);
		menu.findItem(R.id.action_upload)	.setVisible(false);
		
    	if(fragment instanceof RequestFragment) {
    		menu.findItem(R.id.action_delete)	.setVisible(!drawerOpen);
    	}
    	else if(fragment instanceof FileManagerFragment) {
    		FileManagerFragment tmp_fragment = (FileManagerFragment) fragment;    		
    		switch(tmp_fragment.getCurrentFragmentIndex()) {
    		case 0:
    			menu.findItem(R.id.action_add)		.setVisible(!drawerOpen);
    			menu.findItem(R.id.action_download)	.setVisible(!drawerOpen);
    			break;
    		case 1:
    			menu.findItem(R.id.action_upload)	.setVisible(!drawerOpen);
    			break;
    		}
    	}
        return super.onPrepareOptionsMenu(menu);
    }
}
