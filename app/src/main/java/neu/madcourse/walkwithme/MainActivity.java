package neu.madcourse.walkwithme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;


import neu.madcourse.walkwithme.Ranking.RankingActivity;
import neu.madcourse.walkwithme.Test.Constants;
import neu.madcourse.walkwithme.Test.StepService3;
import neu.madcourse.walkwithme.Test.StepsFragment2;

import neu.madcourse.walkwithme.NotiPet.PetActivity;

import neu.madcourse.walkwithme.stepcounter.ProgressActivity;
import neu.madcourse.walkwithme.userlog.LoginActivity;



public class MainActivity extends AppCompatActivity {
    private Button login;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.btnLogin);

        Intent startIntent = new Intent(this, StepService3.class);
        startIntent.setAction(Constants.START_FOREGROUND);
        startService(startIntent);

    }
    // method for open ranking activity
    public void openRankingActivity(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }



    //Method for Notification Button
    public void openPetActivity(View view){
        Intent intent = new Intent(this, PetActivity.class);
        startActivity(intent);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                Intent buttonGrid = new Intent(this, LoginActivity.class);
                startActivity(buttonGrid);
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
    protected void onDestroy() {
        super.onDestroy();
        Intent stopIntent = new Intent(this, StepService3.class);
        stopIntent.setAction(Constants.STOP_FOREGROUND);
        startService(stopIntent);
    }

}