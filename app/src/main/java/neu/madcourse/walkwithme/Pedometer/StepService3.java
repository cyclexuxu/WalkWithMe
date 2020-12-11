package neu.madcourse.walkwithme.Pedometer;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import neu.madcourse.walkwithme.userlog.LoginActivity;


public class StepService3 extends Service implements SensorEventListener {

    //Sensor related variables
    private SensorManager sensorManager;
    private Sensor accelerometer;

    int[] historyData = new int[6];
    private String timestamp = "";

    //Variables used in calculations
    private double preMagnitude = 0;
    private int step = 0;
    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;
    private int totalStep = 0;
    private boolean sendMessage = false; //meet goal message

    private boolean isActive = false;

    private Handler handler = new Handler();
    String CHANNEL_ID = "WalkWithMe";
    int notification_id = 1;
    String TAG = "Step Service";
    public static String currentUser = LoginActivity.currentUser;

    private static int SIX_DAY = 6; //DAY FACTORS TO GET STEP HISTORY
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

        setAlarm(); //this alarm check daily goal and step at 5pm everyday
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        mdb = FirebaseDatabase.getInstance();

        try{
            step_ref = mdb.getReference().child("users").child(currentUser);
        }catch (Exception e){
        }
        accessData(); //fetch all data from firebase
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch(action){

            case Constants.START_FOREGROUND :
                Log.d(TAG,"starting service");
                break;

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
        Log.d("service", "service stop()");
        stopForegroundService(true);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Convert accelerometer data to step count
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


                double lastMagnitude = preMagnitude;
                double magnitude = Math.sqrt(x * x + y * y + z * z);
                double delta = magnitude - preMagnitude;
                preMagnitude = magnitude;

                if (delta > 3 && lastMagnitude != 0) {
                    step++;
                    totalStep++;
                }

            final String now = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            if (now.equals(timestamp)) {

                step_ref.child("Total Steps").setValue(totalStep);
                step_ref.child("Step Count").child(timestamp).child("steps").setValue(step);
            } else {
                resetSteps();
            }

            historyData[SIX_DAY] = step; //update today step in line chart

        }
    }

    public boolean isActive(){
        return  isActive;
    }

    public void startForegroundService(){
        registerSensors();
        NotificationCenter notificationCenter = new NotificationCenter(getApplicationContext());
        startForeground(notification_id,notificationCenter.getNotification(NofiticationConstants.SERVICE_START));
        handler.postDelayed(timerRunnable,1000);
        isActive = true;
    }

    public void stopForegroundService(boolean update){
        unregisterSensors();
        handler.removeCallbacks(timerRunnable);
        isActive = false;
        NotificationCenter notificationCenter = new NotificationCenter(getApplicationContext());
        startForeground(notification_id,notificationCenter.getNotification(NofiticationConstants.SERVICE_STOP));
        stopForeground(true);
        if(update)
           updateSteps(); //save data to firebase
    }

    //Runnable that calculates the elapsed time since the user presses the "start" button
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            NotificationCenter notificationCenter = new NotificationCenter(getApplicationContext());
            //when goal is 0 , user hasn't set the goal yet, dont need to send notification
            //Meet daily goal messgae
            if(step >= StepsFragment2.dailyGoal && !sendMessage && StepsFragment2.dailyGoal != 0) {
                notificationCenter.createNotification(NofiticationConstants.meetGoal);
                sendMessage = true;
            }

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors(){

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
        return historyData;
    }

    private void updateSteps(){
        try {
            timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            Steps steps = new Steps(step, timestamp);
            step_ref.child("Step Count").child(timestamp).setValue(steps);
            step_ref.child("Total Steps").setValue(totalStep);
        }catch (Exception e){

        }
    }
    
    private void resetSteps(){
        //Daily reset step to 0, when pass 23:59
        Log.d(TAG, "resetSteps: ");
        timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        step = 0;
        sendMessage = false;
        Steps steps = new Steps(step, timestamp);
        step_ref.child("Step Count").child(timestamp).setValue(steps);
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

        timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d(TAG,"access firebase data");
        try{
            step_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Log.d(TAG, currentUser);
                    //currentLevel = dataSnapshot.child("level").getValue(Integer.class);
                    if(dataSnapshot.child("Total Steps").exists()){
                        totalStep = Integer.parseInt(dataSnapshot.child("Total Steps").getValue().toString()); //get previous total steps
                    }
                    if(dataSnapshot.child("Step Count").child(timestamp).exists()){
                        Steps steps = dataSnapshot.child("Step Count").child(timestamp).getValue(Steps.class);
                        step = (int)steps.getSteps(); //get previous steps
                        //prevStep = (int)steps.getSteps();
                    }else{
                        sendMessage = true; //reset sendmessage when new timestamp created
                        updateSteps();
                    }

                    //Fetch previous days steps data
                    int[] tmp = {0, 0, 0, 0, 0, 0, 0};
                    for (int i = 0; i <= SIX_DAY; i++) {

                        int step = 0;
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -i);
                        Date todate1 = cal.getTime();
                        String tmptimestamp = new SimpleDateFormat("yyyy-MM-dd").format(todate1);
                        if (dataSnapshot.child("Step Count").child(tmptimestamp).exists()) {
                            Steps steps = dataSnapshot.child("Step Count").child(tmptimestamp).getValue(Steps.class);
                            step = (int) steps.getSteps();
                            tmp[SIX_DAY - i] = step;
                        }
                    }
                    historyData = tmp;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
            Log.d(TAG,"exception " + e.getLocalizedMessage());
        }
    }
    //set alarm to check today's goal and steps at 6pm every day notification
    private void setAlarm(){
        Log.d("setAlarm: ", "set alarm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), MeetGoalReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }else{
            Log.d("setAlarm: ", "set alarm is null");
        }
    }

}