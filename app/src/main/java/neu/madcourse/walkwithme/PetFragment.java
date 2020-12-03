package neu.madcourse.walkwithme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.madcourse.walkwithme.NotiPet.InsufficientMeatException;
import neu.madcourse.walkwithme.NotiPet.PetActivity;
import neu.madcourse.walkwithme.NotiPet.PetSleepState;
import neu.madcourse.walkwithme.NotiPet.PetStarveState;
import neu.madcourse.walkwithme.NotiPet.PetStarvingException;
import neu.madcourse.walkwithme.NotiPet.PetState;
import neu.madcourse.walkwithme.userlog.LoginActivity;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class PetFragment extends Fragment {

    ImageView corgi;
    PetState petState;

    Button feedButton;
    Button musicButton;
    Button tipsButton;

    ProgressBar healthBar;
    ProgressBar happinessBar;
    ProgressBar knowledgeBar;

    ImageView[] meatViews = new ImageView[7];

    AlarmManager alarmManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference knowledgeNumReference;
    DatabaseReference meatNumReference;
    DatabaseReference happinessNumReference;
    DatabaseReference healthNumReference;

    static final String TAG = "Pet page";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        corgi = (ImageView) view.findViewById(R.id.corgiImage);

        healthBar = (ProgressBar) view.findViewById(R.id.healthProgress);
        happinessBar = (ProgressBar) view.findViewById(R.id.happinessProgress);
        knowledgeBar = (ProgressBar) view.findViewById(R.id.knowledgeProgress);
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

        meatViews[0] = view.findViewById(R.id.meat1);
        meatViews[1] = view.findViewById(R.id.meat2);
        meatViews[2] = view.findViewById(R.id.meat3);
        meatViews[3] = view.findViewById(R.id.meat4);
        meatViews[4] = view.findViewById(R.id.meat5);
        meatViews[5] = view.findViewById(R.id.meat6);
        meatViews[6] = view.findViewById(R.id.meat7);

        firebaseDatabase = FirebaseDatabase.getInstance();

        petState = new PetSleepState();

        meatNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("meatNum");
        healthNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("healthNum");
        happinessNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("happinessNum");
        knowledgeNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("knowledgeNum");

        meatNumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petState.setMeat(snapshot.getValue(Integer.class));
                showCorgi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Waiting!");
                Log.e("Database", error.toException().toString());
            }
        });

        knowledgeNumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petState.setcKnowledge(snapshot.getValue(Integer.class));
                Log.e("Data of Knowledge", String.valueOf(petState.getcKnowledge()));
                if (petState.getcHealth() <= 0){
                    petState = new PetStarveState();
                } else {
                    petState = new PetSleepState();
                }
                showCorgi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Waiting!");
                Log.e("Database", error.toException().toString());
            }
        });

        healthNumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petState.setcHealth(snapshot.getValue(Integer.class));
                if (petState.getcHealth() <= 0) {
                    petState = new PetStarveState();
                } else {
                    petState = new PetSleepState();
                }
                showCorgi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Waiting!");
                Log.e("Database", error.toException().toString());
            }
        });

        happinessNumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petState.setcHappiness(snapshot.getValue(Integer.class));
                if (petState.getcHealth() <= 0){
                    petState = new PetStarveState();
                } else {
                    petState = new PetSleepState();
                }
                showCorgi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Waiting!");
                Log.e("Database", error.toException().toString());
            }
        });

//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 3000, 100000,
//               PendingIntent.getService(getApplicationContext(), 0, new Intent(this, NotificationService.class), 0) );

        showCorgi();

        corgi.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                corgi.setImageResource(R.drawable.run);
//                beginTranslationAnimation();
                return true;
            }
        });

        feedButton = (Button) view.findViewById(R.id.feedButton);
        feedButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {
                    petState = petState.feed();
                    meatNumReference.setValue(petState.getMeat());
                    healthNumReference.setValue(petState.getcHealth());
                } catch (InsufficientMeatException e) {
                    showToast("Not enough meat");
                    return;
                }
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "eat", new AlarmManager.OnAlarmListener() {
                    @Override
                    public void onAlarm() {
                        petState = petState.timeout();
                        showCorgi();
                    }
                }, null);
                showCorgi();
            }
        });
        musicButton = (Button) view.findViewById(R.id.musicButton);
        musicButton.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {
                    petState = petState.music();
                } catch (PetStarvingException e) {
                    showToast("I'm starving, feed me first.");
                    return;
                }
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "music", new AlarmManager.OnAlarmListener() {
                    @Override
                    public void onAlarm() {
                        petState = petState.timeout();
                        showCorgi();
                    }
                }, null);
                showCorgi();
            }
        });

        tipsButton = (Button) view.findViewById(R.id.tipsButton);
        tipsButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {
                    petState = petState.tip();
                } catch (PetStarvingException e) {
                    showToast("I'm starving, feed me first.");
                    return;
                }
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "tips", new AlarmManager.OnAlarmListener() {
                    @Override
                    public void onAlarm() {
                        petState = petState.timeout();
                        showCorgi();
                    }
                }, null);
                showCorgi();
            }
        });

    }

    private void showCorgi(){
        corgi.setImageResource(petState.getImage());
        healthBar.setProgress(petState.getcHealth());
        happinessBar.setProgress(petState.getcHappiness());
        knowledgeBar.setProgress(petState.getcKnowledge());

        for (int i = 0; i < 7; i++) {
            if (i < petState.getMeat()) {
                meatViews[i].setVisibility(View.VISIBLE);
            } else {
                meatViews[i].setVisibility(View.INVISIBLE);
            }
        }

    }

    public class JumpInterpolator implements TimeInterpolator {
        @Override
        public float getInterpolation(float v) {
            return (float) - Math.abs(Math.sin(v * 3 * Math.PI)) + 1;
        }
    }

//    private void beginTranslationAnimation(){
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        ObjectAnimator animator = ObjectAnimator.ofFloat(corgi, "translationY", -height / 6, 0);
//        animator.setInterpolator(new PetFragment().JumpInterpolator());
//        animator.setDuration(3000);
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                showCorgi();
//            }
//        });
//        animator.start();
//    }

    private void showToast(String message){
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getContext(), message, duration);
        toast.show();
    }

}

