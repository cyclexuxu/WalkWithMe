package neu.madcourse.walkwithme.Pedometer;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;
import neu.madcourse.walkwithme.R;

public class StepsFragment2 extends Fragment implements NumberPicker.OnValueChangeListener{

    static int dailyGoal = 0;

    //Activity Views
    private TextView dayRecordText;
    private TextView stepText;
    private Button startButton;
    LineChartView lineChart;
    private Handler handler = new Handler();

    private int goal;

    private StepService3 service = null;
    private boolean isBound ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_step, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize views
        stepText = (TextView) view.findViewById(R.id.stepText);
        dayRecordText = view.findViewById(R.id.dayRecordText);
        startButton = view.findViewById(R.id.startButton);
        lineChart = (LineChartView) view.findViewById(R.id.line_chart);

        //when senor is not available
        if(!checkSensors())
            startButton.setEnabled(false);

        //when user click star button, the service start
        if (startButton != null) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isBound){
                        Snackbar.make(view,"We are trying to bind your service", Snackbar.LENGTH_LONG).show();
                    }
                    else if (!service.isActive()) {
                        startButton.setText("Stop");
                        service.startForegroundService();

                    } else {
                        startButton.setText("Start Now");
                        service.stopForegroundService(true);
                        //checkSensors();
                    }
                }
            });
        }

        //set daily goal
        final Button setGoal = view.findViewById(R.id.setgoal);

        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    //Connect to service
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder_service) {
            StepService3.MyBinder myBinder = (StepService3.MyBinder) binder_service;
            service = myBinder.getService();
            isBound = true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        dayRecordText.setText(goal+"");
        Intent intent = new Intent(getActivity(), StepService3.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        handler.postDelayed(timerRunnable, 0);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mServiceConnection);
        handler.removeCallbacks(timerRunnable);
    }

    private boolean checkSensors(){

        SensorManager sensorManager;
        Sensor accelerometer;
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(accelerometer == null ){
            Toast.makeText(getContext(), "Sorry, we cannot find accelerometer sensor in your device", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            Toast.makeText(getContext(), "Accelerometer sensor is available", Toast.LENGTH_SHORT).show();
            return true;
        }


    }

    //Runnable that get current steps and past 6 days history
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> data ;
            int[] days;
            if(isBound){
                if(isAdded()) {
                    data = service.getData();
                    stepText.setText(data.get("steps")+"");
                    days = service.getDays();
                    initLineChart(days);
                    if(service.isActive()){
                        startButton.setText("Stop");
                    }else {
                        startButton.setText("Start Now");
                    }

                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    //set goal dialog, use number pick
    public void showDialog()
    {
        final Dialog d = new Dialog(getActivity());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog_set_goal);
        Button cancel = (Button) d.findViewById(R.id.exist);
        Button set = (Button) d.findViewById(R.id.set);
        final NumberPicker numberPicker = (NumberPicker) d.findViewById(R.id.set_goal);

        final String[] displayedValues = new String[19];

        //Starting from 0 to 19000
        for (int i = 0; i < 19; i++)
            displayedValues [i] = String.valueOf((i) * 1000);

        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(20);
        numberPicker.setDisplayedValues(displayedValues);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //reset goal
                goal = Integer.parseInt(displayedValues[numberPicker.getValue() - 2]);
                dayRecordText.setText(goal+"");
                dailyGoal = goal;
                Log.d("Set Goal", "set goal to  "+ dailyGoal);
                d.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    //Use hellochart library to draw linear char
    private void initLineChart(int[] days) {
        List<PointValue> mPointValues = new ArrayList<>();
        List<AxisValue> mAxisXValues = new ArrayList<>();

        for (int i = 0; i < days.length; i++) {
            //Log.d("days: ", " y " + days[i] +" x " + i+"");
            mPointValues.add(new PointValue(i, days[i]));
        }

        String[] xValues = GetHistory.get7days();

        for (int i = 0; i < xValues.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(xValues[i]));
        }


        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFFAFA"));
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE); //shape of points
        line.setFilled(true);//shadow area under lines
        line.setHasLabels(true);

        line.setHasLines(true);//show lines between points
        line.setHasPoints(true);//show dot

        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis(); //a axis
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);  //color
        axisX.setTextSize(10);//font size
        axisX.setMaxLabelChars(7); //max label 7, 7 days
        axisX.setValues(mAxisXValues);
        data.setAxisXBottom(axisX);
        axisX.setHasLines(true);


        Axis axisY = new Axis();
        axisY.setName("");//y name
        axisY.setTextColor(Color.WHITE);  //color
        data.setAxisYLeft(axisY);

        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 3);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            try {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}