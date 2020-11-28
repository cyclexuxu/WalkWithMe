package neu.madcourse.walkwithme.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.NotiPet.PetActivity;
import neu.madcourse.walkwithme.R;

public class NotificationService extends Service {

    private static final String CHANNEL_ID  = "channel";
    private static final String CHANNEL_NAME  = "channel";
    private static final String CHANNEL_DESCRIPTION  = "description";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getApplicationInfo().enabled)
                showNotification();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {
        Intent intent = new Intent(this, PetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        }
        else {
            builder = new NotificationCompat.Builder(this);
        }


        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("hehe")
                .setContentText("walkwithme")
                .setSmallIcon(R.drawable.run)
                .setStyle(new NotificationCompat.InboxStyle())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0,notification);
    }
}
