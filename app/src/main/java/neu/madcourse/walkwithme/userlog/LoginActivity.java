package neu.madcourse.walkwithme.userlog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.profile.ProfileActivity;
import neu.madcourse.walkwithme.R;
import neu.madcourse.walkwithme.ranking.DRankingData;

public class LoginActivity extends AppCompatActivity {
    public static String currentUser;
    private EditText userName;
    private EditText password;
    private Button loginButton;
    private Button registerButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "Login page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase. getInstance ().getReference();
        userName = (EditText) findViewById(R.id.editTextLogin);
        password = (EditText) findViewById(R.id.editTextPassword );
        loginButton = (Button) findViewById(R.id.submit );
        registerButton = (Button) findViewById(R.id.register );
    }

    public void onLoginClick(View view) {
        final String username = userName.getText().toString();
        final String password = Md5Encode.md5Encryption(this.password.getText().toString());
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(username) &&
                        (password.equals(dataSnapshot.child(username).child("password").getValue()))) {

                    currentUser = username;
                    DRankingData dRankingData = new DRankingData();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    Log.i( TAG, "You successfully login");
                } else {
                    Toast.makeText(getApplicationContext(),"Please login again", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void onRegisterClick(View view) {
        final String username = userName.getText().toString();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(username)) {
                    Log.i(TAG, "" + getApplicationContext());
                    Toast.makeText(getApplicationContext(), "username is already registered", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}