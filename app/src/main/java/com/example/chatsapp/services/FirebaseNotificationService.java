package com.example.chatsapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.AllConstants;
import com.example.chatsapp.activity.MessageActivity;
import com.example.chatsapp.utils.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirebaseNotificationService extends FirebaseMessagingService {

    private Util util = new Util();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //XU ly khi nhan duoc message
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
            String title = map.get("title");
            String message = map.get("message");
            String hisID = map.get("hisID");
            String hisImage = map.get("hisImage");
            String hisName = map.get("hisName");
            String chatID = map.get("chatID");

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                createOreoNotification(title, message, hisID, hisImage, hisName, chatID);
            } else {
                createNomralNotification(title, message, hisID, hisImage, hisName, chatID);
            }
        }
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        //Phương thức sẽ đc gọi khi 1 token mới được sinh ra
        updateToken(s);
        super.onNewToken(s);
    }

    //Update data token vào DatabaseRealtime
    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(util.getUID());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        databaseReference.updateChildren(map);
    }

    private void createNomralNotification(String title, String message, String hisID, String hisImage, String hisName, String chatID) {

        //Sound khi co thong bao
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, AllConstants.CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_chat)
                .setSound(uri)
                .setColor(getColor(R.color.color_Accent));

        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("chatID", chatID);
        intent.putExtra("hisID", hisID);
        intent.putExtra("hisImage", hisImage);
        intent.putExtra("hisName", hisName);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(85 - 65), builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOreoNotification(String title, String message, String hisID, String hisImage, String hisName, String chatID) {
        NotificationChannel channel = new NotificationChannel(AllConstants.CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Message Description");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("chatID", chatID);
        intent.putExtra("hisID", hisID);
        intent.putExtra("hisImage", hisImage);
        intent.putExtra("hisName", hisName);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(this, AllConstants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(getColor(R.color.color_Accent))
                .setSmallIcon(R.drawable.ic_chat)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(new Random().nextInt(85 - 65), notification);
    }
}
