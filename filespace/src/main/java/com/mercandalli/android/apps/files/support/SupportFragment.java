package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.main.AppComponent;

import java.util.List;

import javax.inject.Inject;

public class SupportFragment extends InjectedBackFragment implements View.OnClickListener, SupportCommentAdapter.OnSupportCommentLongClickListener, SupportCommentAdapter.OnSupportCommentClickListener, SupportManager.GetSupportManagerCallback {

    private static final String BUNDLE_ARG_TITLE = "SupportFragment.Args.BUNDLE_ARG_TITLE";
    private SetToolbarCallback mSetToolbarCallback;
    private String mTitle;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private FloatingActionButton mFloatingActionButton;
    private SupportCommentAdapter mSupportCommentAdapter;

    @Inject
    /*package*/ SupportManager mSupportManager;

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
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_support, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_support_tool_bar);
        toolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(toolbar);

        findViews(rootView);
        init(getContext());

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
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.fragment_support_fab:
                onFabClicked();
                break;
        }
    }

    @Override
    public boolean onSupportCommentLongClick(View view, int position) {
        return false;
    }

    @Override
    public void onSupportCommentClick(View view, int position) {

    }

    @Override
    public void onSupportManagerGetSucceeded(List<SupportComment> supportComments) {
        mSupportCommentAdapter.setSupportComments(supportComments);
    }

    @Override
    public void onSupportManagerGetFailed() {

    }

    private void findViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_support_recycler_view);
        mEditText = (EditText) rootView.findViewById(R.id.fragment_support_edit_text);
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fragment_support_fab);
    }

    private void init(final Context context) {
        mSupportCommentAdapter = new SupportCommentAdapter(this, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mSupportCommentAdapter);
        mFloatingActionButton.setOnClickListener(this);
        refreshList();
    }

    private void refreshList() {
        mSupportManager.getSupportComment(this);
    }

    private void onFabClicked() {
        mSupportManager.addSupportComment(new SupportComment("", mEditText.getText().toString()), this);
        mEditText.setText("");
    }
}