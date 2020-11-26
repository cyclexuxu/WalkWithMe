package neu.madcourse.walkwithme.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.madcourse.walkwithme.R;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameProfile;
    private TextView weightProfile;
    private TextView heightProfile;
    private TextView BMIProfile;
    private TextView levelProfile;

    private static final String TAG = "Profile page";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userNameProfile = findViewById(R.id.userNameProfile);
        weightProfile = findViewById(R.id.weightText);
        heightProfile = findViewById(R.id.heightText);
        BMIProfile = findViewById(R.id.BMIText);
        levelProfile = findViewById(R.id.levelText);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("users");

        Intent intent = getIntent();
        String userLogin = intent.getStringExtra("username");
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
                        level = "1"; // to be updated
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


}