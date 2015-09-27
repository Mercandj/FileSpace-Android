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
package mercandalli.com.filespace.ui.fragment.file;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.activity.ApplicationDrawer;
import mercandalli.com.filespace.ui.dialog.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragment.Fragment;
import mercandalli.com.filespace.ui.fragment.FragmentFab;
import mercandalli.com.filespace.ui.view.PagerSlidingTabStrip;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;

public class FileManagerFragment extends Fragment {
	
	private static final int NB_FRAGMENT = 4;
    private static final int INIT_FRAGMENT = 1;
	public static FragmentFab listFragment[] = new FragmentFab[NB_FRAGMENT];
	private Application app;
	private ViewPager mViewPager;
	private FileManagerFragmentPagerAdapter mPagerAdapter;
    private PagerSlidingTabStrip tabs;

    private FloatingActionButton circle, circle2;
    private View coordinatorLayoutView;
    private Snackbar snackbar;

	public static int VIEW_MODE = Const.MODE_LIST;
	
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

        this.coordinatorLayoutView = (View) rootView.findViewById(R.id.snackBarPosition);

		mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager(), app);

        VIEW_MODE = ((app.getConfig().getUserFileModeView() > -1) ? app.getConfig().getUserFileModeView() : Const.MODE_LIST);

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                FileManagerFragment.this.app.invalidateOptionsMenu();
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
        });
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


        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.white));

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
        return mViewPager.getCurrentItem();
	}

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if(listFragment == null || currentFragmentId== -1)
            return false;
        FragmentFab fragment = listFragment[currentFragmentId];
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

        if(currentFragment.isFabVisible(1))
            this.circle2.show();
        else
            this.circle2.hide();
        this.circle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFragment.onFabClick(1, circle2);
            }
        });
        if(currentFragment.getFabDrawable(1) != null)
            this.circle2.setImageDrawable(currentFragment.getFabDrawable(1));
        else
            this.circle2.setImageDrawable(app.getDrawable(android.R.drawable.ic_input_add));
    }

    @Override
    public void onFocus() { }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
		Application app;
		
		public FileManagerFragmentPagerAdapter(FragmentManager fm, Application app) {
			super(fm);
			this.app = app;
		}
		
		@Override
        public Fragment getItem(int i) {

            final IListener refreshFab = new IListener() {
                @Override
                public void execute() {
                    refreshFab();
                }
            };

            if(!app.isLogged())
                i+=2;

			FragmentFab fragment = null;
			switch(i) {
                case 0:		fragment = new FileManagerFragmentCloud(refreshFab);  	    break;
                case 1:		fragment = new FileManagerFragmentMyCloud(refreshFab); 	    break;
                case 2:		fragment = new FileManagerFragmentLocal(refreshFab);	    break;
                case 3:		fragment = new FileManagerFragmentLocalMusic(refreshFab);	break;
                default:	fragment = new FileManagerFragmentLocal(refreshFab);	    break;
			}
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
                case 0:		title = "PUBLIC CLOUD";	break;
                case 1:		title = "MY CLOUD";		break;
                case 2:		title = "LOCAL";		break;
                case 3:		title = "MUSIC";		break;
			}
			return title;
        }
    }
	

	public void refreshListServer() {
		refreshListServer(null);
	}
	
	public void refreshListServer(String search) {
        for(FragmentFab fr : listFragment) {
            if(fr!=null) {
                if (fr instanceof FileManagerFragmentCloud) {
                    FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) fr;
                    fragmentFileManagerFragment.refreshList(search);
                }
                else if (fr instanceof FileManagerFragmentMyCloud) {
                    FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) fr;
                    fragmentFileManagerFragment.refreshList(search);
                }
                else if (fr instanceof FileManagerFragmentLocal) {
                    FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) fr;
                    fragmentFileManagerFragment.refreshList(search);
                }
            }
        }
	}
	
	public void updateAdapterListServer() {

        for(FragmentFab fr : listFragment) {
            if(fr!=null) {
                if (fr instanceof FileManagerFragmentCloud) {
                    FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) fr;
                    fragmentFileManagerFragment.updateAdapter();
                }
                else if (fr instanceof FileManagerFragmentMyCloud) {
                    FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) fr;
                    fragmentFileManagerFragment.updateAdapter();
                }
                else if (fr instanceof FileManagerFragmentLocal) {
                    FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) fr;
                    fragmentFileManagerFragment.updateAdapter();
                }
            }
        }
	}
	
	public void refreshAdapterListServer() {
        for(FragmentFab fr : listFragment) {
            if(fr!=null) {
                if (fr instanceof FileManagerFragmentCloud) {
                    FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) fr;
                    fragmentFileManagerFragment.refreshList();
                }
                if (fr instanceof FileManagerFragmentMyCloud) {
                    FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) fr;
                    fragmentFileManagerFragment.refreshList();
                }
                if (fr instanceof FileManagerFragmentLocal) {
                    FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) fr;
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
                if(listFragment[2] instanceof FileManagerFragmentLocal) {
                    FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
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
                                if(listFragment.length>2)
                                    if (listFragment[2] != null)
                                        if (listFragment[2] instanceof FileManagerFragmentLocal) {
                                            FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
                                            fragmentFileManagerFragment.setSort(item==0?Const.SORT_ABC:(item==1?Const.SORT_SIZE:Const.SORT_DATE_MODIFICATION));
                                        }
                                if(getCurrentFragmentIndex()!=2)
                                    Toast.makeText(app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                break;

                            case 3:
                                if (VIEW_MODE == Const.MODE_LIST)
                                    VIEW_MODE = Const.MODE_GRID;
                                else
                                    VIEW_MODE = Const.MODE_LIST;
                                app.getConfig().setUserFileModeView(VIEW_MODE);
                                if(listFragment[0]!=null)
                                    if(listFragment[0] instanceof FileManagerFragmentCloud) {
                                        FileManagerFragmentCloud fragmentFileManagerFragment = (FileManagerFragmentCloud) listFragment[0];
                                        fragmentFileManagerFragment.updateAdapter();
                                    }
                                if(listFragment.length>1)
                                    if(listFragment[1]!=null)
                                        if(listFragment[1] instanceof FileManagerFragmentMyCloud) {
                                            FileManagerFragmentMyCloud fragmentFileManagerFragment = (FileManagerFragmentMyCloud) listFragment[1];
                                            fragmentFileManagerFragment.updateAdapter();
                                        }
                                if(listFragment.length>2)
                                    if(listFragment[2]!=null)
                                        if(listFragment[2] instanceof FileManagerFragmentLocal) {
                                            FileManagerFragmentLocal fragmentFileManagerFragment = (FileManagerFragmentLocal) listFragment[2];
                                            fragmentFileManagerFragment.updateAdapter();
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
}
