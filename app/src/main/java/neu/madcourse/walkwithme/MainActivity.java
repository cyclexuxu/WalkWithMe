package neu.madcourse.walkwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Method for Notification Button
    public void openPetActivity(View view){
        Intent intent = new Intent(this, PetActivity.class);
        startActivity(intent);
    }
}