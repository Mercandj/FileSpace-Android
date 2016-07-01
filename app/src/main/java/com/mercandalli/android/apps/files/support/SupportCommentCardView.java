package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.library.base.precondition.Preconditions;

public class SupportCommentCardView extends CardView implements View.OnClickListener {

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private View mOverflowView;
    private SupportManager mSupportManager;
    private SupportComment mSupportComment;
    private SupportOverflowActions mSupportOverflowActions;

    private boolean mIsAdminIdSelection;

    public SupportCommentCardView(Context context) {
        super(context);
        init(context);
    }

    public SupportCommentCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SupportCommentCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void onClick(final View v) {
        if (mSupportComment == null) {
            return;
        }
        final int viewId = v.getId();
        if (viewId == R.id.tab_support_comment_card_more) {
            if (mSupportOverflowActions == null) {
                mSupportOverflowActions = new SupportOverflowActions(getContext());
            }
            mSupportOverflowActions.show(mSupportComment, v);
        } else if (viewId == R.id.tab_support_comment_card_item && mIsAdminIdSelection) {
            mSupportManager.getSupportComment(mSupportComment.getIdDevice());
        }
    }

    public void setSupportComment(final SupportComment supportComment, final boolean isAdminIdSelection) {
        Preconditions.checkNotNull(supportComment);
        mSupportComment = supportComment;
        mIsAdminIdSelection = isAdminIdSelection;
        mTitleTextView.setText(isAdminIdSelection ? (
                "#" + supportComment.getId() + " conversation") :
                (supportComment.isDevResponse() ?
                        "The dev" : "You"));
        mOverflowView.setVisibility(isAdminIdSelection ?
                GONE :
                Config.isUserAdmin() ?
                        VISIBLE :
                        (supportComment.isDevResponse() ?
                                GONE :
                                VISIBLE));
        mSubtitleTextView.setText(isAdminIdSelection ?
                (supportComment.getIdDevice() + " is the device id.\n" +
                        supportComment.getNbCommentsWithThisIdDevice() + " messages.") :
                supportComment.getComment());
    }

    private void init(@NonNull final Context context) {
        Preconditions.checkNotNull(context);
        mSupportManager = SupportManager.getInstance(context);
        inflate(context, R.layout.tab_support_comment_card, this);
        setUseCompatPadding(true);
        setContentPadding(0, 0, 0, 0);
        findViews();

        int marginVertical = (int) dpToPx(context, 6);
        int marginHorizontal = (int) dpToPx(context, 20);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
        setLayoutParams(layoutParams);

        mOverflowView.setOnClickListener(this);
    }

    private void findViews() {
        mTitleTextView = (TextView) findViewById(R.id.tab_support_comment_card_title);
        mSubtitleTextView = (TextView) findViewById(R.id.tab_support_comment_card_subtitle);
        mOverflowView = findViewById(R.id.tab_support_comment_card_more);
        findViewById(R.id.tab_support_comment_card_item).setOnClickListener(this);
    }

    private float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
