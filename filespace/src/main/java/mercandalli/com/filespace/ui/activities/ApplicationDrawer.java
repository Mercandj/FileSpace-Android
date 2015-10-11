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
package mercandalli.com.filespace.ui.activities;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Stack;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.multimedia.MultimediaFragment;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.ui.fragments.HomeFragment;
import mercandalli.com.filespace.ui.fragments.ProfileFragment;
import mercandalli.com.filespace.ui.fragments.RoboticsFragment;
import mercandalli.com.filespace.ui.fragments.SettingsFragment;
import mercandalli.com.filespace.ui.fragments.WebFragment;
import mercandalli.com.filespace.ui.fragments.admin.AdminFragment;
import mercandalli.com.filespace.ui.fragments.community.CommunityFragment;
import mercandalli.com.filespace.ui.fragments.file.FileManagerFragment;
import mercandalli.com.filespace.ui.fragments.genealogy.GenealogyFragment;
import mercandalli.com.filespace.ui.fragments.workspace.WorkspaceFragment;
import mercandalli.com.filespace.ui.navdrawer.NavDrawerItem;
import mercandalli.com.filespace.ui.navdrawer.NavDrawerItemList;
import mercandalli.com.filespace.ui.navdrawer.NavDrawerListAdapter;

public abstract class ApplicationDrawer extends Application implements INavigationDrawerActivity {
	
	public static final int[] noSelectable 	= new int[] {Const.TAB_VIEW_TYPE_SECTION, Const.TAB_VIEW_TYPE_SECTION_TITLE, Const.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE};

    public BackFragment backFragment;
    private final int INIT_FRAGMENT_ID = 3;
    private final int INIT_FRAGMENT_LOGGED_ID = 3;
    private Stack<Integer> ID_FRAGMENT_VISITED = new Stack<>();

	protected DrawerLayout mDrawerLayout;
	protected ListView mDrawerList;
	protected NavDrawerItemList navDrawerItems;
	protected ActionBarDrawerToggle mDrawerToggle;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout 	= (DrawerLayout) 	findViewById(R.id.activity_main_drawer_layout);
        mDrawerList 	= (ListView) 		findViewById(R.id.activity_main_left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItems = new NavDrawerItemList();
        
        // Tab 0
        navDrawerItems.add(
        		new NavDrawerItem( this.getConfig().getUserUsername(), "Edit your profile", new IListener() {
                    @Override
                    public void execute() {
                        if(isLogged()) {
                            backFragment = ProfileFragment.newInstance();
                            backFragment.setApp(ApplicationDrawer.this);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                        }
                    }
                }, R.drawable.ic_user, R.color.notifications_bar, Const.TAB_VIEW_TYPE_PROFILE)
        );
        
        // Tab 1
        navDrawerItems.add(
        		new NavDrawerItem( "", R.drawable.ic_launcher, Const.TAB_VIEW_TYPE_SECTION)
        );

        // Tab 2
        navDrawerItems.add(
                new NavDrawerItem(getString(R.string.tab_home), new IListener() {
                    @Override
                    public void execute() {
                        backFragment = HomeFragment.newInstance();
                        backFragment.setApp(ApplicationDrawer.this);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                    }
                }, R.drawable.q_ic_drawer_home, R.drawable.q_ic_drawer_home_pressed, Const.TAB_VIEW_TYPE_NORMAL)
        );
     
        // Tab 3
        navDrawerItems.add(
        		new NavDrawerItem(getString(R.string.tab_files), new IListener() {
						@Override
						public void execute() {
							backFragment = FileManagerFragment.newInstance();
                            backFragment.setApp(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
						}
			    }, R.drawable.q_ic_drawer_files, R.drawable.q_ic_drawer_files_pressed, Const.TAB_VIEW_TYPE_NORMAL)
        );

        navDrawerItems.add(
                new NavDrawerItem("Test", new IListener() {
                    @Override
                    public void execute() {
                        backFragment = MultimediaFragment.newInstance();
                        backFragment.setApp(ApplicationDrawer.this);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                    }
                }, R.drawable.q_ic_drawer_home, R.drawable.q_ic_drawer_home_pressed, Const.TAB_VIEW_TYPE_NORMAL)
        );

        // Tab 4
        navDrawerItems.add(
                new NavDrawerItem(getString(R.string.tab_workspace), new IListener() {
                    @Override
                    public void execute() {
                        backFragment = new WorkspaceFragment();
                        backFragment.setApp(ApplicationDrawer.this);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                    }
                }, R.drawable.q_ic_drawer_workspace, R.drawable.q_ic_drawer_workspace_pressed, Const.TAB_VIEW_TYPE_NORMAL)
        );

        // User logged Tabs
        if(this.getConfig().isLogged()) {
            // Tab 5
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_community), new IListener() {
                        @Override
                        public void execute() {
                            backFragment = CommunityFragment.newInstance();
                            backFragment.setApp(ApplicationDrawer.this);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_community, R.drawable.q_ic_drawer_community_pressed, Const.TAB_VIEW_TYPE_NORMAL)
            );
        }

        // Admin Tabs
        if(this.getConfig().getUser().isAdmin()) {
            // Tab 6
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_robotics), new IListener() {
                        @Override
                        public void execute() {
                            backFragment = RoboticsFragment.newInstance();
                            backFragment.setApp(ApplicationDrawer.this);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_robotics, R.drawable.q_ic_drawer_robotics_pressed, Const.TAB_VIEW_TYPE_NORMAL)
            );

            // Tab 7
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_genealogy), new IListener() {
                        @Override
                        public void execute() {
                            backFragment = new GenealogyFragment();
                            backFragment.setApp(ApplicationDrawer.this);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_robotics, R.drawable.q_ic_drawer_robotics_pressed, Const.TAB_VIEW_TYPE_NORMAL)
            );

            // Tab 8
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_admin), new IListener() {
                        @Override
                        public void execute() {
                            backFragment = new AdminFragment();
                            backFragment.setApp(ApplicationDrawer.this);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_data, R.drawable.q_ic_drawer_data_pressed, Const.TAB_VIEW_TYPE_NORMAL)
            );
        }
        
        // Tab 9
        navDrawerItems.add(
        		new NavDrawerItem( "", R.drawable.ic_launcher, Const.TAB_VIEW_TYPE_SECTION)
        );

        // Tab 10
        navDrawerItems.add(
                new NavDrawerItem( "Other", R.drawable.ic_launcher, Const.TAB_VIEW_TYPE_SECTION_TITLE)
        );
        
        // Tab 11
        navDrawerItems.add(
        		new NavDrawerItem( 
    				getString(R.string.tab_settings),
    				new IListener() {
						@Override
						public void execute() {
							backFragment = SettingsFragment.newInstance();
                            backFragment.setApp(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
						}
                },
                R.drawable.ic_settings_grey,
                Const.TAB_VIEW_TYPE_SETTING)
        );

        // Tab 12
        if(this.getConfig().isLogged()) {
            navDrawerItems.add(
                    new NavDrawerItem(
                            getString(R.string.tab_log_out),
                            new IListener() {
                                @Override
                                public void execute() {
                                    ApplicationDrawer.this.alert("Log out", "Do you want to log out?", "Yes", new IListener() {
                                        @Override
                                        public void execute() {
                                            ApplicationDrawer.this.getConfig().reset();
                                            ApplicationDrawer.this.finish();
                                        }
                                    }, getString(R.string.cancel), null);
                                }
                            },
                            R.drawable.ic_log_out,
                            Const.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE)
            );
        }
        
        // Tab 13
        navDrawerItems.add(
        		new NavDrawerItem(
        			getString(R.string.tab_about),
        			new IListener() {
						@Override
						public void execute() {
                            WebFragment fr = WebFragment.newInstance();
                            fr.setApp(ApplicationDrawer.this);
                            fr.setInitURL(ApplicationDrawer.this.getConfig().aboutURL);
                            backFragment = fr;
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, backFragment).commit();
						}
                            },
        			R.drawable.ic_help_grey,
        			Const.TAB_VIEW_TYPE_SETTING)
        		);
        
        // Initial Fragment
        selectItem(getInitFragmentId());
        /*
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.transparent);
        */

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
	
	public void selectItem(int position) {
        if(position >= navDrawerItems.size())
            return;
		for(NavDrawerItem nav : navDrawerItems.getListe())
			if(navDrawerItems.get(position).equals(nav)) {
                for (int i : noSelectable)
                    if (nav.viewType == i) {
                        if (nav.listenerClick != null)
                            nav.listenerClick.execute();
                        return;
                    }
            }
        if(position == getInitFragmentId()) {
            ID_FRAGMENT_VISITED = new Stack<>();
        }
        ID_FRAGMENT_VISITED.push(position);

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

        //invalidateOptionsMenu();
    }

    /*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarColor(int idColor) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, idColor));
            if (navDrawerItems != null) {
                if (navDrawerItems.size() > 0) {
                    navDrawerItems.get(0).idBackgroundColor = idColor;
                    synchronized (navDrawerItems.get(0)) {
                        navDrawerItems.get(0).notify();
                    }
                }
            }
        }
    }
    */

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
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        switch (item.getItemId()) {
        case android.R.id.home:
        	if (drawerOpen) mDrawerLayout.closeDrawer(mDrawerList);
        	else 			mDrawerLayout.openDrawer(mDrawerList);
        	return true;
        case R.id.action_delete:
        	if(backFragment instanceof AdminFragment) {
                AdminFragment adminFragment = (AdminFragment) backFragment;
                if (adminFragment.getCurrentFragmentIndex() == 6)
                    ((RequestFragment) AdminFragment.LIST_BACK_FRAGMENT[adminFragment.getCurrentFragmentIndex()]).delete();
            }
            else if(backFragment instanceof WorkspaceFragment) {
                WorkspaceFragment workspaceFragment = (WorkspaceFragment) backFragment;
                int currentIndex = workspaceFragment.getCurrentFragmentIndex();
                switch (currentIndex) {
                    case 0:     ((NoteFragment) WorkspaceFragment.LIST_BACK_FRAGMENT[currentIndex]).delete();     break;
                    case 1:     ((CryptFragment) WorkspaceFragment.LIST_BACK_FRAGMENT[currentIndex]).delete();    break;
                }
            }
        	return true;
	    case R.id.action_add:
	    	if (backFragment instanceof FileManagerFragment)
	    		((FileManagerFragment) backFragment).add();
	    	return true;
	    case R.id.action_download:
	    	if (backFragment instanceof FileManagerFragment)
	    		((FileManagerFragment) backFragment).download();
	    	return true;
	    case R.id.action_upload:
	    	if (backFragment instanceof FileManagerFragment)
	    		((FileManagerFragment) backFragment).upload();
            return true;
        case R.id.action_home:
            if (backFragment instanceof FileManagerFragment)
                ((FileManagerFragment) backFragment).goHome();
	    	return true;
        case R.id.action_sort:
            if (backFragment instanceof FileManagerFragment)
                ((FileManagerFragment) backFragment).sort();
            return true;
	    }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main, menu);     
    	
    	MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        	@Override
            public boolean onQueryTextChange(String query) {
        		if (query == null) {
                    return true;
                }
            	if (query.replaceAll(" ", "").equals("")) {
            		if (backFragment instanceof FileManagerFragment) {
                		FileManagerFragment tmp_fragment = (FileManagerFragment) backFragment;
                		switch(tmp_fragment.getCurrentFragmentIndex()) {
                            case 0: tmp_fragment.refreshListServer(); break;
                            case 1: tmp_fragment.refreshListServer(); break;
                            case 2: tmp_fragment.refreshListServer(); break;
                		}
                	}
                    else if (backFragment instanceof GenealogyFragment) {
                        GenealogyFragment tmp_fragment = (GenealogyFragment) backFragment;
                        switch(tmp_fragment.getCurrentFragmentIndex()) {
                            case 0: tmp_fragment.refreshListServer(); break;
                            case 1: tmp_fragment.refreshListServer(); break;
                            case 2: tmp_fragment.refreshListServer(); break;
                        }
                    }
            	}
            	return true;
            }
        	
            @Override
            public boolean onQueryTextSubmit(String query) {
            	if(query == null)
            		return false;
            	if(query.replaceAll(" ", "").equals(""))
            		return false;
            	if(backFragment instanceof FileManagerFragment) {
            		FileManagerFragment tmp_fragment = (FileManagerFragment) backFragment;
            		switch(tmp_fragment.getCurrentFragmentIndex()) {
                        case 0: tmp_fragment.refreshListServer(query); break;
                        case 1: tmp_fragment.refreshListServer(query); break;
                        case 2: tmp_fragment.refreshListServer(query); break;
            		}
            	}
                else if(backFragment instanceof GenealogyFragment) {
                    GenealogyFragment tmp_fragment = (GenealogyFragment) backFragment;
                    switch(tmp_fragment.getCurrentFragmentIndex()) {
                        case 0: tmp_fragment.refreshListServer(query); break;
                        case 1: tmp_fragment.refreshListServer(query); break;
                        case 2: tmp_fragment.refreshListServer(query); break;
                    }
                }
				return false;
            }            
        };

        if(mSearchView!=null)
            mSearchView.setOnQueryTextListener(queryTextListener);
    	
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    	
    	menu.findItem(R.id.action_search)	.setVisible(false);
    	menu.findItem(R.id.action_delete)	.setVisible(false);
		menu.findItem(R.id.action_add)		.setVisible(false);
		menu.findItem(R.id.action_download)	.setVisible(false);
		menu.findItem(R.id.action_upload)	.setVisible(false);
        menu.findItem(R.id.action_home) 	.setVisible(false);
        menu.findItem(R.id.action_sort)	    .setVisible(false);
		
    	if(backFragment instanceof AdminFragment) {
            if(((AdminFragment) backFragment).getCurrentFragmentIndex() == 6)
    		    menu.findItem(R.id.action_delete)	.setVisible(!drawerOpen);
    	}
        else if(backFragment instanceof WorkspaceFragment) {
            if( ((WorkspaceFragment) backFragment).getCurrentFragmentIndex() < 2 )
                menu.findItem(R.id.action_delete)	.setVisible(!drawerOpen);
        }
    	else if(backFragment instanceof FileManagerFragment) {
            menu.findItem(R.id.action_search)	.setVisible(!drawerOpen);
            menu.findItem(R.id.action_sort)	    .setVisible(!drawerOpen);
    		FileManagerFragment tmp_fragment = (FileManagerFragment) backFragment;
    		switch(tmp_fragment.getCurrentFragmentIndex()) {
    		case 0:
    			menu.findItem(R.id.action_download)	.setVisible(!drawerOpen);
    			break;
            case 1:
                menu.findItem(R.id.action_download)	.setVisible(!drawerOpen);
                break;
    		case 2:
    			menu.findItem(R.id.action_home)	.setVisible(!drawerOpen);
    			break;
    		}
    	}
        else if(backFragment instanceof GenealogyFragment) {
            GenealogyFragment tmp_fragment = (GenealogyFragment) backFragment;
            if(tmp_fragment.getCurrentFragmentIndex() == 0) {
                menu.findItem(R.id.action_search)	.setVisible(!drawerOpen);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
    */
    
    public abstract void updateAdapters();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            if(backPressed())
                return true;
        return super.onKeyDown(keyCode, event);
    }

    public boolean backPressed() {
        if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return true;
        }
        if(this.backFragment !=null) {
            if(this.backFragment.back())
                return true;
        }
        if(this.ID_FRAGMENT_VISITED==null) {
            Log.e("ApplicationDrawer", "backPressed() this.ID_FRAGMENT_VISITED==null");
            return false;
        }
        if(this.ID_FRAGMENT_VISITED.empty()) {
            Log.e("ApplicationDrawer", "backPressed() this.ID_FRAGMENT_VISITED.empty()");
            return false;
        }
        if(this.ID_FRAGMENT_VISITED.pop() == getInitFragmentId())
            return false;
        this.selectItem(this.ID_FRAGMENT_VISITED.pop());
        return true;
    }

    private int getInitFragmentId() {
        return isLogged()?INIT_FRAGMENT_LOGGED_ID:INIT_FRAGMENT_ID;
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }
}
