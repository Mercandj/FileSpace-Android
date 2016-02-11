package com.mercandalli.android.apps.files.support;

import android.content.Context;

import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    /* package */ void getSupportComment() {
        final Call<SupportCommentsResponse> call = mSupportOnlineApi.getSupportComments(mDeviceId);
        call.enqueue(new Callback<SupportCommentsResponse>() {
            @Override
            public void onResponse(Call<SupportCommentsResponse> call, Response<SupportCommentsResponse> response) {
                if (!response.isSuccess()) {
                    notifyGetSupportManagerCallbackFailed();
                    return;
                }
                final SupportCommentsResponse supportCommentsResponse = response.body();
                if (!supportCommentsResponse.isSucceed()) {
                    notifyGetSupportManagerCallbackFailed();
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                notifyGetSupportManagerCallbackSucceeded(supportComments);
            }

            @Override
            public void onFailure(Call<SupportCommentsResponse> call, Throwable t) {
                notifyGetSupportManagerCallbackFailed();

            }
        });
    }

    @Override
    /* package */ void addSupportComment(final SupportComment supportComment) {
        Preconditions.checkNotNull(supportComment);
        final Call<SupportCommentsResponse> call = mSupportOnlineApi.postSupportComment(
                mDeviceId,
                supportComment.isDevResponse(),
                supportComment.getComment());
        call.enqueue(new Callback<SupportCommentsResponse>() {
            @Override
            public void onResponse(Call<SupportCommentsResponse> call, Response<SupportCommentsResponse> response) {
                if (!response.isSuccess()) {
                    notifyGetSupportManagerCallbackFailed();
                    return;
                }
                final SupportCommentsResponse supportCommentsResponse = response.body();
                if (!supportCommentsResponse.isSucceed()) {
                    notifyGetSupportManagerCallbackFailed();
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                notifyGetSupportManagerCallbackSucceeded(supportComments);
            }

            @Override
            public void onFailure(Call<SupportCommentsResponse> call, Throwable t) {
                notifyGetSupportManagerCallbackFailed();
            }
        });
    }

    @Override
    /* package */ void deleteSupportComment(final SupportComment supportComment) {
        Preconditions.checkNotNull(supportComment);
        final Call<SupportCommentsResponse> call = mSupportOnlineApi.deleteSupportComment(
                supportComment.getId(),
                mDeviceId);
        call.enqueue(new Callback<SupportCommentsResponse>() {
            @Override
            public void onResponse(Call<SupportCommentsResponse> call, Response<SupportCommentsResponse> response) {
                if (!response.isSuccess()) {
                    notifyGetSupportManagerCallbackFailed();
                    return;
                }
                final SupportCommentsResponse supportCommentsResponse = response.body();
                if (!supportCommentsResponse.isSucceed()) {
                    notifyGetSupportManagerCallbackFailed();
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                notifyGetSupportManagerCallbackSucceeded(supportComments);
            }

            @Override
            public void onFailure(Call<SupportCommentsResponse> call, Throwable t) {
                notifyGetSupportManagerCallbackFailed();
            }
        });
    }
}
