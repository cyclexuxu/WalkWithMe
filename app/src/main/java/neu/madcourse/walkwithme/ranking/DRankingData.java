package neu.madcourse.walkwithme.ranking;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import neu.madcourse.walkwithme.Test.Steps;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class DRankingData {

    private DatabaseReference databaseReference;
    private List<String> usernames;
    private List<ItemRank> itemRanks;
    private String LOG = "DRankingData";
    private String today;
    protected int currentUserStep;

    // private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @SuppressLint("SimpleDateFormat")
    public DRankingData() {
//        Date date = new Date();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
//        today = dateFormat.format(date);
        today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        countCurrentUserSteps();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        usernames = new ArrayList<>();
        getUsername();


        Log.d(LOG, today);

        // today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        // Log.d(LOG, user.getDisplayName());

    }

    private void countCurrentUserSteps() {
        String currentUsername = LoginActivity.currentUser;

        // DatabaseReference databaseCurrentUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUsername).child("Step Count").child(today);
        DatabaseReference databaseCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);


        databaseCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                    String steps = dataSnapshot.child(today).child("steps").getValue().toString();
//                    Log.d(LOG, steps);
//                    currentUserStep = Integer.parseInt(steps);
//                }
                if(snapshot.child("Step Count").child(today).exists()) {
                    Steps steps = snapshot.child("Step Count").child(today).getValue(Steps.class);
                    currentUserStep = (int) steps.getSteps();
                } else {
                    currentUserStep = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getUsername() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String name = ds.child("username").getValue(String.class);
                    usernames.add(name);
                }
                Log.d("onDataChange: ", usernames.size()+"");
                setRandomStepsAndLikes(usernames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setRandomStepsAndLikes(List<String> usernames) {
        itemRanks = new ArrayList<>();
        Random random = new Random();
        Log.d("usernames.size(): ",usernames.size()+"");
        for (String username : usernames) {
            int steps = random.nextInt(1500);
            int likes = random.nextInt(usernames.size());
            Log.e("Loop username", username);
            if (username.equals(LoginActivity.currentUser)) {
                steps = currentUserStep;
                Log.d(LOG, String.valueOf(steps));
            }

            // Log.d(LOG, String.valueOf(steps));
            ItemRank itemRank = new ItemRank(username, steps, likes);
            itemRanks.add(itemRank);
            // databaseReference.child("Rankings").child(RankingId)
        }
        writeRanks(itemRanks);
    }

    private void writeRanks(List<ItemRank> itemRanks) {
        int idCount = 1;
        sortBySteps(itemRanks);
        for (int i = 0; i < itemRanks.size(); i++) {
            ItemRank current = itemRanks.get(i);
            current.setRankId(i + 1);
            writeSingleRanking(current.getRankId() + "", current);
            // writeSingleRanking("RankingId" + idCount, current);
            idCount++;
        }
    }

    private void writeSingleRanking(String idCount, ItemRank current) {
        DatabaseReference databaseReferenceRanking = FirebaseDatabase.getInstance().getReference();
        databaseReferenceRanking.child("Rankings").child(idCount).setValue(current);
    }


    private void sortBySteps(List<ItemRank> itemRanks) {
        Collections.sort(itemRanks, (rank1, rank2) -> rank2.getSteps() - rank1.getSteps());
    }

    // get all the users in our app



}
