package neu.madcourse.walkwithme.Test;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
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

    static int dailyGoal = 2000;

    //Activity Views
    private TextView dayRecordText;
    private TextView stepText;
    private TextView notices ;
    private Button startButton;
    private String numSteps;
    int[] days = new int[6];
    LineChartView lineChart;
    private Handler handler = new Handler();

    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    private int dayStepRecord;

    private FirebaseDatabase mdb;
    private DatabaseReference step_ref;

    private StepService3 service = null;
    private final String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    private boolean isBound ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getActivity().getSharedPreferences("WalkWithMe", Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putInt("dailyGoal", 2000);
        editor.commit();
        mdb = FirebaseDatabase.getInstance();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize views
        stepText = (TextView) view.findViewById(R.id.stepText);
        dayRecordText = view.findViewById(R.id.dayRecordText);
        notices = (TextView)view.findViewById(R.id.accuracy_alert);
        startButton = view.findViewById(R.id.startButton);
        lineChart = (LineChartView) view.findViewById(R.id.line_chart);

//        try{
//            String address = user.getString("address","");
//            step_ref = mdb.getReference().child("users").child("Dan");
//            Steps preStep = new Steps(150, "2020-11-26");
//            step_ref.child("Step Count").child("2020-11-26").setValue(preStep);
//
//        }catch (Exception e){
//
//        }

        if(!checkSensors())
            startButton.setEnabled(false);


        //Step counting and other calculations start when user presses "start" button
        if (startButton != null) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!isBound){
                        Snackbar.make(view,"Binding to the Step Counting Service , wait ... ", Snackbar.LENGTH_LONG).show();
                    }
                    else if (!service.isActive()) {
                        startButton.setText("Stop");
                        notices.setText(" The sensor has a Latency of 10 seconds . ");
                        service.startForegroundService();
//                        startButton.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.color3));
//                        startButton.setTextColor(ContextCompat.getColor(getActivity(),R.color.color1));

                    } else {
                        startButton.setText("Start!");
                        service.stopForegroundService(true);
//                        startButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color2));
//                        startButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.color1));
                        checkSensors();
                    }
                }
            });
        }
        //();


        final Button setGoal = view.findViewById(R.id.setgoal);

        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        Button resetButton = (Button) view.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound && service.isActive())
                    service.resetCount();
            }
        });

        //drawChart();

    }

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
        Log.e("HIHIHIHIHIHIHI", "1");
        //dayStepRecord = Integer.parseInt(user.getString("DAY_STEP_RECORD", "2000"));
        Log.e("HIHIHIHIHIHIHI", "2");
        dayRecordText.setText(dayStepRecord+"");
        Log.e("HIHIHIHIHIHIHI", "3");

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

    private boolean  checkSensors(){

        SensorManager sensorManager;
        Sensor stepDetectorSensor;
        Sensor accelerometer;
        Sensor magnetometer;
        Sensor stepCounter;

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if( stepCounter != null ){
            notices.setText(" Step Counter Sensor available . ");

            if( magnetometer == null || accelerometer == null ){
                notices.setText( notices.getText().toString() + "\n Magnetometer or Accelerometer not available cannot calculate Direction . ");
            }else{
                notices.setText( notices.getText().toString() + "\n Rest All necessary sensors available .");
            }

            return true;

        }else if( stepDetectorSensor != null){
            notices.setText("Step Detector Sensor available . ");
            if( magnetometer == null || accelerometer == null ){
                notices.setText( notices.getText().toString() + "\n Magnetometer or Accelerometer not available cannot calculate Direction . ");
            }else{
                notices.setText( notices.getText().toString() + "\n Rest All necessary sensors available .");
            }

            return true;

        }else{
            notices.setText(" Step Counter and Step Detector Sensor not available \n cannot calculate Steps , Sorry . ");
            return false;
        }
    }

    //Runnable that calculates the elapsed time since the user presses the "start" button
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> data ;
            int[] days;

            if(isBound){

                if(isAdded()) {
                    data = service.getData();

                    stepText.setText(data.get("steps")+"");
                    //Chart(data.get("steps")+"");
                    days = service.getDays();
                    initLineChart(days);

                    if(service.isActive()){
                        startButton.setText("Stop");
//                        startButton.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.color3));
//                        startButton.setTextColor(ContextCompat.getColor(getActivity(),R.color.color1));
                    }else {
                        startButton.setText("Start!");
//                        startButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color2));
//                        startButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.color1));
                    }

                }

            }
            handler.postDelayed(this, 1000);
        }
    };

    public void showDialog()
    {
        final Dialog d = new Dialog(getActivity());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog_set_goal);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);

        //Display the number picker values in thousands
        final String[] displayedValues = new String[19];

        //Starting from 2000
        for (int i = 0; i < 19; i++)
            displayedValues [i] = String.valueOf((i + 2) * 1000);

        np.setMinValue(2);
        np.setMaxValue(20);
        np.setDisplayedValues(displayedValues);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dayStepRecord  = Integer.parseInt(displayedValues[np.getValue() - 2]);
                dayRecordText.setText(dayStepRecord+"");
                dailyGoal = dayStepRecord;
                Log.d("Set Goal", "set goal to  "+ dailyGoal);
//                user.edit().putString("DAY_STEP_RECORD",displayedValues[np.getValue() - 2]).apply();
//                user.edit().putBoolean(todayDate + "_step",true).apply();
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
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

    //Use hellochart library to draw chart
    private void initLineChart(int[] days) {
        List<PointValue> mPointValues = new ArrayList<>();
        List<AxisValue> mAxisXValues = new ArrayList<>();

        for (int i = 0; i < days.length; i++) {
            //Log.d("days: ", " y " + days[i] +" x " + i+"");
            mPointValues.add(new PointValue(i, days[i]));
        }

        String[] xValues = DateTimeHelper.get6days(true);

        for (int i = 0; i < xValues.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(xValues[i]));
        }


        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFFAFA"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注

//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();

        axisY.setName("");//y轴标注
        // axisY.setTextSize(10);//设置字体大小
        axisY.setTextColor(Color.parseColor("#ffffff"));
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);

    }



}