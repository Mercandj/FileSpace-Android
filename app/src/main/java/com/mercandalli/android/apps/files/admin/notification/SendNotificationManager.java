package com.mercandalli.android.apps.files.admin.notification;

import android.view.View;

public class SendNotificationManager {

    private static SendNotificationManager sSendNotificationManager;

    /* */
    static SendNotificationManager getInstance() {
        if (sSendNotificationManager == null) {
            sSendNotificationManager = new SendNotificationManager();
        }
        return sSendNotificationManager;
    }

    private OnFabClicked mOnFabClicked;

    public void onFabClicked(final View v) {
        if (mOnFabClicked != null) {
            mOnFabClicked.onFabClicked(v);
        }
    }

    public void setOnFabClicked(final OnFabClicked onFabClicked) {
        mOnFabClicked = onFabClicked;
    }

    interface OnFabClicked {
        void onFabClicked(final View v);
    }
}
