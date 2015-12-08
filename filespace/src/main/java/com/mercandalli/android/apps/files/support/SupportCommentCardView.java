package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.Preconditions;

public class SupportCommentCardView extends CardView {

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

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

    public void setSupportComment(final SupportComment supportComment) {
        Preconditions.checkNotNull(supportComment);
        mTitleTextView.setText(supportComment.getPseudo());
        mSubtitleTextView.setText(supportComment.getComment());
    }

    private void init(final Context context) {
        Preconditions.checkNotNull(context);
        inflate(context, R.layout.tab_support_comment_card, this);
        findViews();

        int marginVertical = (int) dpToPx(context, 6);
        int marginHorizontal = (int) dpToPx(context, 20);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
        setLayoutParams(layoutParams);
    }

    private void findViews() {
        mTitleTextView = (TextView) findViewById(R.id.tab_support_comment_card_title);
        mSubtitleTextView = (TextView) findViewById(R.id.tab_support_comment_card_subtitle);
    }

    public float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
