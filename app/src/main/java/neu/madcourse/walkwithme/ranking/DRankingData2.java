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
        //databaseReference1.child("Likes").child(today).setValue(0); //create likes
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

//                Log.d(LOG, "onChildAdded: db2 detected add");
//                String username = snapshot.child("username").getValue(String.class);
//                Log.d(LOG, "onChildAdded: db2 detected add username " + username);
//                if(usernames.contains(username)){
//                    int likes = 0;
//                    if(snapshot.child("Likes").child(today).exists()){
//                        likes = snapshot.child("Likes").child(today).getValue(Integer.class);
//                        Log.d(LOG, "onChildAdded: db2 detected likes " + likes);
//                        //databaseReference1.child("Rankings").child(username).child("likesReceived").setValue(likes);
//                    }else{
//                        databaseReference2.child(username).child("Likes").child(today).setValue(0); //create likes
//                    }
//                    int newStep = 0;
//                    if (snapshot.child("Step Count").child(today).exists()){
//                        Steps steps = snapshot.child("Step Count").child(today).getValue(Steps.class);
//                        newStep = (int) steps.getSteps();
//                        //databaseReference1.child("Rankings").child(username).child("steps").setValue(newStep);
//                    }
//
//                    boolean liked = false;
//                    if(snapshot.child(LoginActivity.currentUser).child("Rankings").child(username).child("likeClicked").exists()){
//                        Log.d(LOG, "liked exists");
//                        liked = snapshot.child(LoginActivity.currentUser).child("Rankings").child(username).child("likeClicked").getValue(Boolean.class);
//
//                    }
//                    Log.d(LOG, "liked exists is " + liked);
//
//                    ItemRank itemRank = new ItemRank(username, newStep, likes, liked);
//                    databaseReference1.child("Rankings").child(username).setValue(itemRank);
//
//                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(LOG, "onChildChanged: db2 detected add");
                String username = snapshot.child("username").getValue(String.class);
                Log.d(LOG, "onChildChanged: db2 detected add username " + username);
                if(usernames.contains(username)){
                    int likes = 0;
                    if(snapshot.child("Likes").child(today).exists()){
                        likes = snapshot.child("Likes").child(today).getValue(Integer.class);
                        Log.d(LOG, "onDataChange: db2 detected likes " + likes);
                        //databaseReference1.child("Rankings").child(username).child("likesReceived").setValue(likes);
                    }else{
                        databaseReference2.child(username).child("Likes").child(today).setValue(0); //create likes
                    }
                    int newStep = 0;
                    if (snapshot.child("Step Count").child(today).exists()){
                        Steps steps = snapshot.child("Step Count").child(today).getValue(Steps.class);
                        newStep = (int) steps.getSteps();
                        //databaseReference1.child("Rankings").child(username).child("steps").setValue(newStep);
                    }
//                    boolean liked = false;
//                    if(snapshot.child(LoginActivity.currentUser).child("Rankings").child(username).child("likeClicked").exists()){
//                        Log.d(LOG, "liked exists");
//                        liked = snapshot.child(LoginActivity.currentUser).child("Rankings").child(username).child("likeClicked").getValue(Boolean.class);
//                        Log.d(LOG, "liked exists is " + liked);
//                    }
//
//                    Log.d(LOG, "liked " + liked);

                    //ItemRank itemRank = new ItemRank(username, newStep, likes, );
                    databaseReference1.child("Rankings").child(username).child("steps").setValue(newStep);
                    databaseReference1.child("Rankings").child(username).child("likesReceived").setValue(likes);

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

                Log.d(LOG, "get step for " + name);
                //String username = dataSnapshot.child("username").getValue(String.class);
                Log.d(LOG, "getFriendsSteps detected add username " + name);
                if(usernames.contains(name)){
                    int likes = 0;
                    if(dataSnapshot.child(name).child("Likes").child(today).exists()){
                        likes = dataSnapshot.child(name).child("Likes").child(today).getValue(Integer.class);
                        Log.d(LOG, "getFriendsSteps: db2 detected likes " + likes);
                        //databaseReference1.child("Rankings").child(username).child("likesReceived").setValue(likes);
                    }else{
                        databaseReference2.child(name).child("Likes").child(today).setValue(0); //create likes
                    }
                    int newStep = 0;
                    if (dataSnapshot.child(name).child("Step Count").child(today).exists()){
                        Steps steps = dataSnapshot.child(name).child("Step Count").child(today).getValue(Steps.class);
                        newStep = (int) steps.getSteps();
                        //databaseReference1.child("Rankings").child(username).child("steps").setValue(newStep);
                    }
                    boolean liked = false;
                    if(dataSnapshot.child(LoginActivity.currentUser).child("Rankings").child(name).child("likeClicked").exists()){
                        Log.d(LOG, "liked exists");
                        liked = dataSnapshot.child(LoginActivity.currentUser).child("Rankings").child(name).child("likeClicked").getValue(Boolean.class);
                        Log.d(LOG, "liked exists is " + liked);
                    }

                    ItemRank itemRank = new ItemRank(name, newStep, likes, liked);
                    databaseReference1.child("Rankings").child(name).setValue(itemRank);
                }

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
            if(current.isLikeClicked()){
                current.setLikeClicked(true);
            }else{
                current.setLikeClicked(false);
            }
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
