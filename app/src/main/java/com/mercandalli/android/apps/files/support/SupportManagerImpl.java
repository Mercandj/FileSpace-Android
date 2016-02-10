package com.mercandalli.android.apps.files.support;

import android.content.Context;

import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

public class SupportManagerImpl extends SupportManager {

    private final Context mContextApp;
    private final SupportOnlineApi mSupportOnlineApi;
    private final String mDeviceId;

    @SuppressWarnings({"UnusedParameters", "unused"})
    /* package */ SupportManagerImpl(final Context contextApp, final SupportOnlineApi supportOnlineApi) {
        mContextApp = contextApp;
        mSupportOnlineApi = supportOnlineApi;
        mDeviceId = SupportUtils.getIdentifier(contextApp);
    }

    @Override
    /* package */ void getSupportComment(final GetSupportManagerCallback getSupportManagerCallback) {
        Preconditions.checkNotNull(getSupportManagerCallback);
        mSupportOnlineApi.getSupportComments(mDeviceId, new Callback<SupportCommentsResponse>() {
            @Override
            public void success(SupportCommentsResponse supportCommentsResponse, Response response) {
                if (!supportCommentsResponse.isSucceed()) {
                    getSupportManagerCallback.onSupportManagerGetFailed();
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                getSupportManagerCallback.onSupportManagerGetSucceeded(supportComments);
            }

            @Override
            public void failure(RetrofitError error) {
                getSupportManagerCallback.onSupportManagerGetFailed();
            }
        });
    }

    @Override
    /* package */ void addSupportComment(final SupportComment supportComment, final GetSupportManagerCallback getSupportManagerCallback) {
        Preconditions.checkNotNull(supportComment);
        Preconditions.checkNotNull(getSupportManagerCallback);
        mSupportOnlineApi.postSupportComment(
                new TypedString(mDeviceId),
                new TypedString("" + supportComment.isDevResponse()),
                new TypedString(supportComment.getComment()),
                new Callback<SupportCommentsResponse>() {
                    @Override
                    public void success(SupportCommentsResponse supportCommentsResponse, Response response) {
                        if (!supportCommentsResponse.isSucceed()) {
                            getSupportManagerCallback.onSupportManagerGetFailed();
                            return;
                        }
                        final List<SupportComment> supportComments = new ArrayList<>();
                        final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                        for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                            supportComments.add(supportCommentResponse.toSupportComment());
                        }
                        getSupportManagerCallback.onSupportManagerGetSucceeded(supportComments);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        getSupportManagerCallback.onSupportManagerGetFailed();
                    }
                });
    }

    @Override
    /* package */ void deleteSupportComment(final SupportComment supportComment, final GetSupportManagerCallback getSupportManagerCallback) {
        Preconditions.checkNotNull(supportComment);
        Preconditions.checkNotNull(getSupportManagerCallback);
        mSupportOnlineApi.deleteSupportComment(
                new TypedString(supportComment.getId()),
                new TypedString(mDeviceId),
                new Callback<SupportCommentsResponse>() {
                    @Override
                    public void success(SupportCommentsResponse supportCommentsResponse, Response response) {
                        if (!supportCommentsResponse.isSucceed()) {
                            getSupportManagerCallback.onSupportManagerGetFailed();
                            return;
                        }
                        final List<SupportComment> supportComments = new ArrayList<>();
                        final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                        for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                            supportComments.add(supportCommentResponse.toSupportComment());
                        }
                        getSupportManagerCallback.onSupportManagerGetSucceeded(supportComments);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        getSupportManagerCallback.onSupportManagerGetFailed();
                    }
                });
    }
}
