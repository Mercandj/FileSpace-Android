package com.mercandalli.android.apps.files.main.version;

import android.app.Application;
import android.content.Context;

import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple manager to manage the current
 * {@link com.mercandalli.android.apps.files.BuildConfig#VERSION_CODE}.
 */
public class VersionManager {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private Context mContextApp;

    /**
     * The network API.
     */
    private VersionApi mVersionApi;

    /**
     * Is the update already called.
     */
    private boolean mIsUpdateCalledSucceeded = false;
    private boolean mIsUpdateNeeded = false;
    private boolean mIsUpdateChecking = false;

    /**
     * A {@link List} of listeners called after {@link #checkIfUpdateNeeded()}.
     */
    private final List<UpdateCheckedListener> mUpdateCheckedListeners = new ArrayList<>();

    /* package */ VersionManager(Application application, VersionApi versionApi) {
        Preconditions.checkNotNull(application);
        Preconditions.checkNotNull(versionApi);
        mContextApp = application.getApplicationContext();
        mVersionApi = versionApi;
    }

    /**
     * Check if an update is needed. You need to call
     * {@link #registerUpdateCheckedListener(UpdateCheckedListener)} and
     * {@link #unregisterUpdateCheckedListener(UpdateCheckedListener)}.
     */
    public void checkIfUpdateNeeded() {
        if (mIsUpdateChecking || mIsUpdateCalledSucceeded && !mIsUpdateNeeded) {
            return;
        }
        mIsUpdateChecking = true;
        mVersionApi.getVersionSupported(new Callback<VersionResponse>() {
            @Override
            public void success(VersionResponse versionResponse, Response response) {
                mIsUpdateChecking = false;
                mIsUpdateCalledSucceeded = true;
                mIsUpdateNeeded = versionResponse.isUpdateNeeded();
                if (mIsUpdateNeeded) {
                    notifyUpdateNeeded();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mIsUpdateChecking = false;
            }
        });
    }

    public boolean registerUpdateCheckedListener(UpdateCheckedListener updateCheckedListener) {
        synchronized (mUpdateCheckedListeners) {
            //noinspection SimplifiableIfStatement
            if (updateCheckedListener == null || mUpdateCheckedListeners.contains(updateCheckedListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mUpdateCheckedListeners.add(updateCheckedListener);
        }
    }

    public boolean unregisterUpdateCheckedListener(UpdateCheckedListener updateCheckedListener) {
        synchronized (mUpdateCheckedListeners) {
            return mUpdateCheckedListeners.remove(updateCheckedListener);
        }
    }

    private void notifyUpdateNeeded() {
        synchronized (mUpdateCheckedListeners) {
            for (int i = 0, size = mUpdateCheckedListeners.size(); i < size; i++) {
                mUpdateCheckedListeners.get(i).onUpdateNeeded();
            }
        }
    }

    /**
     * An simple interface used with {@link #checkIfUpdateNeeded()}.
     */
    public interface UpdateCheckedListener {

        /**
         * Is an update needed. The user should update this app.
         */
        void onUpdateNeeded();
    }
}
