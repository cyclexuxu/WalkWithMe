package neu.madcourse.walkwithme.ranking;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import neu.madcourse.walkwithme.userlog.User;

public class DRankingData {

    private DatabaseReference databaseReference;
    private List<String> usernames;
    private List<ItemRank> itemRanks;
    private String LOG = "DRankingData";

    public DRankingData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        usernames = new ArrayList<>();
        getUsername();
    }

    public void getUsername() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String name = ds.child("username").getValue(String.class);
                    usernames.add(name);
                }
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
        for (String username : usernames) {
            int steps = random.nextInt(20000);
            int likes = random.nextInt(50);
            Log.d(LOG, String.valueOf(steps));
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
