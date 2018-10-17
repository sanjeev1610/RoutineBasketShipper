package com.mobiapp4u.pc.routinebasketshipper.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.mobiapp4u.pc.routinebasketshipper.Common.Common;
import com.mobiapp4u.pc.routinebasketshipper.Helper.NotificationHelper;
import com.mobiapp4u.pc.routinebasketshipper.HomeActivity;
import com.mobiapp4u.pc.routinebasketshipper.MainActivity;
import com.mobiapp4u.pc.routinebasketshipper.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificationAPI26(remoteMessage);

            } else {
                sendNotification(remoteMessage);

            }
        }
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        Map<String,String > data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        //here we will fix click notification-> go to order list
        PendingIntent pendingIntent;
        NotificationHelper helper;
        Notification.Builder builder;
        if(Common.currentShipper!=null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           // intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper = new NotificationHelper(this);
            builder = helper.getRBChannelNotification(title, message, pendingIntent, defaultSound);
            //get random id for notification to show all notification
            helper.getManager().notify(new Random().nextInt(), builder.build());
        }else {//fix crash if notifcation send from news system(Common.currentuser == null)
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper = new NotificationHelper(this);
            builder = helper.getRBChannelNotification(title, message, defaultSound);

            helper.getManager().notify(new Random().nextInt(), builder.build());
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String,String > data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSound);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());

    }
}
