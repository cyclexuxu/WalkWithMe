package neu.madcourse.walkwithme.Test;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.R;

public class CheckAppRunService extends Service {
    //If user hasn't use the app for 3 days, send notification

    private final static String TAG = "CheckRecentPlay";
    private static Long MILLISECS_PER_DAY = 86400000L;
    private static Long MILLISECS_PER_MIN = 60000L;
    private static String CHANNEL_ID = "WalkWithMe";

    //private static long delay = MILLISECS_PER_MIN ;   // 30s (for testing)
    private static long delay = MILLISECS_PER_DAY * 3;   // 3 days

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service started");
        SharedPreferences settings = getSharedPreferences(MainActivity.CHANNEL, MODE_PRIVATE);

        // Are notifications enabled?
        if (settings.getBoolean("notification", true)) {
            // Is it time for a notification?
            if (settings.getLong("preRun", Long.MAX_VALUE) < System.currentTimeMillis() - delay)
                sendNotification();

        } else {
            Log.i(TAG, "Notifications are disabled");
        }

        // Set an alarm for the next time this service should run:
        setAlarm();

        Log.v(TAG, "Service stopped");
        stopSelf();
    }

    public void setAlarm() {

        Intent serviceIntent = new Intent(this, CheckAppRunService.class);
        PendingIntent pi = PendingIntent.getService(this, 131313, serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi);
        Log.v(TAG, "Alarm set");
        Log.v(TAG, System.currentTimeMillis() + delay + "");
    }

    public void sendNotification() {

        Intent intent = new Intent(this , MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.happy);
        mBuilder.setContentTitle("We miss you")
                .setContentText("Content")
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WALKWITHME",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mNotificationManager.createNotificationChannel(serviceChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());

        Log.v(TAG, "Notification sent");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}