package neu.madcourse.walkwithme.Test;

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

    private Context context;
    private static final String CHANNEL_ID = "WalkWithMe";
    //NotificationManager manager = null;

    public NotificationCenter(Context context) {
        this.context = context;
    }
//
//    private void createNotificationChannel() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "WALKWITHME",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }
//    private Notification getNotification(String title, String body, int id){
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_ONE_SHOT);
//        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.happy),97,128,false))
//                .setSmallIcon(R.drawable.happy)
//                .setContentIntent(resultPendingIntent)
//                .setOngoing(true)
//                .setAutoCancel(true)
//                .build();
//
//        return notification;
//    }

    public void createNotification(NotificationMessage notificationMessage)
    {

        Intent intent = new Intent(context , MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_walkwithme);
        mBuilder.setContentTitle(notificationMessage.title)
                .setContentText(notificationMessage.body)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), notificationMessage.imgSrc),110,110,false))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WALKWITHME",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mNotificationManager.createNotificationChannel(serviceChannel);
        }

        mNotificationManager.notify(notificationMessage.msgId /* Request Code */, mBuilder.build());
    }


}
