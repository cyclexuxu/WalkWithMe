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
import java.util.Date;
import java.util.HashMap;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class StepService3 extends Service implements SensorEventListener {

    //Sensor related variables
    private SensorManager sensorManager;
    private Sensor accelerometer;

    int[] data = new int[6];


    //Variables used in calculations
    private double preMagnitude = 0;
    private int step = 0;

    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;

    private boolean isActive = false;

    private SharedPreferences user;
    private Handler handler = new Handler();
    String CHANNEL_ID = "WalkWithMe";
    int notification_id = 1711101;
    String TAG = "Step Service: ";

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
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mdb = FirebaseDatabase.getInstance();

        try{
            step_ref = mdb.getReference().child("users").child(LoginActivity.currentUser);
        }catch (Exception e){
        }
        accessData();
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
            //accelValues = event.values;
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
            step_ref.child("Step Count").child(timestamp).child("steps").setValue(step);
            data[5] = step;
        }
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

    //Runnable that calculates the elapsed time since the user presses the "start" button
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Notification notification = updateNoification();
            startForeground(notification_id,notification);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors(){

//        if(stepDetectorSensor != null)
//            sensorManager.registerListener(StepService3.this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        if(accelerometer != null)
            sensorManager.registerListener(StepService3.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

    }

    private void unregisterSensors(){


        if(accelerometer != null)
            sensorManager.unregisterListener(StepService3.this, accelerometer);

    }

    public HashMap<String, String> getData(){
        HashMap<String, String> data =  new HashMap<>();
        data.put("steps", step+"");
        return data;
    }

    public int[] getDays(){
        return data;
    }

    private Notification updateNoification(){

        String body = "";
        HashMap<String, String> data = getData();

        body += data.get("distance") + "                ";
        body += data.get("duration");

        Notification notification = getNotification("STEPS TAKEN :  " + data.get("steps"),body);

        return notification;
    }

    private void persistSteps(){

        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Steps steps = new Steps(step, timestamp);
        step_ref.child("Step Count").child(timestamp).setValue(steps);
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

    private void accessData(){

        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d(TAG,"in fetch");
        try{
            step_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG,"inside fetche");
                    if(dataSnapshot.child("Step Count").child(timestamp).exists()){
                        Log.d(TAG,"fetched data");
                        Steps steps = dataSnapshot.child("Step Count").child(timestamp).getValue(Steps.class);
                        step = (int)steps.getSteps();
                    }else{
                        persistSteps();
                    }

                    int[] tmp = {0, 0, 0, 0, 0, 0};
                    for (int i = 0; i <= 5; i++) {

                        int step = 0;
                        Log.d("FIREBASE ", "FOR LOOP");
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -i);
                        Date todate1 = cal.getTime();
                        String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(todate1);
                        if (dataSnapshot.child("Step Count").child(timestamp).exists()) {
                            Steps steps = dataSnapshot.child("Step Count").child(timestamp).getValue(Steps.class);
                            step = (int) steps.getSteps();
                            tmp[5 - i] = step;
                        }
                    }
                    data = tmp;

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