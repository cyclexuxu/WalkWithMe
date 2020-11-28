package neu.madcourse.walkwithme.Test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import neu.madcourse.walkwithme.MainActivity;

public class StepService3 extends Service implements SensorEventListener {

    //We use accelerotometer to detect steps
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Sensor stepCounter;


    //Variables used in calculations
    private double preMagnitude = 0;
    private int step = 0;
    private long stepCount = 0;
    private long lastSteps = 0;
    private String compassOrientation;
    private double lastDistance = 0;
    private int prevStepCount = 0;
    private long stepTimestamp = 0;
    private long startTime = 0;
    long timeInMilliseconds = 0;
    long elapsedTime = 0;
    long updatedTime = 0;
    private int speed = 0;
    private double distance = 0;
    private float[] accelValues;
    private float[] magnetValues;
    private String timeString;
    private String elapsedString;

    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;

    private boolean isActive = false;

    private SharedPreferences user;
    private Handler handler = new Handler();
    String CHANNEL_ID = "WalkWithMe";
    int notification_id = 1711101;
    String TAG = "Step Count";

    private IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        private StepService3 service;

        public MyBinder() {
            this.service = StepService3.this;
        }

        public StepService3 getService() {
            return service;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mdb = FirebaseDatabase.getInstance();

        try{
            String address = user.getString("address","");
            step_ref = mdb.getReference().child("users").child("Dan");
        }catch (Exception e){
        }
        Log.d(TAG,"increate");
        fetchData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch(action){

            case Constants.START_FOREGROUND :
                Log.d(TAG,"starting service");
                break;

            case Constants.RESET_COUNT :
                resetCount();
                break;

            case Constants.STOP_SAVE_COUNT :
                stopForegroundService(true);

            case Constants.STOP_FOREGROUND :
                Log.d(TAG,"stopping service");
                stopForeground(true);
                unregisterSensors();
                handler.removeCallbacks(timerRunnable);
                stopSelf();
                break;
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Get sensor values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelValues = event.values;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x * x + y * y + z * z);
            double delta = magnitude - preMagnitude;
            preMagnitude = magnitude;

            if (delta > 6) {
                step++;
            }

            final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            step_ref.child(timestamp).child("steps").setValue(step);
        }
                //step_ref.child("Step Count").setValue(step);

                //step_ref.child().setValue(step);

//            case (Sensor.TYPE_MAGNETIC_FIELD):
//                magnetValues = event.values;
//                break;
//            case (Sensor.TYPE_STEP_COUNTER):
//                if (prevStepCount < 1) {
//                    prevStepCount = (int) event.values[0];
//                }
//                step_ref.child("Step Count").setValue("step counter");
//                calculateSpeed(event.timestamp, (int) (event.values[0] - prevStepCount - stepCount));
//                countSteps((int)(event.values[0] - prevStepCount - stepCount));
//                break;
//            case (Sensor.TYPE_STEP_DETECTOR):
//                if (stepCounter == null) {
//                    countSteps((int) event.values[0]);
//                    calculateSpeed(event.timestamp, 1);
//                }
//                break

//        if (accelValues != null && magnetValues != null) {
//            float rotation[] = new float[9];
//            float orientation[] = new float[3];
//            if (SensorManager.getRotationMatrix(rotation, null, accelValues, magnetValues)) {
//                SensorManager.getOrientation(rotation, orientation);
//                float azimuthDegree = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
//                float orientationDegree = Math.round(azimuthDegree);
//                getOrientation(orientationDegree);
//            }
//        }
    }

    public long getStepCount(){
        return stepCount;
    }

    public boolean isActive(){
        return  isActive;
    }

    public void startForegroundService(){
        registerSensors();
        //startTime = SystemClock.uptimeMillis() + 1000;
        startForeground(notification_id,getNotification("Starting Step Counter Service",""));
        handler.postDelayed(timerRunnable,1000);
        isActive = true;
    }

    public void stopForegroundService(boolean persist){
        unregisterSensors();
        handler.removeCallbacks(timerRunnable);
        isActive = false;
        startForeground(notification_id,getNotification("Stopping  Step Counter Service",""));
        stopForeground(true);
        //elapsedTime = elapsedTime + timeInMilliseconds;
        if(persist)
            persistSteps();
    }

    public void resetCount(){
        step = 0;
        //distance = 0;
        //startTime = SystemClock.uptimeMillis();
        //updatedTime = elapsedTime;
    }

    private void resetVariables(){

    }

    //Calculates the number of steps and the other calculations related to them
//    private void countSteps(int step) {
//        //Step count
//        stepCount += step;
//
//        //Distance calculation
//        distance = stepCount * 0.8; //Average step length in an average adult
//    }

    //Calculated the amount of steps taken per minute at the current rate
//    private void calculateSpeed(long eventTimeStamp, int steps) {
//
//        long timestampDifference = eventTimeStamp - stepTimestamp;
//        stepTimestamp = eventTimeStamp;
//        double stepTime = timestampDifference /1000000000.0;
//        speed = (int) (60 / stepTime);
//    }

    //Show cardinal point (compass orientation) according to degree
   // private void getOrientation(float orientationDegree) {
//
//        if (orientationDegree >= 0 && orientationDegree < 90) {
//            compassOrientation = "North";
//        } else if (orientationDegree >= 90 && orientationDegree < 180) {
//            compassOrientation = "East";
//        } else if (orientationDegree >= 180 && orientationDegree < 270) {
//            compassOrientation = "South";
//        } else {
//            compassOrientation = "West";
//        }
//    }

    //Runnable that calculates the elapsed time since the user presses the "start" button
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            //timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            //updatedTime = elapsedTime + timeInMilliseconds;
            Notification notification = updateNoification();
            startForeground(notification_id,notification);
            //Log.d(TAG,timeString);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors(){

        if(stepDetectorSensor != null)
            sensorManager.registerListener(StepService3.this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        if(accelerometer != null)
            sensorManager.registerListener(StepService3.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

//        if(magnetometer != null)
//            sensorManager.registerListener(StepService3.this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

//        if(stepCounter != null)
//            sensorManager.registerListener(StepService3.this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);

    }

    private void unregisterSensors(){

//        if(stepDetectorSensor != null)
//            sensorManager.unregisterListener(StepService3.this, stepDetectorSensor);

        if(accelerometer != null)
            sensorManager.unregisterListener(StepService3.this, accelerometer);

//        if(magnetometer != null)
//            sensorManager.unregisterListener(StepService3.this, magnetometer);
//
//        if(stepCounter != null)
//            sensorManager.unregisterListener(StepService3.this , stepCounter);

    }

    public HashMap<String, String> getPrevData(){

        HashMap<String, String> data = new HashMap<>();
        String distanceString = String.format("%.2f",lastDistance);

        data.put("duration", elapsedString+"");
        data.put("steps", lastSteps+"");
        data.put("distance", distanceString+"");
        data.put("speed",(int)(distance/(elapsedTime/(1000*60)))+"");
        return data;
    }

    public HashMap<String, String> getData(){
        HashMap<String, String> data =  new HashMap<>();
        String distanceString = String.format("%.2f",lastDistance + distance);
//
//        int seconds = (int) (updatedTime / 1000);
//        int minutes = seconds / 60;
//        int hours = minutes / 60;
//        seconds = seconds % 60;
//        minutes = minutes % 60;
//        timeString = String.format("%d:%s:%s", hours, String.format("%02d", minutes), String.format("%02d", seconds));

        //data.put("steps", lastSteps + stepCount+"");
        data.put("steps", step+"");
//        data.put("distance", distanceString);
//        data.put("orientation", compassOrientation);
//        data.put("duration", timeString);
//        data.put("speed", speed+"");
        return data;
    }

    private Notification updateNoification(){

        String body = "";
        String title = "Step Counter ";
        HashMap<String, String> data = getData();

        body += data.get("distance") + "                ";
        body += data.get("duration");

        Notification notification = getNotification("STEPS TAKEN :  " + data.get("steps"),body);

        return notification;
    }

    private void persistSteps(){

        try {
            final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            Steps steps = new Steps(step, timestamp);
            step_ref.child(timestamp).setValue(steps);
        }catch (Exception e){

        }
    }

    private Notification getNotification(String title, String body){

        Intent resetIntent = new Intent(this,StepService3.class);
        resetIntent.setAction(Constants.RESET_COUNT);
        PendingIntent resetPendingIntent = PendingIntent.getService(this,0,resetIntent,0);

        Intent stopIntent = new Intent(this,StepService3.class);
        resetIntent.setAction(Constants.STOP_SAVE_COUNT);
        PendingIntent stopPendingIntent = PendingIntent.getService(this,0,resetIntent,0);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,9,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .build();

        return notification;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WALKWITHME",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void fetchData(){

        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d(TAG,"in fetch");
        try{
            step_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG,"inside fetche");
                    //step = Integer.valueOf(dataSnapshot.child("Test Accelerometer").getValue().toString());
                    if(dataSnapshot.child(timestamp).exists()){
                        Log.d(TAG,"fetched data");
                        Steps steps = dataSnapshot.child(timestamp).getValue(Steps.class);
                        String tmp= dataSnapshot.child("Step Count").getValue().toString();
                        step = (int)steps.getSteps();
//                        lastDistance = steps.getDistance();
//                        lastSteps = steps.getSteps();
//                        elapsedTime = steps.getDuration();
//                        updatedTime = elapsedTime;
                    }else{
                        persistSteps();
                    }

//                    int seconds = (int) (elapsedTime/1000);
//                    int minutes = seconds / 60;
//                    int hours = minutes / 60;
//                    seconds = seconds % 60;
//                    minutes = minutes % 60;
//
//                    elapsedString = String.format("%d:%s:%s", hours, String.format("%02d", minutes), String.format("%02d", seconds));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG,"fetch canceled");
                }
            });
        }catch (Exception e){
            Log.d(TAG,"fetch exception " + e.getLocalizedMessage());
        }
    }
}
