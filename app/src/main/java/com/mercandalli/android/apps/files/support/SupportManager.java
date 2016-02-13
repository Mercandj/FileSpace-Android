package com.mercandalli.android.apps.files.support;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class SupportManager {

    private final List<GetSupportManagerCallback> mGetSupportManagerCallbacks = new ArrayList<>();

    abstract void getSupportComment(String deviceId);

    abstract void addSupportComment(SupportComment supportComment);

    abstract void deleteSupportComment(SupportComment supportComment);

    abstract void getAllDeviceIds();

    /* package */ boolean registerGetSupportManagerCallback(GetSupportManagerCallback getSupportManagerCallback) {
        synchronized (mGetSupportManagerCallbacks) {
            //noinspection SimplifiableIfStatement
            if (getSupportManagerCallback == null || mGetSupportManagerCallbacks.contains(getSupportManagerCallback)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }

            mGetSupportManagerCallbacks.add(getSupportManagerCallback);
            return true;
        }
    }

    /* package */ boolean unregisterGetSupportManagerCallback(GetSupportManagerCallback getSupportManagerCallback) {
        synchronized (mGetSupportManagerCallbacks) {
            return mGetSupportManagerCallbacks.remove(getSupportManagerCallback);
        }
    }

    /* package */ void notifyGetSupportManagerCallbackSucceeded(@Nullable final String deviceIdAsked, final List<SupportComment> supportComments, final boolean adminIdSelection) {
        synchronized (mGetSupportManagerCallbacks) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, size = mGetSupportManagerCallbacks.size(); i < size; i++) {
                mGetSupportManagerCallbacks.get(i).onGetSupportSucceeded(deviceIdAsked, supportComments, adminIdSelection);
            }
        }
    }

    /* package */ void notifyGetSupportManagerCallbackFailed(final boolean adminIdSelection) {
        synchronized (mGetSupportManagerCallbacks) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, size = mGetSupportManagerCallbacks.size(); i < size; i++) {
                mGetSupportManagerCallbacks.get(i).onGetSupportFailed(adminIdSelection);
            }
        }
    }

    interface GetSupportManagerCallback {
        void onGetSupportSucceeded(@Nullable final String deviceIdAsked, final List<SupportComment> supportComments, final boolean adminIdSelection);

        void onGetSupportFailed(final boolean adminIdSelection);
    }
}
