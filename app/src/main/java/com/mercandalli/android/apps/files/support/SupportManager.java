package com.mercandalli.android.apps.files.support;

import java.util.ArrayList;
import java.util.List;

public abstract class SupportManager {

    private final List<GetSupportManagerCallback> mGetSupportManagerCallbacks = new ArrayList<>();

    abstract void getSupportComment();

    abstract void addSupportComment(SupportComment supportComment);

    abstract void deleteSupportComment(SupportComment supportComment);

    /* package */ boolean registerOnCurrentMixFaderChangeListener(GetSupportManagerCallback getSupportManagerCallback) {
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

    /* package */ boolean unregisterOnCurrentMixFaderChangeListener(GetSupportManagerCallback getSupportManagerCallback) {
        synchronized (mGetSupportManagerCallbacks) {
            return mGetSupportManagerCallbacks.remove(getSupportManagerCallback);
        }
    }

    /* package */ void notifyGetSupportManagerCallbackSucceeded(final List<SupportComment> supportComments) {
        synchronized (mGetSupportManagerCallbacks) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, size = mGetSupportManagerCallbacks.size(); i < size; i++) {
                mGetSupportManagerCallbacks.get(i).onSupportManagerGetSucceeded(supportComments);
            }
        }
    }

    /* package */ void notifyGetSupportManagerCallbackFailed() {
        synchronized (mGetSupportManagerCallbacks) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, size = mGetSupportManagerCallbacks.size(); i < size; i++) {
                mGetSupportManagerCallbacks.get(i).onSupportManagerGetFailed();
            }
        }
    }

    interface GetSupportManagerCallback {
        void onSupportManagerGetSucceeded(final List<SupportComment> supportComments);

        void onSupportManagerGetFailed();
    }
}
