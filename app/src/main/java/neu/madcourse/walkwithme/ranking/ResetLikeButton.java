package neu.madcourse.walkwithme.ranking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.madcourse.walkwithme.userlog.LoginActivity;

public class ResetLikeButton extends BroadcastReceiver{

        private DatabaseReference resetLikesClick;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Reset likes button : ", ".....");
            //int goal = Integer.parseInt(settings.getString("dailyGoal", null));
            //Log.d(TAG, "goal: " + goal);

            resetLikesClick = FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.currentUser).child("Rankings");
            resetLikesClick.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //itemRankList = new ArrayList<>();
                    for (DataSnapshot d : snapshot.getChildren()) {

                        String username = d.child("username").getValue(String.class);
                        Log.d("Reset alarm", "onDataChange: " + username);
                        resetLikesClick.child(username).child("likeClicked").setValue(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
}
