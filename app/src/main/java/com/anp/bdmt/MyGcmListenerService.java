
package com.anp.bdmt;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * @ Jung-Hum Cho Created by anp on 2016. 1. 5..
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "data: " + data);

        // String title = data.getString("title");
        // String message = data.getString("message");
        // sendNotification(title, message);

        String title = data.getString("title");
        String message = data.getString("message");

        try {
            int messageType = Integer.parseInt(data.getString("msg_type"));
            boolean isPopup = Boolean.parseBoolean(data.getString("is_popup"));

            // String pointResults = data.getString("point_result");

            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Message: " + message);
            Log.d(TAG, "MessageType: " + messageType);
            Log.d(TAG, "isPopup: " + isPopup);

            // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
            sendNotification(title, message, messageType, isPopup);
            // sendNotiDialog(title, message);
            // sendNotiService(title, message, messageType, isPopup);
        } catch (Exception e) {
            e.printStackTrace();
            sendNotification(title, message);
        }

    }

    private void sendNotification(String title, String message, int messageType, boolean isPopup) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setSound(defaultSoundUri);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());

        NotificationService service = new NotificationService();

        // Creates an explicit intent for an Activity in your app
        intent = new Intent(this, service.getClass());
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("message_type", messageType);
//        intent.putExtra("point_result", pointResults);

        if (isPopup) {
            stopService(intent);
            startService(intent);
        }

    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * 
     * @param title
     * @param message
     * @param messageType
     * @param isPopup
     * @param pointResults
     */
    private void sendNotification(String title, String message, int messageType, boolean isPopup,
            String pointResults) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setSound(defaultSoundUri);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());

        NotificationService service = new NotificationService();

        // Creates an explicit intent for an Activity in your app
        intent = new Intent(this, service.getClass());
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("message_type", messageType);
        intent.putExtra("point_result", pointResults);

        if (isPopup) {
            stopService(intent);
            startService(intent);
        }

    }

    private void sendNotiDialog(String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);

        // Creates an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, builder.build());

        if (!isScreenOn()) {
            startActivity(new Intent(this, MailActivity.class).putExtra("title", title)
                    .putExtra("message", message).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            startActivity(new Intent(this, MailActivity.class).putExtra("title", title)
                    .putExtra("message", message).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    private void sendNotiService(String title, String message, int messageType, boolean isPopup) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);

        // Creates an explicit intent for an Activity in your app
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("message_type", messageType);

        if (isPopup) {
            startService(intent);
        }

        // The stack builder object will contain an artificial back stack for
        // the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, builder.build());

    }

    private boolean isScreenOn() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return ((PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE))
                    .isInteractive();
        }

        else {
            return ((PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE))
                    .isScreenOn();
        }
    }

}
