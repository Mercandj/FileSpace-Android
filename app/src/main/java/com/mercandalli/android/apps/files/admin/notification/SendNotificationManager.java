package com.mercandalli.android.apps.files.admin.notification;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mercandalli.android.apps.files.admin.notification.simple.SendNotificationSimpleResponse;
import com.mercandalli.android.apps.files.main.network.RetrofitUtils;
import com.mercandalli.android.library.base.device.Device;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationManager {

    private static SendNotificationManager sSendNotificationManager;
    @NonNull
    private final SendNotificationOnlineApi mSendNotificationOnlineApi;

    /* */
    static SendNotificationManager getInstance() {
        if (sSendNotificationManager == null) {
            sSendNotificationManager = new SendNotificationManager();
        }
        return sSendNotificationManager;
    }

    private OnFabClicked mOnFabClicked;
    private OnRefreshDevice mOnRefreshDevice;

    public SendNotificationManager() {
        mSendNotificationOnlineApi = RetrofitUtils.getAuthorizedRetrofit().create(SendNotificationOnlineApi.class);
    }

    public void onFabClicked(final View v) {
        if (mOnFabClicked != null) {
            mOnFabClicked.onFabClicked(v);
        }
    }

    public void setOnFabClicked(final OnFabClicked onFabClicked) {
        mOnFabClicked = onFabClicked;
    }

    public void delete(@Nullable final Device device) {
        if (device == null || device.getId() == null) {
            return;
        }
        final Call<SendNotificationSimpleResponse> delete = mSendNotificationOnlineApi.delete(
                new SendNotificationDeleteRequest(device.getId()));
        delete.enqueue(new Callback<SendNotificationSimpleResponse>() {
            @Override
            public void onResponse(
                    final Call<SendNotificationSimpleResponse> call,
                    final Response<SendNotificationSimpleResponse> response) {
                if (mOnRefreshDevice != null) {
                    mOnRefreshDevice.onRefreshDevice();
                }
            }

            @Override
            public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {

            }
        });
    }

    public void setOnRefreshDevice(final OnRefreshDevice onRefreshDevice) {
        mOnRefreshDevice = onRefreshDevice;
    }

    interface OnFabClicked {
        void onFabClicked(final View v);
    }

    interface OnRefreshDevice {
        void onRefreshDevice();
    }
}
