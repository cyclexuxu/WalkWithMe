package neu.madcourse.walkwithme.ranking;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import neu.madcourse.walkwithme.Pedometer.Steps;
import neu.madcourse.walkwithme.rankingFra.RankFragment2;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class DRankingData2 {

    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    private List<String> usernames;
    private List<ItemRank> itemRanks;
    private String LOG = "DRankingData";
    private String today;
    protected int currentUserStep;
    private List<String> friends;
    private Map<String, Integer> usernameToSteps = new HashMap<>();
    private int i = 1;
    @SuppressLint("SimpleDateFormat")
    public DRankingData2() {
        today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        // countCurrentUserSteps();
        databaseReference1= FirebaseDatabase.getInstance().getReference("users").child(LoginActivity.currentUser); //get friends
        databaseReference2= FirebaseDatabase.getInstance().getReference("users"); //get friends steps
        usernames = new ArrayList<>();
        friends = new ArrayList<>();
        databaseReference1.child("Friends").child(LoginActivity.currentUser).setValue(true); //add them self to friends in order to create rank
        getUsernameAndSteps();
    }
    // 1)get friends list

//    private void countCurrentUserSteps() {
//        String currentUsername = LoginActivity.currentUser;
//        // DatabaseReference databaseCurrentUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUsername).child("Step Count").child(today);
//        DatabaseReference databaseCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);
//        databaseCurrentUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child("Step Count").child(today).exists()) {
//                    Steps steps = snapshot.child("Step Count").child(today).getValue(Steps.class);
//                    currentUserStep = (int) steps.getSteps();
//                } else {
//                    currentUserStep = 0;
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }

    public void getUsernameAndSteps() {
//        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child("Friends").exists()){
//                    for (DataSnapshot ds : dataSnapshot.child("Friends").getChildren()) {
//                        // String name = ds.child("username").getValue(String.class);
//                        String name = ds.getKey();
//                        Log.d("Fetch data from friends: ", name);
//                        friends.add(name);
//                        usernames.add(name);
//                        getFriendsSteps(name);
//                    }
//                }
//                Log.d("onDataChange: ", friends.toString());
//                //setStepsAndLikes(usernames);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

        databaseReference1.child("Friends").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String name = snapshot.getKey();
                Log.d("Add friends: ", name);
                friends.add(name);
                usernames.add(name);
                Log.d("Add friends: ", "onChildAdded: get frirnds step for " + name);
                getFriendsSteps(name);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//
        databaseReference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d(LOG, "onDataChange: db2 detected add");
                String username = snapshot.child("username").getValue(String.class);
                Log.d(LOG, "onDataChange: db2 detected add username " + username);
                if(usernames.contains(username)){
                    if (snapshot.child("Step Count").child(today).exists()){
                        Steps steps = snapshot.child("Step Count").child(today).getValue(Steps.class);
                        int newStep = (int) steps.getSteps();
                        databaseReference1.child("Rankings").child(username).child("steps").setValue(newStep);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(LOG, "onDataChange: db2 detected change");
                String username = snapshot.child("username").getValue(String.class);
                Log.d(LOG, "onDataChange: db2 detected change username " + username);
                if(usernames.contains(username)){
                    if (snapshot.child("Step Count").child(today).exists()){
                        Steps steps = snapshot.child("Step Count").child(today).getValue(Steps.class);
                        int newStep = (int) steps.getSteps();
                        databaseReference1.child("Rankings").child(username).child("steps").setValue(newStep);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



}

    public void getFriendsSteps(String name){
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                if(dataSnapshot.child("Ranking"))
//                //for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    // String name = ds.child("username").getValue(String.class);
//                    String name = ds.getKey();
//                    if(friends.contains(name)){
//                        //is friend with user
//                        usernames.add(name);
                Log.d(LOG, "get step for " + name);
                    int userStep = 0;
                    if (dataSnapshot.child(name).child("Step Count").child(today).exists()) {
                        Steps steps = dataSnapshot.child(name).child("Step Count").child(today).getValue(Steps.class);
                        userStep = (int) steps.getSteps();
                        usernameToSteps.put(name, userStep);
                    } else {
                        usernameToSteps.put(name, 0);
                    }

                        //int steps = usernameToSteps.get(username);
                        // int steps = random.nextInt(1500);
                         Random random = new Random();
                        //int likes = random.nextInt(10);
                        //Log.e("Loop username", username);
                        ItemRank itemRank = new ItemRank(name, userStep, 0);
                        //itemRanks.add(itemRank);
                        databaseReference1.child("Rankings").child(name).setValue(itemRank);


                Log.d("onDataChange: ", usernameToSteps.toString());
                //
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void setStepsAndLikes(List<String> usernames) {
        itemRanks = new ArrayList<>();
        Random random = new Random();
        Log.d("usernames.size(): ", usernames.size() + "");
        Log.d("usernames", usernames.toString());
        for (String username : usernames) {
            int steps = usernameToSteps.get(username);
            // int steps = random.nextInt(1500);
            int likes = random.nextInt(usernames.size());
            Log.e("Loop username", username);
            ItemRank itemRank = new ItemRank(username, steps, likes);
            itemRanks.add(itemRank);
            // databaseReference.child("Rankings").child(RankingId)
        }
        writeRanks(itemRanks);
    }
//
    private void writeRanks(List<ItemRank> itemRanks) {
        int idCount = 1;
        sortBySteps(itemRanks);
        for (int i = 0; i < itemRanks.size(); i++) {
            ItemRank current = itemRanks.get(i);
            current.setRankId(i + 1);
            current.setLikeClicked(false);
            writeSingleRanking(current.getRankId() + "", current);
            // writeSingleRanking("RankingId" + idCount, current);
            idCount++;
        }
    }
//
    private void writeSingleRanking(String idCount, ItemRank current) {
        //DatabaseReference databaseReferenceRanking = FirebaseDatabase.getInstance().getReference();
        Log.d(LOG, "writeSingleRanking: ");
        databaseReference1.child("Rankings").child(idCount).setValue(current);
    }

    private void sortBySteps(List<ItemRank> itemRanks) {
        Collections.sort(itemRanks, (rank1, rank2) -> rank2.getSteps() - rank1.getSteps());
    }
}
