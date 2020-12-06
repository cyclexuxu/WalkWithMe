package neu.madcourse.walkwithme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import neu.madcourse.walkwithme.Test.CheckAppRunService;
import neu.madcourse.walkwithme.profile.ProfileActivity;
import neu.madcourse.walkwithme.profile.ProfileFragment;
import neu.madcourse.walkwithme.ranking.RankingActivity;
import neu.madcourse.walkwithme.Test.Constants;
import neu.madcourse.walkwithme.Test.StepService3;
import neu.madcourse.walkwithme.Test.StepsFragment2;

import neu.madcourse.walkwithme.NotiPet.PetActivity;

import neu.madcourse.walkwithme.rankingFra.RankFragment;
import neu.madcourse.walkwithme.stepcounter.ProgressActivity;
import neu.madcourse.walkwithme.userlog.LoginActivity;
import neu.madcourse.walkwithme.userlog.LogoutFragment;


public class MainActivity extends AppCompatActivity {
    private Button login;
    FragmentTransaction fragmentTransaction;
    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    public final static String CHANNEL = "WalkWithMe";
    public final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //login = (Button) findViewById(R.id.btnLogin);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new StepsFragment2()).commit();
        }

        settings = getSharedPreferences(CHANNEL, MODE_PRIVATE);
        editor = settings.edit();

        //Is it the first time to use the app
        if(!settings.contains("preRun")) {
            Log.d(TAG, "This is the first time to run this app");
            //enable notification
           //editor.putLong("preRun", System.currentTimeMillis());
            editor.putBoolean("notification", true);

            Log.d(TAG, "Enable notification");
        }
        editor.putLong("preRun", System.currentTimeMillis());
        editor.commit();

        Intent startIntent = new Intent(this, StepService3.class);
        startIntent.setAction(Constants.START_FOREGROUND);
        startService(startIntent);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_logout:
                    selectedFragment = new LogoutFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.nav_pedometer:
                    selectedFragment = new StepsFragment2();
                    break;
                case R.id.nav_rank:
                    selectedFragment = new RankFragment();
                    break;
                case R.id.nav_pet:
                    selectedFragment = new PetFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
            return true;
        };


    // method for open ranking activity
    public void openRankingActivity(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    //Method for Notification Button
    public void openPetActivity(View view){
        Intent intent = new Intent(this, PetActivity.class);
        startActivity(intent);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yes_logout:
                Intent stopIntent = new Intent(this, StepService3.class);
                //stopIntent.setAction(Constants.STOP_FOREGROUND);
                stopService(stopIntent);
                Intent login = new Intent(this, LoginActivity.class);
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1); //close all notification for current users
                startActivity(login);
                break;
            case R.id.not_logout:
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;
        }
    }

    public void startProgressActivity(View view){
        startActivity(new Intent(MainActivity.this, ProgressActivity.class));
    }

    public void startStepsFragment(View view){
       //startActivity(new Intent(MainActivity.this, StepsFragment2.class));
        showFragment(new StepsFragment2());
    }

    private void showFragment(Fragment frag) {
        FragmentManager manager = getSupportFragmentManager();
        fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, frag).commit();
        manager.executePendingTransactions();
    }
    @Override
    protected void onStop() {
        super.onStop();
        editor.putLong("preRun", System.currentTimeMillis());
        editor.commit();
        Log.v(TAG, "On Stop, Starting CheckRecentRun service...");
        startService(new Intent(this,  CheckAppRunService.class));
//        Intent startIntent = new Intent(this, StepService3.class);
//        startIntent.setAction(Constants.START_FOREGROUND);
//        startService(startIntent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putLong("preRun", System.currentTimeMillis());
        editor.commit();
        Log.v(TAG, "On destory, Starting CheckRecentRun service...");
        startService(new Intent(this,  CheckAppRunService.class));

        Intent stopIntent = new Intent(this, StepService3.class);
        stopIntent.setAction(Constants.STOP_FOREGROUND);
        startService(stopIntent);
    }

}