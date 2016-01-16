package com.mercandalli.android.apps.files.file.audio.cast;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.MenuItem;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.common.api.Status;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.audio.FileAudioActivity;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple class with Cast methods (ChromeCast).
 */
public class FileAudioCast {

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;

    private Activity mActivity;
    private int mCurrentMusicIndex;
    private final List<FileAudioModel> mFileAudioModelList = new ArrayList<>();

    public void onCreate(final Activity activity) {
        mActivity = activity;
        mMediaRouter = MediaRouter.getInstance(activity.getApplicationContext());
        mMediaRouterCallback = new MediaRouterCallback();
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(
                        mActivity.getString(R.string.cast_id)))
                .build();
    }

    public void onCreateOptionsMenu(MenuItem mediaRouteMenuItem) {
        if (mediaRouteMenuItem == null) {
            return;
        }
        final MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        if (mediaRouteActionProvider != null) {
            mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        }
    }

    public void onResume() {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void onPause() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        mActivity = null;
    }

    public void startMusic(final int currentMusicIndex, List<FileAudioModel> musics) {
        mCurrentMusicIndex = currentMusicIndex;
        mFileAudioModelList.clear();
        mFileAudioModelList.addAll(musics);
    }

    private void teardown() {
        CastRemoteDisplayLocalService.stopService();
        mSelectedDevice = null;
    }

    private class MediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            super.onRouteSelected(router, info);
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            final Intent intent = new Intent(mActivity, FileAudioActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                    mActivity, 0, intent, 0);

            CastRemoteDisplayLocalService.NotificationSettings settings =
                    new CastRemoteDisplayLocalService.NotificationSettings.Builder()
                            .setNotificationPendingIntent(notificationPendingIntent)
                            .build();

            CastRemoteDisplayLocalService.startService(
                    mActivity.getApplicationContext(),
                    FileAudioPresentationService.class,
                    mActivity.getString(R.string.cast_id),
                    mSelectedDevice,
                    settings,
                    new CastRemoteDisplayLocalService.Callbacks() {
                        @Override
                        public void onServiceCreated(CastRemoteDisplayLocalService castRemoteDisplayLocalService) {

                        }

                        @Override
                        public void onRemoteDisplaySessionStarted(final CastRemoteDisplayLocalService service) {
                            // initialize sender UI
                            final List<FileAudioModel> fileAudioModelList = new ArrayList<>();
                            fileAudioModelList.addAll(mFileAudioModelList);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    FileAudioPresentationService presentationService = (FileAudioPresentationService) service;
                                    presentationService.startMusic(mCurrentMusicIndex, fileAudioModelList);
                                }
                            }, 500);
                        }

                        @Override
                        public void onRemoteDisplaySessionError(Status errorReason) {

                        }
                    });
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            super.onRouteUnselected(router, info);
            teardown();
            mSelectedDevice = null;
        }
    }
}
