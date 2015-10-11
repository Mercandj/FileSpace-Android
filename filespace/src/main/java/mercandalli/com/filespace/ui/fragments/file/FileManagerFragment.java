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
package mercandalli.com.filespace.ui.fragments.file;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.ui.activities.Application;
import mercandalli.com.filespace.ui.dialogs.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.ui.fragments.FabFragment;

import static mercandalli.com.filespace.utils.NetUtils.isInternetConnection;

public class FileManagerFragment extends BackFragment implements ViewPager.OnPageChangeListener {
	
	private static final int NB_FRAGMENT = 4;
    private static final int INIT_FRAGMENT = 1;
	public static FabFragment listFragment[] = new FabFragment[NB_FRAGMENT];
	private ViewPager mViewPager;
	private FileManagerFragmentPagerAdapter mPagerAdapter;
    private TabLayout tabs;

    private FloatingActionButton circle, circle2;
    private View coordinatorLayoutView;
    private Snackbar snackbar;

    private AppBarLayout mAppBarLayout;

    protected Toolbar mToolbar;

	protected int mViewMode = Const.MODE_LIST;

    public static FileManagerFragment newInstance() {
        Bundle args = new Bundle();
        FileManagerFragment fragment = new FileManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_file_manager, container, false);

        app.setTitle(R.string.tab_files);

        mToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        app.setToolbar(mToolbar);
        setHasOptionsMenu(true);

        mAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.fragment_file_manager_app_bar_layout);
        coordinatorLayoutView = (View) rootView.findViewById(R.id.fragment_file_manager_coordinator_layout);

		mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager(), app);

        mViewMode = ((app.getConfig().getUserFileModeView() > -1) ? app.getConfig().getUserFileModeView() : Const.MODE_LIST);

        tabs = (TabLayout) rootView.findViewById(R.id.fragment_file_manager_tab_layout);
		mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_file_manager_view_pager);
		mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        if(app.isLogged()) {
            if (isInternetConnection(app)) {
                mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
                mViewPager.setCurrentItem(INIT_FRAGMENT);
            } else {
                mViewPager.setOffscreenPageLimit(NB_FRAGMENT);
                mViewPager.setCurrentItem(INIT_FRAGMENT + 1);
            }
        }
        else {
            mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
            mViewPager.setCurrentItem(0);
        }

        tabs.setupWithViewPager(mViewPager);

        this.circle = ((FloatingActionButton) rootView.findViewById(R.id.circle));
        this.circle.setVisibility(View.GONE);
        this.circle2 = ((FloatingActionButton) rootView.findViewById(R.id.circle2));
        this.circle2.setVisibility(View.GONE);
		
        return rootView;
	}
	
	public int getCurrentFragmentIndex() {
        if(mViewPager == null)
            return -1;
        int result = mViewPager.getCurrentItem();
		if(result >= listFragment.length)
            return -1;
        return mViewPager.getCurrentItem() + (app.isLogged()?0:2);
	}

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if(listFragment == null || currentFragmentId== -1)
            return false;
        FabFragment fragment = listFragment[currentFragmentId];
        if(fragment==null)
            return false;
        refreshFab(fragment);
        return fragment.back();
    }

    private void refreshFab() {
        refreshFab(getCurrentFragmentIndex());
    }

    private void refreshFab(int currentFragmentId) {
        if(listFragment == null || currentFragmentId== -1)
            return;
        FabFragment fragment = listFragment[currentFragmentId];
        if(fragment==null)
            return;
        refreshFab(fragment);
    }

    private void refreshFab(final FabFragment currentFragment) {
        if (circle == null) {
            return;
        }
        int imageResource;
        if (currentFragment.isFabVisible(0)) {
            circle.show();
            imageResource = currentFragment.getFabImageResource(0);
            if(imageResource == -1)
                imageResource = android.R.drawable.ic_input_add;
            circle.setImageResource(imageResource);
            circle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFragment.onFabClick(0, circle);
                }
            });
        }
        else
            circle.hide();

        if (circle2 == null) {
            return;
        }
        if(currentFragment.isFabVisible(1)) {
            circle2.show();
            imageResource = currentFragment.getFabImageResource(1);
            if (imageResource == -1)
                imageResource = android.R.drawable.ic_input_add;
            circle2.setImageResource(imageResource);
            circle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFragment.onFabClick(1, circle2);
                }
            });
        }
        else
            circle2.hide();
    }

    @Override
    public void onFocus() { }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onPageSelected(int position) {
        FileManagerFragment.this.app.invalidateOptionsMenu();
        mAppBarLayout.setExpanded(true);
        if(app.isLogged()) {
            switch (position) {
                case 0:
                    updateNoInternet();
                    break;
                case 1:
                    updateNoInternet();
                    break;
                default:
                    if (snackbar != null)
                        snackbar.dismiss();
            }
            refreshFab(position);
        }
        else
            refreshFab(position+2);
    }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
		Application app;
		
		public FileManagerFragmentPagerAdapter(FragmentManager fm, Application app) {
			super(fm);
			this.app = app;
		}
		
		@Override
        public BackFragment getItem(int i) {
            if(!app.isLogged())
                i+=2;

			FabFragment fragment;
			switch(i) {
                case 0:		fragment = FileManagerCloudFragment.newInstance();  	break;
                case 1:		fragment = FileManagerMyCloudFragment.newInstance(); 	break;
                case 2:		fragment = FileManagerLocalFragment.newInstance();	    break;
                case 3:		fragment = FileManagerLocalMusicFragment.newInstance();	break;
                default:	fragment = FileManagerLocalFragment.newInstance();	    break;
			}
            fragment.setRefreshFab(new IListener() {
                @Override
                public void execute() {
                    refreshFab();
                }
            });
			listFragment[i] = fragment;
            return listFragment[i];
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT - (app.isLogged()?0:2);
        }

        @Override
        public CharSequence getPageTitle(int i) {
            if(!app.isLogged())
                i+=2;
        	String title = "null";
			switch(i) {
                case 0:	title = getString(R.string.file_fragment_public_cloud);	break;
                case 1:	title = getString(R.string.file_fragment_my_cloud);		break;
                case 2:	title = getString(R.string.file_fragment_local);		break;
                case 3:	title = getString(R.string.file_fragment_music);	    break;
			}
			return title;
        }
    }
	

	public void refreshListServer() {
		refreshListServer(null);
	}
	
	public void refreshListServer(String search) {
        for(FabFragment fr : listFragment) {
            if(fr!=null) {
                if (fr instanceof FileManagerCloudFragment) {
                    FileManagerCloudFragment fragmentFileManagerFragment = (FileManagerCloudFragment) fr;
                    fragmentFileManagerFragment.refreshList(search);
                }
                else if (fr instanceof FileManagerMyCloudFragment) {
                    FileManagerMyCloudFragment fragmentFileManagerFragment = (FileManagerMyCloudFragment) fr;
                    fragmentFileManagerFragment.refreshList(search);
                }
                else if (fr instanceof FileManagerLocalFragment) {
                    FileManagerLocalFragment fragmentFileManagerFragment = (FileManagerLocalFragment) fr;
                    fragmentFileManagerFragment.refreshList(search);
                }
            }
        }
	}
	
	public void updateAdapterListServer() {

        for(FabFragment fr : listFragment) {
            if(fr!=null) {
                if (fr instanceof FileManagerCloudFragment) {
                    FileManagerCloudFragment fragmentFileManagerFragment = (FileManagerCloudFragment) fr;
                    fragmentFileManagerFragment.updateAdapter();
                }
                else if (fr instanceof FileManagerMyCloudFragment) {
                    FileManagerMyCloudFragment fragmentFileManagerFragment = (FileManagerMyCloudFragment) fr;
                    fragmentFileManagerFragment.updateAdapter();
                }
                else if (fr instanceof FileManagerLocalFragment) {
                    FileManagerLocalFragment fragmentFileManagerFragment = (FileManagerLocalFragment) fr;
                    fragmentFileManagerFragment.updateAdapter();
                }
            }
        }
	}
	
	public void refreshAdapterListServer() {
        for(FabFragment fr : listFragment) {
            if(fr!=null) {
                if (fr instanceof FileManagerCloudFragment) {
                    FileManagerCloudFragment fragmentFileManagerFragment = (FileManagerCloudFragment) fr;
                    fragmentFileManagerFragment.refreshList();
                }
                if (fr instanceof FileManagerMyCloudFragment) {
                    FileManagerMyCloudFragment fragmentFileManagerFragment = (FileManagerMyCloudFragment) fr;
                    fragmentFileManagerFragment.refreshList();
                }
                if (fr instanceof FileManagerLocalFragment) {
                    FileManagerLocalFragment fragmentFileManagerFragment = (FileManagerLocalFragment) fr;
                    fragmentFileManagerFragment.refreshList();
                }
            }
        }
	}

	public void add() {
		app.dialog = new DialogAddFileManager(app, -1, new IPostExecuteListener() {
			@Override
			public void execute(JSONObject json, String body) {
				if (json != null)
					refreshListServer();
			}
		}, new IListener() { // Dismiss
			@Override
			public void execute() {

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

    public void goHome() {
        if(listFragment.length>2)
            if(listFragment[2]!=null)
                if(listFragment[2] instanceof FileManagerLocalFragment) {
                    FileManagerLocalFragment fragmentFileManagerFragment = (FileManagerLocalFragment) listFragment[2];
                    fragmentFileManagerFragment.goHome();
                }
    }

	public void sort() {
		final AlertDialog.Builder menuAlert = new AlertDialog.Builder(app);
		String[] menuList = { "Sort by name (A-Z)", "Sort by size", "Sort by date", app.getConfig().getUserFileModeView()== Const.MODE_LIST ? "Grid View" : "List View" };
        menuAlert.setTitle(getString(R.string.view));
        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {

                            case 0:
                            case 1:
                            case 2:
                                if (getCurrentFragmentIndex() != 2 && getCurrentFragmentIndex() != 3)
                                    Toast.makeText(app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                else {
                                    for (BackFragment fr : listFragment) {
                                        if (fr != null) {
                                            if (fr instanceof ISortMode) {
                                                ((ISortMode) fr).setSortMode(item == 0 ? Const.SORT_ABC : (item == 1 ? Const.SORT_SIZE : Const.SORT_DATE_MODIFICATION));
                                            }
                                        }
                                    }
                                }
                                break;

                            case 3:
                                if (mViewMode == Const.MODE_LIST)
                                    mViewMode = Const.MODE_GRID;
                                else
                                    mViewMode = Const.MODE_LIST;
                                app.getConfig().setUserFileModeView(mViewMode);
                                for (BackFragment fr : listFragment) {
                                    if (fr != null) {
                                        if (fr instanceof IListViewMode) {
                                            IListViewMode fragmentFileManagerFragment = (IListViewMode) fr;
                                            fragmentFileManagerFragment.setViewMode(mViewMode);
                                        }
                                    }
                                }
                                break;
                            default:
                                Toast.makeText(app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
        });
		AlertDialog menuDrop = menuAlert.create();
		menuDrop.show();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                    switch(getCurrentFragmentIndex()) {
                        case 0: refreshListServer(); break;
                        case 1: refreshListServer(); break;
                        case 2: refreshListServer(); break;
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

                switch(getCurrentFragmentIndex()) {
                    case 0: refreshListServer(query); break;
                    case 1: refreshListServer(query); break;
                    case 2: refreshListServer(query); break;
                }

                return false;
            }
        };

        if(mSearchView!=null)
            mSearchView.setOnQueryTextListener(queryTextListener);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search)	.setVisible(true);
        menu.findItem(R.id.action_delete)	.setVisible(false);
        menu.findItem(R.id.action_add)		.setVisible(false);
        menu.findItem(R.id.action_download)	.setVisible(false);
        menu.findItem(R.id.action_upload)	.setVisible(false);
        menu.findItem(R.id.action_home) 	.setVisible(false);
        menu.findItem(R.id.action_sort)	    .setVisible(true);

        switch(getCurrentFragmentIndex()) {
            case 0:
                menu.findItem(R.id.action_download)	.setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.action_download)	.setVisible(true);
                break;
            case 2:
                menu.findItem(R.id.action_home)	.setVisible(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                add();
                return true;
            case R.id.action_download:
                download();
                return true;
            case R.id.action_upload:
                upload();
                return true;
            case R.id.action_home:
                goHome();
                return true;
            case R.id.action_sort:
                    sort();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
