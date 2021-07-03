package com.oileemobile.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import com.oileemobile.R;
import com.oileemobile.activity.OrderDetailActivity;
import com.oileemobile.activity.SplashActivity;
import com.oileemobile.utils.Constants;
import com.oileemobile.utils.MyLogger;
import com.oileemobile.utils.PrefManager;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-09-18 22:49
 **/
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "144";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        PrefManager.putString(PrefManager.FCM_TOKEN, s);
        MyLogger.error("Firebase Token Changed", "Token: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        printNotificationData(data);
        String title = data.get("title");
        String message = data.get("description");
        String type = data.get("key");
        int jobId = -1;
        try {
            if (data.containsKey("data")) {
                JSONObject dataObject = new JSONObject(data.get("data"));
                if (dataObject.has("order_id"))
                    jobId = dataObject.getInt("order_id");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sendNotification(title, message, type, jobId);
    }

    private String getNotificationDataAsString(Map<String, String> dataMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for(String key : dataMap.keySet()) {
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(dataMap.get(key));
            stringBuilder.append(",");
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private void printNotificationData(Map<String, String> dataMap) {
        MyLogger.error("Notification data", "Content: " + getNotificationDataAsString(dataMap));
    }

    private Intent getNotificationIntent(String type, int orderId) {
        Intent intent;
        if (PrefManager.getInt(PrefManager.USER_TYPE, Constants.TYPE_CUSTOMER) == Constants.TYPE_CUSTOMER) {
            switch (type) {
                case Constants.NotificationType.TECHNICIAN_ACCEPTED_JOB_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_REACHED_AT_WAREHOUSE_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_IS_LEAVING_WAREHOUSE_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_IS_IN_ROUTE_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_ARRIVED_AT_LOCATION_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_STARTING_WORK_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_WORK_COMPLETED_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_PAYMENT_SUBMITTED_NOTIFICATION:
                case Constants.NotificationType.TECHNICIAN_BOOKING_CLOSED_NOTIFICATION:
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.JOB_ID, orderId);
                    intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtras(bundle);
                    break;

                default:
                    intent = new Intent(this, SplashActivity.class);
            }
        } else {
            intent = new Intent(this, SplashActivity.class);
        }

        return intent;
    }

    private void sendNotification(String title, String messageBody, String type, int jobId) {
        NotificationCompat.Builder notificationBuilder;

        Intent intent = getNotificationIntent(type, jobId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(messageBody);
            notificationManager.createNotificationChannel(channel);

            /*notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent);*/



            //notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this


        }

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon_white)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon_white))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
