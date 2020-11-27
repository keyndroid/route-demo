package com.android.based.data.myapplication.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.based.data.myapplication.R;
import com.android.based.data.myapplication.ui.activity.MapActivity;

import java.util.Map;

import static com.android.based.data.myapplication.util.UtilsObjectKt.STARTFOREGROUND_ACTION;
import static com.android.based.data.myapplication.util.UtilsObjectKt.STARTFOREGROUND_MESSAGE;
import static com.android.based.data.myapplication.util.UtilsObjectKt.STOPFOREGROUND_ACTION;

public class RouteService extends Service {

    public static final int NOTIF_ID = 1;
    public static final String NOTIF_CHANNEL_ID = "Location Update";

    private static boolean isStarted=false;
    public static boolean isServiceStarted(){
        return isStarted;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStarted=true;
        // do your jobs here

//        startForeground();
//
//        return super.onStartCommand(intent, flags, startId);
        if (intent!=null && intent.getAction()!=null){
            if (intent.getAction().equals(STARTFOREGROUND_ACTION)) {
//            Log.i(LOG_TAG, "Received Start Foreground Intent ");
                // your start service code
                startForeground(this, "Updating background location...");
            }else if (intent.getAction().equals(STARTFOREGROUND_MESSAGE)) {
//            Log.i(LOG_TAG, "Received Stop Foreground Intent");
                //your end servce code
                String message = intent.getStringExtra(STARTFOREGROUND_MESSAGE);
                startForeground(this,message);
            } else if (intent.getAction().equals(STOPFOREGROUND_ACTION)) {
//            Log.i(LOG_TAG, "Received Stop Foreground Intent");
                //your end servce code
                isStarted=false;
                stopForeground(true);
                stopSelf();
            }
        }

        return START_STICKY;
    }

    public void startForeground(Context context, String message) {
        startForeground(NOTIF_ID, getNotification(context, message));
    }

    public static Notification getNotification(Context context, String message) {
        Intent notificationIntent = new Intent(context, MapActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        String channeldId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channeldId = createNotificationChannel(context, NOTIF_CHANNEL_ID, NOTIF_CHANNEL_ID);
        }
        Notification notification = new NotificationCompat.Builder(context,
                channeldId) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_car_foreground)
                .setContentTitle(context.getString(R.string.app_name))
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .build();
        return notification;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String createNotificationChannel(Context context, String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;

    }

}