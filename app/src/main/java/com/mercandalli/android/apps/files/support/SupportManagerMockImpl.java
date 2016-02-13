package com.mercandalli.android.apps.files.support;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SupportManagerMockImpl extends SupportManager {

    private final List<SupportComment> mSupportComments;

    @SuppressWarnings("UnusedParameters")
    public SupportManagerMockImpl(final Context contextApp) {
        mSupportComments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mSupportComments.add(new SupportComment(
                    String.valueOf(i),
                    String.valueOf(i),
                    false,
                    "Comment " + i,
                    "",
                    "",
                    "",
                    10));
        }
    }

    @Override
    /* package */ void getSupportComment(final String deviceId) {
        notifyGetSupportManagerCallbackSucceeded(deviceId, mSupportComments, false);
    }

    @Override
    /* package */ void addSupportComment(SupportComment supportComment) {
        mSupportComments.add(supportComment);
        notifyGetSupportManagerCallbackSucceeded(supportComment.getIdDevice(), mSupportComments, false);
    }

    @Override
    /* package */ void deleteSupportComment(SupportComment supportComment) {

        notifyGetSupportManagerCallbackSucceeded(supportComment.getIdDevice(), mSupportComments, false);
    }

    @Override
    void getAllDeviceIds() {

        notifyGetSupportManagerCallbackSucceeded(null, mSupportComments, true);
    }
}
