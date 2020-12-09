package neu.madcourse.walkwithme.Pedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import neu.madcourse.walkwithme.userlog.LoginActivity;

public class MeetGoalReceiver extends BroadcastReceiver {
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
        //get steps to check whether need to send notification
        getStep(context);

    }

    private void getStep(Context context) {
        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        NotificationCenter notificationCenter = new NotificationCenter(context);

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
                    Log.d(TAG,"Goal: " + StepsFragment2.dailyGoal);
                    if(StepsFragment2.dailyGoal > step && StepsFragment2.dailyGoal - step <= 300){
                        //close to goal
                        Log.d(TAG, "onDataChange: diff is less than 300");
                        notificationCenter.createNotification(NofiticationConstants.b1elowGoal);
                    }else if(StepsFragment2.dailyGoal > step){
                        Log.d(TAG, "onDataChange: diff is more than 300");
                        notificationCenter.createNotification(NofiticationConstants.b1elowGoal2);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
            Log.d(TAG,"exception " + e.getLocalizedMessage());
        }
    }
}
