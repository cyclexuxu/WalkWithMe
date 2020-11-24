package neu.madcourse.walkwithme.stepcounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class StepService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final String TAG = "StepService: ";
    private double preMagnitude = 0;
    private double numSteps = 0;


//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        //sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        //accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = this.getSharedPreferences(
                "user", Context.MODE_PRIVATE);

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Log.d("Service Started","Service Started");
        //mInitialized = false;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public double getStepCount(){
        return numSteps;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
//            simpleStepDetector.updateAccel(
//                    event.timestamp, event.values[0], event.values[1], event.values[2]);
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x*x + y*y + z*z);
            double delta = magnitude - preMagnitude;
            preMagnitude = magnitude;

            if(delta > 6){
                numSteps++;
            }
            Log.e(TAG, "current step" + numSteps);
            //textView.setText(""+numSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Service Destroyed");
        sensorManager.unregisterListener(this);
    }
}
