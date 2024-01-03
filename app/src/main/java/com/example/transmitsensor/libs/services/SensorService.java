package com.example.transmitsensor.libs.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.example.transmitsensor.R;
import com.example.transmitsensor.controllers.ManageGPS;
import com.example.transmitsensor.controllers.ManageSensor;

public class SensorService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final int NOTIFICATION_ID = 1;
    ManageSensor manageSensor;
    ManageGPS manageGPS;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manageSensor = ManageSensor.getInstance();
        manageGPS = ManageGPS.getInstance();

        if(manageSensor == null || manageGPS == null) return Service.START_STICKY;

        manageSensor.registerListener();
        manageGPS.startLocationUpdates(getApplicationContext());

        startForeground(NOTIFICATION_ID, getNotification());

        return START_STICKY;
    }

    Notification getNotification() {
        createNotificationChannel();
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Transmit Sensor")
                .setContentText("is running in the background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(Notification.PRIORITY_LOW)
                .build();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the sensor listener when the service is destroyed
        if (manageSensor != null) {
            manageSensor.unregisterListener();
        }
        if (manageGPS != null) {
            manageGPS.removeListener();
        }
        stopForeground(true);
        stopSelf();
    }
}
