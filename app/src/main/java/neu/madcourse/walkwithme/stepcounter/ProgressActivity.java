package neu.madcourse.walkwithme.stepcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import neu.madcourse.walkwithme.R;

public class ProgressActivity extends AppCompatActivity{
    private TextView textView;
    private SharedPreferences user;

    //private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private static final String TAG = "Pedometer: ";
    private StepService service = null;
    private int numSteps;
    private double preMagnitude;

    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;

    SharedPreferences sharedPreferences;
    EventBus bus; //Event bus to allow the pedometer service running in background


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        textView = new TextView(this);

        user = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        Intent intent = new Intent(this, StepService.class);
        Log.e(TAG, "Started service");

        mdb = FirebaseDatabase.getInstance();

        try{
            //String address = user.getString("address","");
            step_ref = mdb.getReference().child("users").child("Dan");
        }catch (Exception e){
        }

        textView = findViewById(R.id.steps);
//
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);

        bus = EventBus.getDefault();
        bus.register(this);
        bus.post(true);
        if(intent != null){
            Log.e(TAG, "intent isnot null");
        }

        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateSteps(int num) {
        numSteps = num;
        textView.setText(""+numSteps);
    }



    @Override
    public void onPause() {
        super.onPause();
    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            double magnitude = Math.sqrt(x*x + y*y + z*z);
//            double delta = magnitude - preMagnitude;
//            preMagnitude = magnitude;
//
//            if(delta > 6){
//                numSteps++;
//            }
//            textView.setText(""+numSteps);
//        }
//    }
//
//    @Override
//    public void step(long timeNs) {
//        numSteps++;
//        updateSteps(numSteps);
//        textView.setText("" + numSteps);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "activity destory");
        bus.post(false);
        if (bus.isRegistered(this))
            bus.unregister(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (bus.isRegistered(this))
            bus.unregister(this);
    }

}

