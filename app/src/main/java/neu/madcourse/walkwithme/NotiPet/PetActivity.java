package neu.madcourse.walkwithme.NotiPet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.madcourse.walkwithme.Notifications.NotificationService;
import neu.madcourse.walkwithme.R;

public class PetActivity extends AppCompatActivity {

    String userName = "Claire ";

    ImageView corgi;
    PetState petState;

    Button feedButton;
    Button musicButton;
    Button tipsButton;

    ProgressBar healthBar;
    ProgressBar happinessBar;
    ProgressBar knowledgeBar;

    ImageView[] meatViews = new ImageView[7];

    AlarmManager alarmManager;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        corgi = (ImageView)findViewById(R.id.corgiImage);

        healthBar = (ProgressBar)findViewById(R.id.healthProgress);
        happinessBar = (ProgressBar) findViewById(R.id.happinessProgress);
        knowledgeBar = (ProgressBar) findViewById(R.id.knowledgeProgress);
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        meatViews[0] = findViewById(R.id.meat1);
        meatViews[1] = findViewById(R.id.meat2);
        meatViews[2] = findViewById(R.id.meat3);
        meatViews[3] = findViewById(R.id.meat4);
        meatViews[4] = findViewById(R.id.meat5);
        meatViews[5] = findViewById(R.id.meat6);
        meatViews[6] = findViewById(R.id.meat7);

        petState = new PetSleepState();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(userName).child("meatNum");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petState.setMeat(snapshot.getValue(Integer.class));
                showCorgi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 3000, 100000,
//               PendingIntent.getService(getApplicationContext(), 0, new Intent(this, NotificationService.class), 0) );

        showCorgi();

        corgi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //ObjectAnimator animation = ObjectAnimator.ofFloat(corgi, "translationX", 100f);
                corgi.setImageResource(R.drawable.run);
                ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
                animation.setDuration(3000);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        showCorgi();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                corgi.setAnimation(animation);
                corgi.animate().start();
                return true;
            }
        });

        feedButton = (Button) findViewById(R.id.feedButton);
        feedButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {
                    petState = petState.feed();
                } catch (InsufficientMeatException e) {
                    System.out.println("not enough meat");
                    return;
                }
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "eat", new AlarmManager.OnAlarmListener() {
                    @Override
                    public void onAlarm() {
                        petState = petState.timeout();
                        showCorgi();
                    }
                }, null);
                showCorgi();
            }
        });
        musicButton = (Button) findViewById(R.id.musicButton);
        musicButton.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                petState = petState.music();
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "music", new AlarmManager.OnAlarmListener() {
                    @Override
                    public void onAlarm() {
                        petState = petState.timeout();
                        showCorgi();
                    }
                }, null);

                showCorgi();
            }
        });

        tipsButton = (Button) findViewById(R.id.tipsButton);
        tipsButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                petState = petState.tip();
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "tips", new AlarmManager.OnAlarmListener() {
                    @Override
                    public void onAlarm() {
                        petState = petState.timeout();
                        showCorgi();
                    }
                }, null);
                showCorgi();
            }
        });

    }

    private void showCorgi(){
        corgi.setImageResource(petState.getImage());
        healthBar.setProgress(petState.getcHealth());
        happinessBar.setProgress(petState.getcHappiness());
        knowledgeBar.setProgress(petState.getcKnowledge());

        for (int i = 0; i < 7; i++) {
            if (i < petState.getMeat()) {
                meatViews[i].setVisibility(View.VISIBLE);
            } else {
                meatViews[i].setVisibility(View.INVISIBLE);
            }
        }

    }

}