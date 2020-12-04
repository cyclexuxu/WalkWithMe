package neu.madcourse.walkwithme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
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
import android.widget.TextView;
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
import com.google.rpc.context.AttributeContext;

import java.io.IOException;
import java.util.Random;

import io.grpc.internal.SharedResourceHolder;
import neu.madcourse.walkwithme.NotiPet.CustomToast;
import neu.madcourse.walkwithme.NotiPet.InsufficientMeatException;
import neu.madcourse.walkwithme.NotiPet.PetActivity;
import neu.madcourse.walkwithme.NotiPet.PetHappyState;
import neu.madcourse.walkwithme.NotiPet.PetSleepState;
import neu.madcourse.walkwithme.NotiPet.PetStarveState;
import neu.madcourse.walkwithme.NotiPet.PetStarvingException;
import neu.madcourse.walkwithme.NotiPet.PetState;
import neu.madcourse.walkwithme.userlog.LoginActivity;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class PetFragment extends Fragment {

    AlarmManager.OnAlarmListener alarmListener = new AlarmManager.OnAlarmListener() {
        @Override
        public void onAlarm() {
            petState = petState.timeout();
            showCorgi();
        }
    };

    ImageView corgi;
    PetState petState;

    Button feedButton;
    Button musicButton;
    Button tipsButton;

    ProgressBar healthBar;
    ProgressBar happinessBar;
    ProgressBar knowledgeBar;

    TextView levelText;

    ImageView[] meatViews = new ImageView[7];

    AlarmManager alarmManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference knowledgeNumReference;
    DatabaseReference meatNumReference;
    DatabaseReference happinessNumReference;
    DatabaseReference healthNumReference;
    DatabaseReference redeemedSteps;
    DatabaseReference totalSteps;
    DatabaseReference petLevel;

    static MediaPlayer mediaPlayer;
    static Context context;
    static final String TAG = "Pet page";

    static final int[] songBase = {R.raw.happy, R.raw.i_dont_care, R.raw.mamacita};
    static int songCount = 0;
    CustomToast customToast;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        corgi = (ImageView) view.findViewById(R.id.corgiImage);

        healthBar = (ProgressBar) view.findViewById(R.id.healthProgress);
        happinessBar = (ProgressBar) view.findViewById(R.id.happinessProgress);
        knowledgeBar = (ProgressBar) view.findViewById(R.id.knowledgeProgress);
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        levelText = view.findViewById(R.id.levelText);

        meatViews[0] = view.findViewById(R.id.meat1);
        meatViews[1] = view.findViewById(R.id.meat2);
        meatViews[2] = view.findViewById(R.id.meat3);
        meatViews[3] = view.findViewById(R.id.meat4);
        meatViews[4] = view.findViewById(R.id.meat5);
        meatViews[5] = view.findViewById(R.id.meat6);
        meatViews[6] = view.findViewById(R.id.meat7);

        firebaseDatabase = FirebaseDatabase.getInstance();

        petState = mediaPlayer != null && mediaPlayer.isPlaying() ?
                new PetHappyState():
                new PetSleepState();

        meatNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("meatNum");
        healthNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("healthNum");
        happinessNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("happinessNum");
        knowledgeNumReference = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("knowledgeNum");
        redeemedSteps = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("redeemedSteps");
        petLevel = firebaseDatabase.getReference("users").child(LoginActivity.currentUser).child("petLevel");

        redeemSteps();
        updateLevel();

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

        corgi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                corgi.setImageResource(R.drawable.run);
                beginTranslationAnimation();
            }
        });

        feedButton = (Button) view.findViewById(R.id.feedButton);
        feedButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {
                    petState = petState.feed();
                    updateDb();
                } catch (InsufficientMeatException e) {
                    showToast("Not enough meat");
                    return;
                }
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "eat", alarmListener, null);
                showCorgi();
            }
        });

        musicButton = (Button) view.findViewById(R.id.musicButton);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            musicButton.setText("Stop");
        } else {
            musicButton.setText("Music");
        }
        musicButton.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    musicButton.setText("Music");
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    petState = petState.timeout();
                    showCorgi();
                } else {
                    musicButton.setText("Stop");
                    try {
                        petState = petState.music();
                        alarmManager.cancel(alarmListener);
                        updateDb();
                        int songID = songCount % 3;
                        mediaPlayer = MediaPlayer.create(getContext(), songBase[songID]);
                        songCount++;
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                musicButton.setText("Music");
                                mediaPlayer.release();
                                mediaPlayer = null;
                                petState = petState.timeout();
                                showCorgi();
                            }
                        });
                        mediaPlayer.start();

                    } catch (PetStarvingException e) {
                        showToast("I'm starving, feed me first.");
                        return;
                    }
                    showCorgi();
                }
            }
        });

        tipsButton = (Button) view.findViewById(R.id.tipsButton);
        tipsButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {
                    Random random = new Random();
                    int tipID = random.nextInt(10);
                    Resources res = getResources();
                    String[] tip_lab = res.getStringArray(R.array.tips_lab);
                    showToast(tip_lab[tipID]);
                    petState = petState.tip();
                    updateDb();
                } catch (PetStarvingException e) {
                    showToast("I'm starving, feed me first.");
                    return;
                }
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, 3000, "tips", alarmListener, null);
                showCorgi();
            }
        });

    }

    private void showCorgi(){
        corgi.setImageResource(petState.getImage());
        healthBar.setProgress(petState.getcHealth());
        happinessBar.setProgress(petState.getcHappiness());
        knowledgeBar.setProgress(petState.getcKnowledge());

        levelText.setText(context.getString(R.string.petLevelString, petState.getPetLevel()));


        for (int i = 0; i < 7; i++) {
            if (i < petState.getMeat()) {
                meatViews[i].setVisibility(View.VISIBLE);
            } else {
                meatViews[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateDb() {
        healthNumReference.setValue(petState.getcHealth());
        happinessNumReference.setValue(petState.getcHappiness());
        knowledgeNumReference.setValue(petState.getcKnowledge());
        petLevel.setValue(petState.getPetLevel());
        meatNumReference.setValue(petState.getMeat());
    }

    public class JumpInterpolator implements TimeInterpolator {
        @Override
        public float getInterpolation(float v) {
            return (float) - Math.abs(Math.sin(v * 3 * Math.PI)) + 1;
        }
    }

    private void beginTranslationAnimation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        ObjectAnimator animator = ObjectAnimator.ofFloat(corgi, "translationY", -height / 6, 0);
        animator.setInterpolator(new JumpInterpolator());
        animator.setDuration(3000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showCorgi();
            }
        });
        animator.start();
    }

    private void showToast(String message){
        int duration = CustomToast.LENGTH_LONG;
        if (customToast == null){
            customToast= CustomToast.makeText(getContext(), message, duration, 120,230);
        } else {
            customToast.setText(message);
        }

        customToast.show();
    }

    private void redeemSteps(){
        DatabaseReference diffSteps = FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.currentUser);
        diffSteps.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalStep = snapshot.child("Total Steps").getValue(Integer.class);
                int redeemedStep = snapshot.child("redeemedSteps").getValue(Integer.class);
                int meatNum = snapshot.child("meatNum").getValue(Integer.class);
                if (totalStep - redeemedStep < 500){
                    return;
                } else {
                    int meatReward = Math.min((totalStep - redeemedStep) / 500, 7 - meatNum);
                    meatNumReference.setValue(meatNum + meatReward);
                    redeemedSteps.setValue(totalStep);
                }
                showCorgi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateLevel(){
        petLevel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int curLevel = snapshot.getValue(Integer.class);
                petState.setPetLevel(curLevel);
                showCorgi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


