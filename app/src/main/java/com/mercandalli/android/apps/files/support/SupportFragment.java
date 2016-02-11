package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.network.NetUtils;

import java.util.List;

public class SupportFragment extends BackFragment implements
        SupportCommentAdapter.OnSupportCommentLongClickListener,
        SupportCommentAdapter.OnSupportCommentClickListener,
        SupportManager.GetSupportManagerCallback, View.OnClickListener {

    private static final String BUNDLE_ARG_TITLE = "SupportFragment.Args.BUNDLE_ARG_TITLE";
    private SetToolbarCallback mSetToolbarCallback;
    private String mTitle;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Button mNoInternetButton;
    private SupportCommentAdapter mSupportCommentAdapter;

    private SupportManager mSupportManager;

    public static SupportFragment newInstance(String title) {
        final SupportFragment fragment = new SupportFragment();
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
        mSupportManager = FileApp.get().getFileAppComponent().provideSupportManager();
        mSupportManager.registerOnCurrentMixFaderChangeListener(this);
    }

    @Override
    public void onDestroy() {
        mSupportManager.unregisterOnCurrentMixFaderChangeListener(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_support, container, false);
        final Context context = getContext();

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_support_tool_bar);
        toolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(toolbar);

        findViews(rootView);
        init(context);

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
    public boolean onSupportCommentLongClick(View view, int position) {
        return false;
    }

    @Override
    public void onSupportCommentClick(View view, int position) {

    }

    @Override
    public void onSupportManagerGetSucceeded(final List<SupportComment> supportComments) {
        syncInternetConnection();
        mSupportCommentAdapter.setSupportComments(supportComments);
        mRecyclerView.scrollToPosition(supportComments.size() - 1);
    }

    @Override
    public void onSupportManagerGetFailed() {
        syncInternetConnection();
    }

    @Override
    public void onClick(View v) {
        if (v == mNoInternetButton) {
            refreshList();
        }
    }

    private void findViews(final View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_support_recycler_view);
        mEditText = (EditText) rootView.findViewById(R.id.fragment_support_edit_text);
        mNoInternetButton = (Button) rootView.findViewById(R.id.fragment_support_no_internet_bt);
    }

    private void init(final Context context) {
        mNoInternetButton.setOnClickListener(this);

        mSupportCommentAdapter = new SupportCommentAdapter(this, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mSupportCommentAdapter);
        refreshList();

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(v.getText())) {
                    onFabClicked();
                    return true;
                }
                return false;
            }
        });
    }

    private void syncInternetConnection() {
        final boolean internetOn = NetUtils.isInternetConnection(getContext());
        mNoInternetButton.setVisibility(internetOn ? View.GONE : View.VISIBLE);
        mEditText.setVisibility(internetOn ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(internetOn ? View.VISIBLE : View.GONE);
    }

    private void refreshList() {
        mSupportManager.getSupportComment();
    }

    private void onFabClicked() {
        mSupportManager.addSupportComment(new SupportComment("", false, mEditText.getText().toString()));
        mEditText.setText("");
    }
}
