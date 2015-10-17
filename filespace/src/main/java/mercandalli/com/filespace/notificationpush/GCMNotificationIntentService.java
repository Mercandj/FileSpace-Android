/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.notificationpush;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import mercandalli.com.filespace.models.ModelServerMessage;
import mercandalli.com.filespace.ui.activities.ConversationActivity;
import mercandalli.com.filespace.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.filespace.R;

public class GCMNotificationIntentService extends IntentService {
    private static final String TAG = "GCMNotificationIntentS";

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(new ModelServerMessage("Send error: " + extras.toString()));
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(new ModelServerMessage("Deleted messages on server: " + extras.toString()));
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                ModelServerMessage serverMessage = new ModelServerMessage(
                        "" + extras.get(Config.KEY_MESSAGE),
                        "" + extras.get(Config.KEY_ID_CONVERSATION)
                );
                saveServerMessage(serverMessage);

                sendNotification(serverMessage);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(ModelServerMessage serverMessage) {
        Intent i;
        if (serverMessage.isConversationMessage()) {
            i = new Intent(this, ConversationActivity.class);
            i.putExtra("ID_CONVERSATION", serverMessage.getId_conversation());
        } else {
            i = new Intent(Intent.ACTION_MAIN);
            PackageManager manager = this.getPackageManager();
            i = manager.getLaunchIntentForPackage("mercandalli.com.filespace");
            i.addCategory(Intent.CATEGORY_LAUNCHER);
        }

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("JARVIS")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(serverMessage.getContent()))
                .setLights(getResources().getColor(R.color.actionbar), 500, 2200)
                .setContentText(serverMessage.getContent())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void saveServerMessage(ModelServerMessage serverMessage) {
        if (serverMessage == null)
            return;
        try {
            JSONObject tmp_json = new JSONObject(FileUtils.readStringFile(this.getApplicationContext(), mercandalli.com.filespace.config.Config.getFileName()));
            if (tmp_json.has("settings_1")) {
                JSONObject tmp_settings_1 = tmp_json.getJSONObject("settings_1");

                if (tmp_settings_1.has("listServerMessage_1")) {
                    JSONArray array_listServerMessage_1 = tmp_settings_1.getJSONArray("listServerMessage_1");
                    for (int i = 0; i < array_listServerMessage_1.length(); i++)
                        if ((new ModelServerMessage(array_listServerMessage_1.getJSONObject(i))).equals(serverMessage))
                            return;
                    array_listServerMessage_1.put(serverMessage.toJSONObject());
                    tmp_settings_1.remove("listServerMessage_1");
                    tmp_settings_1.put("listServerMessage_1", array_listServerMessage_1);
                } else {
                    JSONArray array_listServerMessage_1 = new JSONArray();
                    array_listServerMessage_1.put(serverMessage);
                    tmp_settings_1.put("listServerMessage_1", array_listServerMessage_1);
                }
                tmp_json.remove("settings_1");
                tmp_json.put("settings_1", tmp_settings_1);
                FileUtils.writeStringFile(this.getApplicationContext(), mercandalli.com.filespace.config.Config.getFileName(), tmp_json.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}