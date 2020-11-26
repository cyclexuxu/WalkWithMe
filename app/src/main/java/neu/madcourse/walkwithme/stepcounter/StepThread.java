package neu.madcourse.walkwithme.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import neu.madcourse.walkwithme.Test.Steps;

public class StepThread extends Thread implements SensorEventListener, StepListener{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final String TAG = "StepService: ";
    private double preMagnitude = 0;
    private long numSteps = 0;
    private long lastSteps = 0;
    private Context context;
    boolean isRegiter = false;
    boolean isActivity = false;
    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;

    public StepThread(Context context) {
        this.context = context;
        initStepDetector();
    }

    @Override
    public void run() {
        Log.e(TAG, "step thread is running");
        step_ref.child("step").setValue(100);
        if (!isRegiter) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            isRegiter = true;
        }
    }

    public void mystop()
    {
        if (isRegiter) {
            sensorManager.unregisterListener(this);
            isRegiter = false;
        }


    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean activity) {
        isActivity = activity;
    }

    public void initStepDetector() {

        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        Realm realm = Realm.getDefaultInstance();
//        StepModel result = realm.where(StepModel.class)
//                .equalTo("date", today)
//                .findFirst();
//
//        lastStpes = result == null ? 0 : result.getNumSteps();
        //user = getSharedPreferences("user",Context.MODE_PRIVATE);
        mdb = FirebaseDatabase.getInstance();

        try{
            //String address = user.getString("address","");
            step_ref = mdb.getReference().child("users").child("Dan");
        }catch (Exception e){
        }
        Log.d(TAG,"increate");
        fetchData();
        step(lastSteps);
        //realm.close();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x*x + y*y + z*z);
            double delta = magnitude - preMagnitude;
            preMagnitude = magnitude;

            if(delta > 6){
                numSteps++;
            }
        }
        Log.e(TAG, numSteps+"" );
        step(numSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long num) {
//        if (!today.equals(DateTimeHelper.getToday()))
//        {
//            save(today,numStpes);
//            numStpes=0;
//            lastStpes=0;
//            today=DateTimeHelper.getToday();
//        }
        numSteps += num;
        Log.d("step", "step(num) " + num);
        EventBus.getDefault().post(num);
        step_ref.child("Dan").setValue(numSteps);
//        if (numStpes - lastStpes > 10) {
//            lastStpes = numStpes;
//            save(today,numStpes);
//        }

    }
//
//    public void save(Date date, long num)
//    {
//        Realm realm = Realm.getDefaultInstance();
//        realmAsyncTask = realm.executeTransactionAsync(
//                new StepTransaction(date, num),
//                new SuccessTransaction(realmAsyncTask),
//                new Realm.Transaction.OnError() {
//                    @Override
//                    public void onError(Throwable error) {
//                        error.printStackTrace();
//                        Log.d("realm", "insert error");
//                    }
//                }
//        );
//        realm.close();
//    }

    private void fetchData(){

        final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d(TAG,"in fetch");
        try{
            step_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG,"inside fetche");
                    if(dataSnapshot.child(timestamp).exists()){
                        Log.d(TAG,"fetched data");
                        Steps steps = dataSnapshot.child("Step").getValue(Steps.class);
                        //lastDistance = steps.getDistance();
                        lastSteps = steps.getSteps();
                        //elapsedTime = steps.getDuration();
                        //updatedTime = elapsedTime;
                    }

//                    //int seconds = (int) (elapsedTime/1000);
//                    int minutes = seconds / 60;
//                    int hours = minutes / 60;
//                    seconds = seconds % 60;
//                    minutes = minutes % 60;
//
//                    e//lapsedString = String.format("%d:%s:%s", hours, String.format("%02d", minutes), String.format("%02d", seconds));
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
