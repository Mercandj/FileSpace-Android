/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.activity;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Stack;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.fragment.FileManagerFragment;
import mercandalli.com.jarvis.fragment.Fragment;
import mercandalli.com.jarvis.fragment.HomeFragment;
import mercandalli.com.jarvis.fragment.InformationFragment;
import mercandalli.com.jarvis.fragment.RequestFragment;
import mercandalli.com.jarvis.fragment.RoboticsFragment;
import mercandalli.com.jarvis.fragment.SettingsFragment;
import mercandalli.com.jarvis.fragment.UserFragment;
import mercandalli.com.jarvis.fragment.WebFragment;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.navdrawer.NavDrawerItem;
import mercandalli.com.jarvis.navdrawer.NavDrawerItemListe;
import mercandalli.com.jarvis.navdrawer.NavDrawerListAdapter;

public abstract class ApplicationDrawer extends Application {
	
	public static final int[] noSelectable 	= new int[] {Const.TAB_VIEW_TYPE_PROFIL, Const.TAB_VIEW_TYPE_SECTION, Const.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE};

    public Fragment fragment;
    private final int INIT_ID_FRAGMENT = 2;
    private Stack<Integer> ID_FRAGMENT_VISITED = new Stack<>();
    
	protected DrawerLayout mDrawerLayout;
	protected ListView mDrawerList;
	protected NavDrawerItemListe navDrawerItems;
	protected ActionBarDrawerToggle mDrawerToggle;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout 	= (DrawerLayout) 	findViewById(R.id.drawer_layout);
        mDrawerList 	= (ListView) 		findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItems = new NavDrawerItemListe();
        
        // Tab 0
        navDrawerItems.add(
        		new NavDrawerItem( this.getConfig().getUserUsername(), this.getConfig().getUrlServer(), R.drawable.ic_launcher, Const.TAB_VIEW_TYPE_PROFIL)
        );
        
        // Tab 1
        navDrawerItems.add(
        		new NavDrawerItem( "", R.drawable.ic_launcher, Const.TAB_VIEW_TYPE_SECTION)
        );

        // Tab 2
        navDrawerItems.add(
                new NavDrawerItem( "Home", new IListener() {
                    @Override
                    public void execute() {
                        fragment = new HomeFragment(ApplicationDrawer.this);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                }, Const.TAB_VIEW_TYPE_NORMAL)
        );
     
        // Tab 3
        navDrawerItems.add(
        		new NavDrawerItem( "Files", new IListener() {
						@Override
						public void execute() {
							fragment = new FileManagerFragment(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						}
			        }, Const.TAB_VIEW_TYPE_NORMAL)
        );

        // Tab 4
        navDrawerItems.add(
                new NavDrawerItem( "Talks", new IListener() {
                    @Override
                    public void execute() {
                        fragment = new UserFragment(ApplicationDrawer.this);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                }, Const.TAB_VIEW_TYPE_NORMAL)
        );

        // Tab 5
        navDrawerItems.add(
                new NavDrawerItem( "Robotics", new IListener() {
                    @Override
                    public void execute() {
                        fragment = new RoboticsFragment(ApplicationDrawer.this);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                }, Const.TAB_VIEW_TYPE_NORMAL)
        );
        
        // Tab 6
        navDrawerItems.add(
        		new NavDrawerItem( "Informations", new IListener() {
						@Override
						public void execute() {
							fragment = new InformationFragment(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						}
			        }, Const.TAB_VIEW_TYPE_NORMAL)
        );
        
        // Tab 7
        navDrawerItems.add(
        		new NavDrawerItem( "Request", new IListener() {
						@Override
						public void execute() {
							fragment = new RequestFragment(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						}
			            }, Const.TAB_VIEW_TYPE_NORMAL)
        );
        
        // Tab 8
        navDrawerItems.add(
        		new NavDrawerItem( "", R.drawable.ic_launcher, Const.TAB_VIEW_TYPE_SECTION)
        );
        
        // Tab 9
        navDrawerItems.add(
        		new NavDrawerItem( 
    				"Settings",
    				new IListener() {
						@Override
						public void execute() {
							fragment = new SettingsFragment(ApplicationDrawer.this);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						}
                },
                R.drawable.ic_settings_grey,
                Const.TAB_VIEW_TYPE_SETTING)
        );

        // Tab 10
        navDrawerItems.add(
                new NavDrawerItem(
                        "Log out",
                        new IListener() {
                            @Override
                            public void execute() {
                                ApplicationDrawer.this.alert("Log out", "Do you want to log out?", "Yes", new IListener() {
                                    @Override
                                    public void execute() {
                                        ApplicationDrawer.this.getConfig().reset();
                                        ApplicationDrawer.this.finish();
                                    }
                                }, "Cancel", null);
                            }
                        },
                        R.drawable.ic_log_out,
                        Const.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE)
        );
        
        // Tab 11
        navDrawerItems.add(
        		new NavDrawerItem(
        			"About Dev",
        			new IListener() {
						@Override
						public void execute() {
							fragment = new WebFragment(ApplicationDrawer.this, ApplicationDrawer.this.getConfig().aboutURL);
					        FragmentManager fragmentManager = getFragmentManager();
					        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						}
                            },
        			R.drawable.ic_help_grey,
        			Const.TAB_VIEW_TYPE_SETTING)
        		);
        
        // Initial Fragment
        selectItem(INIT_ID_FRAGMENT);
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
        if(position == INIT_ID_FRAGMENT) {
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

        invalidateOptionsMenu();
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
    	
    	MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        	@Override
            public boolean onQueryTextChange(String query) {
        		if(query == null)
            		return true;
            	if(query.replaceAll(" ", "").equals("")) {
            		if(fragment instanceof FileManagerFragment) {
                		FileManagerFragment tmp_fragment = (FileManagerFragment) fragment;    		
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
            	if(fragment instanceof FileManagerFragment) {
            		FileManagerFragment tmp_fragment = (FileManagerFragment) fragment;    		
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
        menu.findItem(R.id.action_sort)	    .setVisible(false);
		
    	if(fragment instanceof RequestFragment) {
    		menu.findItem(R.id.action_delete)	.setVisible(!drawerOpen);
    	}
    	else if(fragment instanceof FileManagerFragment) {
            menu.findItem(R.id.action_search)	.setVisible(!drawerOpen);
            menu.findItem(R.id.action_sort)	    .setVisible(!drawerOpen);
    		FileManagerFragment tmp_fragment = (FileManagerFragment) fragment;    		
    		switch(tmp_fragment.getCurrentFragmentIndex()) {
    		case 0:
    			menu.findItem(R.id.action_download)	.setVisible(!drawerOpen);
    			break;
            case 1:
                menu.findItem(R.id.action_download)	.setVisible(!drawerOpen);
                break;
    		case 2:
    			menu.findItem(R.id.action_upload)	.setVisible(!drawerOpen);
    			break;
    		}
    	}
        return super.onPrepareOptionsMenu(menu);
    }
    
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
        if(this.fragment!=null) {
            if(this.fragment.back())
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
        if(this.ID_FRAGMENT_VISITED.pop() == INIT_ID_FRAGMENT)
            return false;
        this.selectItem(this.ID_FRAGMENT_VISITED.pop());
        return true;
    }
}
