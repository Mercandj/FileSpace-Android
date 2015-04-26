package mercandalli.com.jarvis.notificationpush;

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

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.ActivityConversation;

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
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				
				for (int i = 0; i < 3; i++) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
				}
                if(extras.get("id_conversation") != null) {
                    sendNotification("" + extras.get(Config.MESSAGE_KEY), "" + extras.get("id_conversation"));
                }
                else
				    sendNotification("" + extras.get(Config.MESSAGE_KEY));
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		Intent i = new Intent(Intent.ACTION_MAIN);
		PackageManager manager = this.getPackageManager();
		i = manager.getLaunchIntentForPackage("mercandalli.com.jarvis");
		i.addCategory(Intent.CATEGORY_LAUNCHER);		
		
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_notification)
				.setContentTitle("JARVIS")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setLights(getResources().getColor(R.color.actionbar), 500, 2200)
				.setContentText(msg)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

    private void sendNotification(String msg, String id_conversation) {
        Intent i = new Intent(this, ActivityConversation.class);
        i.putExtra("ID_CONVERSATION", id_conversation);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("JARVIS")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setLights(getResources().getColor(R.color.actionbar), 500, 2200)
                .setContentText(msg)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}