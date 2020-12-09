package neu.madcourse.walkwithme.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.madcourse.walkwithme.Pedometer.NofiticationConstants;
import neu.madcourse.walkwithme.Pedometer.NotificationCenter;
import neu.madcourse.walkwithme.R;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class ProfileFragment extends Fragment {
    private TextView userNameProfile;
    private TextView weightProfile;
    private TextView heightProfile;
    private TextView BMIProfile;
    private TextView levelProfile;
    private SharedPreferences userStr;
    private FirebaseDatabase mdb;
    private DatabaseReference mDatabase;
    private static final String TAG = "Profile page";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userStr = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        mdb = FirebaseDatabase.getInstance();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        userNameProfile = view.findViewById(R.id.userNameProfile);
        weightProfile = view.findViewById(R.id.weightText);
        heightProfile = view.findViewById(R.id.heightText);
        BMIProfile = view.findViewById(R.id.BMIText);
        levelProfile = view.findViewById(R.id.levelText);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("users");

        //String userLogin = intent.getStringExtra("username");
        String userLogin = LoginActivity.currentUser;
        Log.i(TAG, userLogin);

        userRef.addValueEventListener(new ValueEventListener() {
            String name, weight, height, bmi, level;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user: dataSnapshot.getChildren()) {
                    if (user.child("username").getValue().equals(userLogin)) {
                        name = user.child("username").getValue(String.class);
                        weight = String.valueOf(user.child("weight").getValue(Double.class));
                        height = String.valueOf(user.child("height").getValue(Double.class));
                        bmi = String.valueOf(user.child("bmi").getValue(Double.class));
                        int steps = 0;
                        if(user.hasChild("Total Steps")) {
                            steps = user.child("Total Steps").getValue(Integer.class);
                        }
                        int oldLevel = user.child("level").getValue(Integer.class);
                        int newLevel = processSteps(steps);
                        level = String.valueOf(newLevel);
                        if (oldLevel != newLevel) {
                            // do notification
                            NotificationCenter notificationCenter = new NotificationCenter(getContext());
                            if (steps > 2000 && ((steps - 2000 ) / 3500) % 4 == 0) {
                                notificationCenter.createNotification(NofiticationConstants.L2);
                            } else if (steps > 2000 && ((steps - 2000 ) / 3500) % 4 == 1) {
                                notificationCenter.createNotification(NofiticationConstants.L3);
                            } else if (steps > 2000 && ((steps - 2000 ) / 3500) % 4 == 2) {
                                notificationCenter.createNotification(NofiticationConstants.L4);
                            } else if (steps > 2000 && ((steps - 2000 ) / 3500) % 4 == 3) {
                                notificationCenter.createNotification(NofiticationConstants.L5);
                            }
                        }
                        userRef.child(name).child("level").setValue(newLevel);
                        break;
                    }
                }
                userNameProfile.setText(name);
                weightProfile.setText(weight);
                heightProfile.setText(height);
                BMIProfile.setText(bmi);
                levelProfile.setText(level);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, error.toException());
            }
        });

    }

    private int processSteps(int steps) {
        if (steps <= 2000) {
            return 1;
        } else {
            steps -= 2000;
            int n = 2;
            return n + steps / 3500;
        }
    }
}