package com.mobiapp4u.pc.routinebasketshipper.Helper;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.mobiapp4u.pc.routinebasketshipper.R;


public class NotificationHelper extends ContextWrapper {

    private static final String RB_CHANNEL_ID = "com.example.pc.routinebasketshipper";
    private static final String RB_CHANNEL_NAME = "Routine Basket";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel rbChannel = new NotificationChannel(RB_CHANNEL_ID,RB_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        rbChannel.enableLights(false);
        rbChannel.enableVibration(true);
        rbChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(rbChannel);
    }

    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getRBChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri){
        return new Notification.Builder(getApplicationContext(),RB_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp);

    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getRBChannelNotification(String title, String body, Uri soundUri){
        return new Notification.Builder(getApplicationContext(),RB_CHANNEL_ID)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp);

    }


}
