package com.mercandalli.android.apps.files.support;

public class SupportComment {

    private final String mPseudo;
    private final String mComment;

    public SupportComment(final String pseudo, final String comment) {
        mPseudo = pseudo;
        mComment = comment;
    }

    public String getPseudo() {
        return mPseudo;
    }

    public String getComment() {
        return mComment;
    }
}
