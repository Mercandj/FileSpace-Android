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
import mercandalli.com.jarvis.model.ModelServerMessage;

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

                sendNotification(serverMessage);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    private void sendNotification(ModelServerMessage serverMessage) {
        Intent i;
        if(serverMessage.isConversationMessage()) {
            i = new Intent(this, ActivityConversation.class);
            i.putExtra("ID_CONVERSATION", serverMessage.getId_conversation());
        }
        else {
            i = new Intent(Intent.ACTION_MAIN);
            PackageManager manager = this.getPackageManager();
            i = manager.getLaunchIntentForPackage("mercandalli.com.jarvis");
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
}