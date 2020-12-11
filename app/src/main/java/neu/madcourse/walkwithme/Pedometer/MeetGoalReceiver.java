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

//BroadcastReceiver to check the goal and steps at 5pm

public class MeetGoalReceiver extends BroadcastReceiver {
    private static String TAG = "Notification Recever";
    private final int STEP_THRESHOLD = 300;
    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;
    private int step;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        mdb = FirebaseDatabase.getInstance();

        try{
            step_ref = mdb.getReference().child("users").child(LoginActivity.currentUser);
        }catch (Exception e){
        }
        getStep(context);

    }

    private void getStep(Context context) {
        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        NotificationCenter notificationCenter = new NotificationCenter(context);

        Log.d(TAG,"access firebase data: " + timestamp);
        try{
            step_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Step Count").child(timestamp).exists()) {
                        Steps steps = dataSnapshot.child("Step Count").child(timestamp).getValue(Steps.class);
                        step = (int) steps.getSteps(); //get previous steps
                    } else {
                        step = 0;// step is set to 0
                    }
                    Log.d(TAG,"Step: " + step);
                    Log.d(TAG,"Goal: " + StepsFragment2.dailyGoal);
                    if(StepsFragment2.dailyGoal > step && StepsFragment2.dailyGoal - step <= STEP_THRESHOLD){
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
