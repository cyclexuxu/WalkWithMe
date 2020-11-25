package neu.madcourse.walkwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;

import neu.madcourse.walkwithme.stepcounter.ProgressActivity;
import neu.madcourse.walkwithme.userlog.LoginActivity;



public class MainActivity extends AppCompatActivity {
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.btnLogin);

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

}