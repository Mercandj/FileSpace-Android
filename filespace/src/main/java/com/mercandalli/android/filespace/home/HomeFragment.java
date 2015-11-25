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
package com.mercandalli.android.filespace.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.fragment.BackFragment;
import com.mercandalli.android.filespace.main.Constants;
import com.mercandalli.android.filespace.extras.ia.Interpreter;
import com.mercandalli.android.filespace.extras.ia.InterpreterMain;
import com.mercandalli.android.filespace.extras.ia.InterpreterResult;
import com.mercandalli.android.filespace.common.listener.SetToolbarCallback;
import com.mercandalli.android.filespace.main.ApplicationDrawerActivity;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class HomeFragment extends BackFragment implements TextToSpeech.OnInitListener {

    private static final String BUNDLE_ARG_TITLE = "HomeFragment.Args.BUNDLE_ARG_TITLE";

    private View rootView;

    private RecyclerView mRecyclerView;
    private AdapterModelHome mAdapter;
    private List<ModelHome> mModelHomeList;
    private ProgressBar circularProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextToSpeech myTTS;

    private EditText input;

    private Toolbar mToolbar;
    private String mTitle;

    protected SetToolbarCallback mSetToolbarCallback;

    public static HomeFragment newInstance(String title) {
        final HomeFragment fragment = new HomeFragment();
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
        mApplicationCallback = null;
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
        this.rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.fragment_home_toolbar);
        mToolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(mToolbar);
        setStatusBarColor(mActivity, R.color.notifications_bar);

        circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        (rootView.findViewById(R.id.circle)).setVisibility(View.GONE);

        this.input = (EditText) rootView.findViewById(R.id.input);
        this.input.setVisibility(View.GONE);
        this.input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Interpreter interpreter = new InterpreterMain(mActivity, mApplicationCallback.getConfig().getUser().isAdmin());
                    addItemList("FileSpace", interpreter.interpret(input.getText().toString()));
                    input.setText("");
                    return true;
                }
                return false;
            }
        });

        (rootView.findViewById(R.id.circle)).setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (input.getVisibility() == View.GONE) {
                    input.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    InputMethodManager mgr = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    input.setVisibility(View.GONE);
                }
                return true;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NasaUtils.getNasaRandomPicture(mActivity, mApplicationCallback, new IModelNasaImageListener() {
                    @Override
                    public void execute(ModelNasaImage modelNasaImage) {
                        refreshList(modelNasaImage);
                    }
                });
                refreshList();
            }
        });

        refreshList();

        NasaUtils.getNasaRandomPicture(mActivity, mApplicationCallback, new IModelNasaImageListener() {
            @Override
            public void execute(ModelNasaImage modelNasaImage) {
                refreshList(modelNasaImage);
            }
        });

        return rootView;
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(ModelNasaImage modelNasaImage) {
        if(!isAdded()) {
            return;
        }
        mModelHomeList = new ArrayList<>();

        List<ModelServerMessage> serverMessageList = mApplicationCallback.getConfig().getListServerMessage_1();
        for (int i = serverMessageList.size() - 1; i >= 0; i--) {
            mModelHomeList.add(new ModelHome(mModelHomeList.size(), "Notification", new IModelHomeListener() {
                @Override
                public void execute(ModelHome modelHome) {
                    removeItemList(modelHome);
                    if (modelHome.serverMessage != null)
                        mApplicationCallback.getConfig().removeServerMessage(modelHome.serverMessage);
                }
            }, serverMessageList.get(i), Constants.TAB_VIEW_TYPE_HOME_INFORMATION));
        }

        if (mApplicationCallback.getConfig().isHomeWelcomeMessage()) {

            Spanned htmlMessage = Html.fromHtml("<a>This app give you the control on your local <font color=\"#26AEEE\">files</font>. This app is also a <font color=\"#f57c00\">music</font> player.</a>");
            if (mApplicationCallback.getConfig().isLogged())
                htmlMessage = Html.fromHtml("<a>This app give you the Cloud control from your Android device and your PC thanks to the <font color=\"#26AEEE\">web application</font>. You can share files and talk with your friends.</a>");

            if (mApplicationCallback.isLogged()) {
                mModelHomeList.add(new ModelHome(mModelHomeList.size(), "Welcome", new IModelHomeListener() {
                    @Override
                    public void execute(ModelHome modelHome) {
                        removeItemList(modelHome);
                        mApplicationCallback.getConfig().setHomeWelcomeMessage(false);
                    }
                }, htmlMessage, Constants.TAB_VIEW_TYPE_HOME_INFORMATION));
            } else {
                mModelHomeList.add(new ModelHome(mModelHomeList.size(), "Welcome", new IModelHomeListener() {
                    @Override
                    public void execute(ModelHome modelHome) {
                        removeItemList(modelHome);
                        mApplicationCallback.getConfig().setHomeWelcomeMessage(false);
                    }
                }, htmlMessage, Constants.TAB_VIEW_TYPE_HOME_INFORMATION));
            }
        }

        mModelHomeList.add(new ModelHome(mModelHomeList.size(), "Tabs", Constants.TAB_VIEW_TYPE_SECTION));
        mModelHomeList.add(new ModelHome(mModelHomeList.size(),
                "Files",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mActivity instanceof ApplicationDrawerActivity) {
                            ((ApplicationDrawerActivity) mActivity).selectItem(3);
                        }
                    }
                },
                "Workspace",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ApplicationDrawerActivity) mActivity).selectItem(4);
                    }
                },
                Constants.TAB_VIEW_TYPE_TWO_BUTTONS));

        if (modelNasaImage != null) {
            mModelHomeList.add(new ModelHome(mModelHomeList.size(), "NASA Image - " + modelNasaImage.date, new IModelHomeListener() {
                @Override
                public void execute(ModelHome modelHome) {
                    removeItemList(modelHome);
                }
            }, modelNasaImage.explanation, modelNasaImage.bitmap, Constants.TAB_VIEW_TYPE_HOME_IMAGE));
        }

        updateAdapter();
    }

    public void updateAdapter() {
        if (mRecyclerView != null && mModelHomeList != null && isAdded()) {
            circularProgressBar.setVisibility(View.GONE);

            mAdapter = new AdapterModelHome(mActivity, mModelHomeList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());

            if ((rootView.findViewById(R.id.circle)).getVisibility() == View.GONE) {
                (rootView.findViewById(R.id.circle)).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(mActivity, R.anim.circle_button_bottom_open);
                (rootView.findViewById(R.id.circle)).startAnimation(animOpen);
            }

            (rootView.findViewById(R.id.circle)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        // Specify the calling package to identify your application
                        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getClass()
                                .getPackage().getName());

                        // Display an hint to the user about what he should say.
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "FileSpace");

                        // Given an hint to the recognizer about what the user is going to say
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

                        int noOfMatches = 1;
                        // Specify how many results you want to receive. The results will be
                        // sorted where the first result is the one with higher confidence.

                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);

                        HomeFragment.this.startActivityForResult(intent, 1001);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "Google voice recognition not found.", Toast.LENGTH_LONG).show();
                    }
                    if (myTTS == null)
                        myTTS = new TextToSpeech(HomeFragment.this.getActivity(), HomeFragment.this);
                }

            });

            this.mAdapter.setOnItemClickListener(new AdapterModelHome.OnModelHomeClickListener() {
                @Override
                public void onModelHomeClick(View view, int position) {

                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1001 && data != null) {

            if (myTTS == null)
                myTTS = new TextToSpeech(this.getActivity(), this);

            ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (textMatchList != null)
                if (!textMatchList.isEmpty()) {
                    Interpreter interpreter = new InterpreterMain(mActivity, mApplicationCallback.getConfig().isUserAdmin());
                    String input = textMatchList.get(0);
                    addItemList("FileSpace", interpreter.interpret(input));
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // speak the user text
    public void speakWords(String speech) {
        if (speech == null) return;
        else if (speech.equals("") || speech.equals(" ")) return;

        if (myTTS == null)
            myTTS = new TextToSpeech(this.getActivity(), this);

        HashMap<String, String> ttsParams = new HashMap<>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, this.getActivity().getPackageName());
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, ttsParams);
    }

    @Override
    public void onInit(int initStatus) {
        // check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(getActivity(), "Text-To-Speech error...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myTTS != null)
            myTTS.shutdown();
    }

    public void addItemList(String title, InterpreterResult interpreterResult) {
        if (title != null && interpreterResult != null) {
            if (interpreterResult.content != null)
                speakWords(interpreterResult.content);

            mRecyclerView.scrollToPosition(0);
            if (interpreterResult.modelForm != null)
                mAdapter.addItem(
                        new ModelHome(mModelHomeList.size(), title, new IModelHomeListener() {
                            @Override
                            public void execute(ModelHome modelHome) {
                                removeItemList(modelHome);
                            }
                        },
                                interpreterResult.modelForm,
                                Constants.TAB_VIEW_TYPE_HOME_INFORMATION_FORM),
                        0
                );
            else if (interpreterResult.content != null)
                if (!interpreterResult.content.equals(""))
                    mAdapter.addItem(
                            new ModelHome(mModelHomeList.size(), title, new IModelHomeListener() {
                                @Override
                                public void execute(ModelHome modelHome) {
                                    removeItemList(modelHome);
                                }
                            },
                                    interpreterResult.content,
                                    Constants.TAB_VIEW_TYPE_HOME_INFORMATION),
                            0
                    );
        }
    }

    public void removeItemList(ModelHome modelHome) {
        mAdapter.removeItem(modelHome);
    }
}
