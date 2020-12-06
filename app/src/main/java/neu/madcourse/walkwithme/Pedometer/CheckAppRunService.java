package neu.madcourse.walkwithme.Pedometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import neu.madcourse.walkwithme.MainActivity;

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

        Log.d(TAG, "Set Alarm to check last active");
        SharedPreferences settings = getSharedPreferences(MainActivity.CHANNEL, MODE_PRIVATE);

        // Are notifications enabled?
        if (settings.getBoolean("notification", true)) {
            // Is it time for a notification?
            if (settings.getLong("preRun", Long.MAX_VALUE) < System.currentTimeMillis() - delay) {
                NotificationCenter notificationCenter = new NotificationCenter(getApplicationContext());
                notificationCenter.createNotification(NofiticationConstants.THREE_DAYS_INACTIVE);
            }

        } else {
            Log.i(TAG, "Notifications are disabled");
        }

        // Set an alarm for the next time this service should run:
        setAlarm();
        stopSelf();
    }

    public void setAlarm() {

        Intent serviceIntent = new Intent(this, CheckAppRunService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        Log.v(TAG, "Recent activity check alarm set");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}