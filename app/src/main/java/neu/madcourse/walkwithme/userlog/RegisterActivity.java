package neu.madcourse.walkwithme.userlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.profile.ProfileActivity;
import neu.madcourse.walkwithme.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName;
    private EditText password;
    private EditText weight;
    private EditText height;
    private Button registerButton;
    private DatabaseReference mDatabase;

    private static final String TAG = "Rester page";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDatabase = FirebaseDatabase. getInstance ().getReference();
        userName = (EditText) findViewById(R.id.regiLogin);
        password = (EditText) findViewById(R.id.regiPassword);
        weight = (EditText) findViewById(R.id.editTextWeight);
        height = (EditText) findViewById(R.id.editTextHeight);
        registerButton = (Button) findViewById(R.id.register_submit);
    }

    public void onRegisterClick(View view) {

        final String username = userName.getText().toString();
        final String password = Md5Encode.md5Encryption(this.password.getText().toString());
        if (username.equals("") || password.equals("") || weight.getText().toString().equals("") || height.getText().toString().equals("")) {
            Log.i(TAG, "checked out the null string");
            Toast.makeText(getApplicationContext(), "All information needed", Toast.LENGTH_LONG).show();
            return;
        }
        final double wei = Double.parseDouble(weight.getText().toString());
        final double hei = Double.parseDouble(height.getText().toString());
        final User user = new User(username, password, System.currentTimeMillis(), wei, hei);
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(username)) {
                    Log.i(TAG, "" + getApplicationContext());
                    Toast.makeText(getApplicationContext(), "username is already registered", Toast.LENGTH_SHORT).show();
                } else if (username.length() != 0 && password.length() != 0 && weight.getText().toString() != "" && height.getText().toString() != "") {
                    mDatabase.child("users").child(user.getUsername()).setValue(user);
                    mDatabase.child("users").child(user.getUsername()).child("level").setValue(1);
                    mDatabase.child("users").child(user.getUsername()).child("meatNum").setValue(3);
                    mDatabase.child("users").child(user.getUsername()).child("healthNum").setValue(0);
                    mDatabase.child("users").child(user.getUsername()).child("happinessNum").setValue(0);
                    mDatabase.child("users").child(user.getUsername()).child("knowledgeNum").setValue(0);
                    mDatabase.child("users").child(user.getUsername()).child("petLevel").setValue(0);
                    mDatabase.child("users").child(user.getUsername()).child("redeemedSteps").setValue(0);
                    //Toast.makeText(getApplicationContext(), username + " successfully registered", Toast.LENGTH_SHORT).show();
                    // go to profile
                    LoginActivity.currentUser = username;
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "All information needed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}