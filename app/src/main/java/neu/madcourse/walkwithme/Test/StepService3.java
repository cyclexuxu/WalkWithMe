package neu.madcourse.walkwithme.Test;

import android.app.AlarmManager;
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
import android.os.Looper;
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
import neu.madcourse.walkwithme.R;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class StepService3 extends Service implements SensorEventListener {

    //Sensor related variables
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Sensor stepCounter;

    int[] data = new int[6];


    //Variables used in calculations
    private double preMagnitude = 0;
    private int step = 0;
    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;
    private int totalStep = 0;
    private int prevStep = 0;
    private boolean sendMessage = false;


    private boolean isActive = false;

    private SharedPreferences user;
    private Handler handler = new Handler();
    String CHANNEL_ID = "WalkWithMe";
    int notification_id = 1;
    String TAG = "service_error";
    public static String currentUser = LoginActivity.currentUser;

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
        setAlarm();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mdb = FirebaseDatabase.getInstance();
        Log.d(TAG, currentUser);

        try{
            step_ref = mdb.getReference().child("users").child(currentUser);
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
        stopForegroundService(true);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Get sensor values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x * x + y * y + z * z);
            double delta = magnitude - preMagnitude;
            preMagnitude = magnitude;

            if (delta > 6) {
                step++;
                totalStep++;
            }


            final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
//            step_ref.child("Total Steps").setValue(totalStep);
//            step_ref.child("Step Count").child(timestamp).child("steps").setValue(step);

            step_ref.child("Total Steps").setValue(totalStep);
            step_ref.child("Step Count").child(timestamp).child("steps").setValue(step);


//            step_ref.child("Step Count").child(timestamp).child("steps").setValue(step);
//            step_ref.child("Total Steps").setValue(totalStep);
            data[5] = step;
        }
    }

    public boolean isActive(){
        return  isActive;
    }

    public void startForegroundService(){
        registerSensors();
        //startTime = SystemClock.uptimeMillis() + 1000;
        startForeground(notification_id,getNotification("Starting Step Counter Service","", 1));
        handler.postDelayed(timerRunnable,1000);
        isActive = true;
    }

    public void stopForegroundService(boolean update){
        unregisterSensors();
        handler.removeCallbacks(timerRunnable);
        isActive = false;
        startForeground(notification_id,getNotification("Stopping  Step Counter Service","", 1));
        stopForeground(true);
        //elapsedTime = elapsedTime + timeInMilliseconds;
        if(update)
           updateSteps();
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
            if(step >= 100 && !sendMessage) {
                Notification notification = updateNoification();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(2, notification);
                //startForeground(2, notification);
                sendMessage = true;
            }
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
            sensorManager.registerListener(StepService3.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

        Notification notification = getNotification(NofiticationCenter.REACH_DAILY_GOAL,body, 2);

        return notification;
    }

    private void updateSteps(){

        try {
            final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            Steps steps = new Steps(step, timestamp);
            step_ref.child("Step Count").child(timestamp).setValue(steps);
            step_ref.child("Total Steps").setValue(totalStep);
        }catch (Exception e){

        }
    }

    private Notification getNotification(String title, String body, int id){

        Intent resetIntent = new Intent(this,StepService3.class);
        resetIntent.setAction(Constants.RESET_COUNT);
        PendingIntent resetPendingIntent = PendingIntent.getService(this,0,resetIntent,0);

        Intent stopIntent = new Intent(this,StepService3.class);
        resetIntent.setAction(Constants.STOP_SAVE_COUNT);
        PendingIntent stopPendingIntent = PendingIntent.getService(this,0,resetIntent,0);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,id,intent,PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.happy),97,128,false))
                .setSmallIcon(R.drawable.happy)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build();

        return notification;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WALKWITHME",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void accessData(){

        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d(TAG,"access firebase data");
        try{
            step_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, currentUser);
                    if(dataSnapshot.child("Total Steps").exists()){
                        totalStep = Integer.parseInt(dataSnapshot.child("Total Steps").getValue().toString());
                        //prevStep = Integer.parseInt(dataSnapshot.child("Total Steps").getValue().toString());
                    }
                    if(dataSnapshot.child("Step Count").child(timestamp).exists()){
                        Steps steps = dataSnapshot.child("Step Count").child(timestamp).getValue(Steps.class);
                        step = (int)steps.getSteps();
                        prevStep = (int)steps.getSteps();
                    }else{
                        updateSteps();
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
    //set alarm to check today's goal and steps at 5pm every day notification
    private void setAlarm(){
        Log.d("setAlarm: ", "set alarm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 11);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotiReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }else{
            Log.d("setAlarm: ", "set alarm is null");
        }
    }

}