package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.network.NetUtils;

import java.util.List;

import static com.mercandalli.android.apps.files.support.SupportUtils.getDevice;

public class SupportFragment extends BackFragment implements
        SupportCommentAdapter.OnSupportCommentLongClickListener,
        SupportCommentAdapter.OnSupportCommentClickListener,
        SupportManager.GetSupportManagerCallback,
        View.OnClickListener {

    private static final String BUNDLE_ARG_TITLE = "SupportFragment.Args.BUNDLE_ARG_TITLE";
    private SetToolbarCallback mSetToolbarCallback;
    private String mTitle;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Button mNoInternetButton;
    private TextView mAdminTextView;
    private View mCancelView;
    private View mOkView;
    private View mProgressBar;

    private String mCurrentDeviceId;

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
        mSupportManager.registerGetSupportManagerCallback(this);
    }

    @Override
    public void onDestroy() {
        mSupportManager.unregisterGetSupportManagerCallback(this);
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
    public void onGetSupportSucceeded(@Nullable final String deviceIdAsked, final List<SupportComment> supportComments, final boolean adminIdSelection) {
        mCurrentDeviceId = deviceIdAsked;
        syncVisibility(false);
        mSupportCommentAdapter.setSupportComments(supportComments);
        mSupportCommentAdapter.setIsAdminIdSelection(adminIdSelection);
        mRecyclerView.scrollToPosition(supportComments.size() - 1);
    }

    @Override
    public void onGetSupportFailed(final boolean adminIdSelection) {
        syncVisibility(false);
    }

    @Override
    public void onClick(final View v) {
        if (v == mNoInternetButton) {
            refreshList();
        } else if (v == mOkView) {
            sendEditTextContent();
        } else if (v == mCancelView) {
            mEditText.setText("");
        } else if (v == mAdminTextView) {
            final String myDeviceId = SupportUtils.getDeviceId(getContext());
            if (!SupportUtils.equalsString(mCurrentDeviceId, myDeviceId)) {
                mCurrentDeviceId = myDeviceId;
                refreshList();
                mAdminTextView.setText("Go Adm");
            } else {
                syncVisibility(true);
                mSupportManager.getAllDeviceIds();
                mAdminTextView.setText("Go You");
            }
        }
    }

    private void findViews(final View rootView) {
        mProgressBar = rootView.findViewById(R.id.fragment_support_progress_bar);
        mOkView = rootView.findViewById(R.id.fragment_support_ok);
        mAdminTextView = (TextView) rootView.findViewById(R.id.fragment_support_admin);
        mCancelView = rootView.findViewById(R.id.fragment_support_cancel);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_support_recycler_view);
        mEditText = (EditText) rootView.findViewById(R.id.fragment_support_edit_text);
        mNoInternetButton = (Button) rootView.findViewById(R.id.fragment_support_no_internet_bt);
    }

    private void init(final Context context) {
        mNoInternetButton.setOnClickListener(this);

        mSupportCommentAdapter = new SupportCommentAdapter(this, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mSupportCommentAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < -5) {
                    SupportUtils.hideSoftInput(mEditText);
                }
            }
        });

        mCurrentDeviceId = SupportUtils.getDeviceId(context);
        refreshList();

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(v.getText())) {
                    sendEditTextContent();
                    return true;
                }
                return false;
            }
        });

        if (Config.isUserAdmin()) {
            mAdminTextView.setOnClickListener(this);
        }
        mOkView.setOnClickListener(this);
        mCancelView.setOnClickListener(this);
    }

    private void syncVisibility(final boolean isLoading) {
        if (!NetUtils.isInternetConnection(getContext())) {
            mNoInternetButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mEditText.setVisibility(View.GONE);
            mAdminTextView.setVisibility(View.GONE);
            mOkView.setVisibility(View.GONE);
            mCancelView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            return;
        }
        mNoInternetButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        mEditText.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        mAdminTextView.setVisibility(!isLoading && Config.isUserAdmin() ? View.VISIBLE : View.GONE);
        mOkView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        mCancelView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void refreshList() {
        syncVisibility(true);
        mSupportManager.getSupportComment(mCurrentDeviceId);
    }

    private void sendEditTextContent() {
        final String message = mEditText.getText().toString();
        final Context context = getContext();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(context, "Your message is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final SupportDevice supportDevice = getDevice(context);


        mSupportManager.addSupportComment(new SupportComment(
                "",
                mCurrentDeviceId,
                Config.isUserAdmin(),
                message,
                supportDevice.mAndroidAppVersionCode,
                supportDevice.mAndroidAppVersionName,
                supportDevice.mAndroidDeviceVersionSdk,
                0));
        mEditText.setText("");
    }
}
