package com.mercandalli.android.apps.files.support;

import android.content.Context;

import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.library.mainlibrary.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportManagerImpl extends SupportManager {

    private final Context mContextApp;
    private final SupportOnlineApi mSupportOnlineApi;

    @SuppressWarnings({"UnusedParameters", "unused"})
    /* package */ SupportManagerImpl(final Context contextApp, final SupportOnlineApi supportOnlineApi) {
        mContextApp = contextApp;
        mSupportOnlineApi = supportOnlineApi;
    }

    @Override
    /* package */ void getSupportComment(final String deviceId) {
        final Call<SupportCommentsResponse> call = mSupportOnlineApi.getSupportComments(deviceId);
        call.enqueue(new Callback<SupportCommentsResponse>() {
            @Override
            public void onResponse(Call<SupportCommentsResponse> call, Response<SupportCommentsResponse> response) {
                if (!response.isSuccessful()) {
                    notifyGetSupportManagerCallbackFailed(false);
                    return;
                }
                final SupportCommentsResponse supportCommentsResponse = response.body();
                if (!supportCommentsResponse.isSucceed()) {
                    notifyGetSupportManagerCallbackFailed(false);
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                notifyGetSupportManagerCallbackSucceeded(deviceId, supportComments, false);
            }

            @Override
            public void onFailure(Call<SupportCommentsResponse> call, Throwable t) {
                notifyGetSupportManagerCallbackFailed(false);

            }
        });
    }

    @Override
    /* package */ void addSupportComment(final SupportComment supportComment) {
        Preconditions.checkNotNull(supportComment);
        final Call<SupportCommentsResponse> call = mSupportOnlineApi.postSupportComment(
                supportComment.getIdDevice(),
                supportComment.isDevResponse(),
                supportComment.getComment(),

                supportComment.getAndroidAppVersionCode(),
                supportComment.getAndroidAppVersionName(),
                supportComment.getAndroidAppNotificationId(),

                supportComment.getAndroidDeviceVersionSdk(),
                supportComment.getAndroidDeviceModel(),
                supportComment.getAndroidDeviceManufacturer(),
                supportComment.getAndroidDeviceDisplayLanguage(),
                supportComment.getAndroidDeviceCountry()
        );
        call.enqueue(new Callback<SupportCommentsResponse>() {
            @Override
            public void onResponse(Call<SupportCommentsResponse> call, Response<SupportCommentsResponse> response) {
                if (!response.isSuccessful()) {
                    notifyGetSupportManagerCallbackFailed(false);
                    return;
                }
                final SupportCommentsResponse supportCommentsResponse = response.body();
                if (!supportCommentsResponse.isSucceed()) {
                    notifyGetSupportManagerCallbackFailed(false);
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                notifyGetSupportManagerCallbackSucceeded(supportComment.getIdDevice(), supportComments, false);
            }

            @Override
            public void onFailure(Call<SupportCommentsResponse> call, Throwable t) {
                notifyGetSupportManagerCallbackFailed(false);
            }
        });
    }

    @Override
    /* package */ void deleteSupportComment(final SupportComment supportComment) {
        Preconditions.checkNotNull(supportComment);
        final Call<SupportCommentsResponse> call = mSupportOnlineApi.deleteSupportComment(
                supportComment.getId(),
                supportComment.getIdDevice());
        call.enqueue(new Callback<SupportCommentsResponse>() {
            @Override
            public void onResponse(Call<SupportCommentsResponse> call, Response<SupportCommentsResponse> response) {
                if (!response.isSuccessful()) {
                    notifyGetSupportManagerCallbackFailed(false);
                    return;
                }
                final SupportCommentsResponse supportCommentsResponse = response.body();
                if (!supportCommentsResponse.isSucceed()) {
                    notifyGetSupportManagerCallbackFailed(false);
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                notifyGetSupportManagerCallbackSucceeded(supportComment.getIdDevice(), supportComments, false);
            }

            @Override
            public void onFailure(Call<SupportCommentsResponse> call, Throwable t) {
                notifyGetSupportManagerCallbackFailed(false);
            }
        });
    }

    @Override
    /* package */ void getAllDeviceIds() {
        if (!Config.isUserAdmin()) {
            notifyGetSupportManagerCallbackFailed(true);
        }
        final Call<SupportCommentsResponse> call = mSupportOnlineApi.getAllDeviceIdSupportComment();
        call.enqueue(new Callback<SupportCommentsResponse>() {
            @Override
            public void onResponse(Call<SupportCommentsResponse> call, Response<SupportCommentsResponse> response) {
                if (!response.isSuccessful()) {
                    notifyGetSupportManagerCallbackFailed(true);
                    return;
                }
                final SupportCommentsResponse supportCommentsResponse = response.body();
                if (!supportCommentsResponse.isSucceed()) {
                    notifyGetSupportManagerCallbackFailed(true);
                    return;
                }
                final List<SupportComment> supportComments = new ArrayList<>();
                final List<SupportCommentResponse> supportCommentResponses = supportCommentsResponse.getResult(mContextApp);
                for (SupportCommentResponse supportCommentResponse : supportCommentResponses) {
                    supportComments.add(supportCommentResponse.toSupportComment());
                }
                notifyGetSupportManagerCallbackSucceeded(null, supportComments, true);
            }

            @Override
            public void onFailure(Call<SupportCommentsResponse> call, Throwable t) {
                notifyGetSupportManagerCallbackFailed(true);
            }
        });
    }
}
