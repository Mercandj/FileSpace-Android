package com.mercandalli.android.apps.files.support;

import java.util.List;

public abstract class SupportManager {

    abstract void getSupportComment(GetSupportManagerCallback getSupportManagerCallback);

    abstract void addSupportComment(SupportComment supportComment, GetSupportManagerCallback getSupportManagerCallback);

    interface GetSupportManagerCallback {
        void onSupportManagerGetSucceeded(List<SupportComment> supportComments);

        void onSupportManagerGetFailed();
    }
}
