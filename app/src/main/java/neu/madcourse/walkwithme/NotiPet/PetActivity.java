package neu.madcourse.walkwithme.NotiPet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import neu.madcourse.walkwithme.R;

public class PetActivity extends AppCompatActivity {
    ImageView corgi;
    PetState petState;

    Button feedButton;
    Button musicButton;
    Button tipsButton;

    ProgressBar healthBar;
    ProgressBar happinessBar;
    ProgressBar knowledgeBar;


    AlarmManager alarmManager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        corgi = (ImageView)findViewById(R.id.corgiImage);
        healthBar = (ProgressBar)findViewById(R.id.healthProgress);
        happinessBar = (ProgressBar) findViewById(R.id.happinessProgress);
        knowledgeBar = (ProgressBar) findViewById(R.id.knowledgeProgress);
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        petState = new PetSleepState();
        showCorgi();
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

        //ObjectAnimator animation = ObjectAnimator.ofFloat(corgi, "translationX", 100f);
        //ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
        //animation.setDuration(2000);
        //corgi.startAnimation(animation);
    }

}