/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.common.util.NetUtils;
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.user.LoginRegisterActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends BackFragment {

    private static final String BUNDLE_ARG_TITLE = "HomeFragment.Args.BUNDLE_ARG_TITLE";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ModelSetting> list;
    private int click_version;
    private boolean isDeveloper = false;
    private SetToolbarCallback mSetToolbarCallback;
    private String mTitle;

    private InterstitialAd mInterstitialAd;
    private int mThanhYou;

    public static SettingsFragment newInstance(String title) {
        final SettingsFragment fragment = new SettingsFragment();
        final Bundle args = new Bundle();
        args.putString(BUNDLE_ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetToolbarCallback) {
            mSetToolbarCallback = (SetToolbarCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSetToolbarCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (!args.containsKey(BUNDLE_ARG_TITLE)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mTitle = args.getString(BUNDLE_ARG_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_settings_toolbar);
        toolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(toolbar);
        setStatusBarColor(getActivity(), R.color.status_bar);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        click_version = 0;

        if (Constants.ADS_VISIBLE) {
            // Create an InterstitialAd object. This same object can be re-used whenever you want to
            // show an interstitial.
            mInterstitialAd = new InterstitialAd(getContext());
            mInterstitialAd.setAdUnitId(Constants.AD_MOB_KEY_SETTINGS);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    if (isAdded()) {
                        switch (mThanhYou) {
                            case 0:
                                Toast.makeText(getContext(), R.string.settings_ad_thank_you_1, Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(getContext(), R.string.settings_ad_thank_you_2, Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(getContext(), R.string.settings_ad_thank_you_3, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getContext(), R.string.settings_ad_thank_you_4, Toast.LENGTH_SHORT).show();
                                break;
                        }
                        mThanhYou++;
                    }
                }
            });
            if (!mInterstitialAd.isLoaded()) {
                requestNewInterstitial();
            }
        }

        refreshList();

        return rootView;
    }

    public void refreshList() {
        final Context context = getContext();
        list = new ArrayList<>();
        list.add(new ModelSetting(context.getString(R.string.settings_title), Constants.TAB_VIEW_TYPE_SECTION));
        if (Config.isLogged()) {
            list.add(new ModelSetting("Auto connection", new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mApplicationCallback.getConfig().setAutoConnection(context, isChecked);
                }
            }, mApplicationCallback.getConfig().isAutoConnection()));
            list.add(new ModelSetting("Web application", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NetUtils.search(context, Config.webApplication);
                }
            }));
        }
        if (Config.isLogged()) {
            list.add(new ModelSetting("Change password", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO Change password
                    Toast.makeText(context, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                }
            }));
        }
        if (isDeveloper) {
            list.add(new ModelSetting("Login / Sign in", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, LoginRegisterActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    getActivity().finish();
                }
            }));
        }
        list.add(new ModelSetting(getString(R.string.settings_about), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogAuthorLabel(getActivity(), mApplicationCallback);
            }
        }));
        list.add(new ModelSetting(getString(R.string.settings_licences), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LicenseActivity.start(context);
            }
        }));
        if (Constants.ADS_VISIBLE) {
            list.add(new ModelSetting(getString(R.string.settings_ad), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        requestNewInterstitial();
                        Toast.makeText(getContext(), R.string.settings_ad_is_loading, Toast.LENGTH_SHORT).show();
                    }
                }
            }));
        }

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
            list.add(new ModelSetting("Last update date GMT", TimeUtils.getGMTDate(pInfo.lastUpdateTime)));
            list.add(new ModelSetting("Version", pInfo.versionName, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (click_version == 11) {
                        Toast.makeText(context, "Development settings activated.", Toast.LENGTH_SHORT).show();
                        isDeveloper = true;
                        refreshList();
                    } else if (click_version < 11) {
                        if (click_version >= 1) {
                            final Toast t = Toast.makeText(context, "" + (11 - click_version), Toast.LENGTH_SHORT);
                            t.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    t.cancel();
                                }
                            }, 700);
                        }
                        click_version++;
                    }
                }
            }));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getClass().getName(), "NameNotFoundException", e);
        }

        updateAdapter();
    }

    public void updateAdapter() {
        if (recyclerView != null && list != null) {
            AdapterModelSetting adapter = new AdapterModelSetting(getActivity(), mApplicationCallback, list);
            adapter.setOnItemClickListener(new AdapterModelSetting.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position < list.size() && list.get(position).onClickListener != null) {
                        list.get(position).onClickListener.onClick(view);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }

    /**
     * Load a new interstitial ad asynchronously.
     */
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
