package com.mercandalli.android.apps.files.common.listener;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

public class IListenerUtils {

    @NonNull
    private static final Thread UI_THREAD = Looper.getMainLooper().getThread();

    @NonNull
    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    public static void executeOnUiThread(@NonNull final IListener listener) {
        if (Thread.currentThread() != UI_THREAD) {
            UI_HANDLER.post(new Runnable() {
                public void run() {
                    executeOnUiThread(listener);
                }
            });
            return;
        }
        listener.execute();
    }
}
