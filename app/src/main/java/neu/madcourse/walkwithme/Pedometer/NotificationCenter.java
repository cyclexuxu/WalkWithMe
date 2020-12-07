package neu.madcourse.walkwithme.Pedometer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.R;

public class NotificationCenter {
    //send notification

    private Context context;
    private static final String CHANNEL_ID = "WalkWithMe";
    //NotificationManager manager = null;

    public NotificationCenter(Context context) {
        this.context = context;
    }

    public void createNotification(NotificationMessage notificationMessage)
    {

        Intent intent = new Intent(context , MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WALKWITHME",
                    NotificationManager.IMPORTANCE_HIGH
            );
            //serviceChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(serviceChannel);

        }

        mNotificationManager.notify(notificationMessage.msgId /* Request Code */, getNotification(notificationMessage));
    }

    public Notification getNotification(NotificationMessage notificationMessage){
        Intent intent = new Intent(context , MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_walkwithme)
                .setContentTitle(notificationMessage.title)
                .setContentText(notificationMessage.body)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), notificationMessage.imgSrc),110,110,false))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .build();

        return notification;
    }


}
