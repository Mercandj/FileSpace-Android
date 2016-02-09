package com.mercandalli.android.apps.files.support;

public class SupportComment {

    private final boolean mIsDevResponse;
    private final String mComment;

    public SupportComment(final boolean isDevResponse, final String comment) {
        mIsDevResponse = isDevResponse;
        mComment = comment;
    }

    public boolean isDevResponse() {
        return mIsDevResponse;
    }

    public String getComment() {
        return mComment;
    }
}
