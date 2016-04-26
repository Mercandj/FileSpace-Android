package com.mercandalli.android.apps.files.admin.notification;

import android.content.Context;

import com.mercandalli.android.apps.files.main.network.MyResponse;
import com.mercandalli.android.library.baselibrary.device.Device;

import java.util.List;

/* package */ class DevicesResponse extends MyResponse<Device> {

    @Override
    public List<Device> getResult(final Context context) {
        return super.getResult(context);
    }

    public String getToast() {
        return mToast;
    }
}
