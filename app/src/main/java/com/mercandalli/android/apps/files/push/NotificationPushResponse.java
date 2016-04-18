package com.mercandalli.android.apps.files.push;

import com.mercandalli.android.apps.files.main.network.MyResponseEmpty;

public class NotificationPushResponse extends MyResponseEmpty {

    public String getToast() {
        return mToast;
    }
}
