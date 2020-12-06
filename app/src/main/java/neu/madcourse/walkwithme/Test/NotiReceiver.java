package neu.madcourse.walkwithme.Test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.R;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class NotiReceiver extends BroadcastReceiver {
    private static String CHANNEL_ID = "WalkWithMe";
    private static String TAG = "Notification Recever";
    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;
    private int step;
    private SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        mdb = FirebaseDatabase.getInstance();
        settings = context.getSharedPreferences("WalkWithMe", Context.MODE_PRIVATE);
        //int goal = Integer.parseInt(settings.getString("dailyGoal", null));
        //Log.d(TAG, "goal: " + goal);

        try{
            step_ref = mdb.getReference().child("users").child(LoginActivity.currentUser);
        }catch (Exception e){
        }


        getStep();
//        try{
//            Log.d(TAG, "onReceive: start to sleep");
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Log.d(TAG, "step: " + step + " goal: " + StepsFragment2.dailyGoal);

        //sleep 5s to wait getStep() finish

        if(step <= StepsFragment2.dailyGoal){
//            NotificationHelper notificationHelper = new NotificationHelper(context);
//            notificationHelper.createNotification();
            NotificationCenter notificationCenter = new NotificationCenter(context);
            notificationCenter.createNotification(NofiticationConstants.b1elowGoal2);
        }
    }

    private void getStep() {
        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d(TAG,"access firebase data: " + timestamp);
        try{
            step_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Step Count").child(timestamp).exists()) {
                        Steps steps = dataSnapshot.child("Step Count").child(timestamp).getValue(Steps.class);
                        step = (int) steps.getSteps(); //get previous steps
                        Log.d(TAG,"exist");
                    } else {
                        step = 0;
                    }

                    Log.d(TAG,"Step: " + step);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
            Log.d(TAG,"exception " + e.getLocalizedMessage());
        }
    }


//    class NotificationHelper {
//
//        private Context mContext;
//        private static final String NOTIFICATION_CHANNEL_ID = "10001";
//
//        NotificationHelper(Context context) {
//            mContext = context;
//        }
//
//        void createNotification()
//        {
//
//            Intent intent = new Intent(mContext , MainActivity.class);
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
//                    0 /* Request code */, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
//            mBuilder.setSmallIcon(R.drawable.happy);
//            mBuilder.setContentTitle("You haven't met today's goal")
//                    .setContentText("Content")
//                    .setAutoCancel(true)
//                    .setContentIntent(resultPendingIntent);
//
//            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//            {
//
//                NotificationChannel serviceChannel = new NotificationChannel(
//                        CHANNEL_ID,
//                        "WALKWITHME",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//              mNotificationManager.createNotificationChannel(serviceChannel);
//            }
//            assert mNotificationManager != null;
//            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
//        }
//    }


}
