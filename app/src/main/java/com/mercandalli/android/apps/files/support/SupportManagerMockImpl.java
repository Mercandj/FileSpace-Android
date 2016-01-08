package com.mercandalli.android.apps.files.support;

import java.util.ArrayList;
import java.util.List;

public class SupportManagerMockImpl extends SupportManager {

    private final List<SupportComment> mSupportComments;

    public SupportManagerMockImpl() {
        mSupportComments = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mSupportComments.add(new SupportComment("Toto " + i, "Comment " + i));
        }
    }

    @Override
    /* package */ void getSupportComment(GetSupportManagerCallback getSupportManagerCallback) {
        getSupportManagerCallback.onSupportManagerGetSucceeded(mSupportComments);
    }

    @Override
    /* package */ void addSupportComment(SupportComment supportComment, GetSupportManagerCallback getSupportManagerCallback) {
        mSupportComments.add(supportComment);
        getSupportManagerCallback.onSupportManagerGetSucceeded(mSupportComments);
    }
}
