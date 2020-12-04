package neu.madcourse.walkwithme.Test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.R;

public class NotiReceiver extends BroadcastReceiver {
    private static String CHANNEL_ID = "WalkWithMe";
    private static String TAG = "Notification Recever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification();

    }

    class NotificationHelper {

        private Context mContext;
        private static final String NOTIFICATION_CHANNEL_ID = "10001";

        NotificationHelper(Context context) {
            mContext = context;
        }

        void createNotification()
        {

            Intent intent = new Intent(mContext , MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                    0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
            mBuilder.setSmallIcon(R.drawable.happy);
            mBuilder.setContentTitle("You haven't met today's goal")
                    .setContentText("Content")
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

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
        }
    }


}
