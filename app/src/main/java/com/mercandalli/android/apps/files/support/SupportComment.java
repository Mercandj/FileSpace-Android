package com.mercandalli.android.apps.files.support;

public class SupportComment {

    private final String mId;
    private final boolean mIsDevResponse;
    private final String mComment;

    public SupportComment(final String id, final boolean isDevResponse, final String comment) {
        mId = id;
        mIsDevResponse = isDevResponse;
        mComment = comment;
    }

    public boolean isDevResponse() {
        return mIsDevResponse;
    }

    public String getComment() {
        return mComment;
    }

    public String getId() {
        return mId;
    }
}
