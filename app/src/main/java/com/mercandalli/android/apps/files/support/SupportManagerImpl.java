package com.mercandalli.android.apps.files.support;

import android.content.Context;

import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SupportManagerImpl extends SupportManager {

    private final SupportOnlineApi mSupportOnlineApi;
    private final String mDeviceId;

    @SuppressWarnings({"UnusedParameters", "unused"})
    /* package */ SupportManagerImpl(final Context contextApp, final SupportOnlineApi supportOnlineApi) {
        mSupportOnlineApi = supportOnlineApi;
        mDeviceId = SupportUtils.getIdentifier(contextApp);
    }

    @Override
    /* package */ void getSupportComment(final GetSupportManagerCallback getSupportManagerCallback) {
        Preconditions.checkNotNull(getSupportManagerCallback);
        mSupportOnlineApi.getSupportComments(mDeviceId, new Callback<SupportCommentsResponse>() {
            @Override
            public void success(SupportCommentsResponse supportCommentsResponse, Response response) {
                final List<SupportComment> supportComments = new ArrayList<>();
                // TODO
                getSupportManagerCallback.onSupportManagerGetSucceeded(supportComments);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    /* package */ void addSupportComment(final SupportComment supportComment, final GetSupportManagerCallback getSupportManagerCallback) {
        Preconditions.checkNotNull(supportComment);
        Preconditions.checkNotNull(getSupportManagerCallback);
    }
}
