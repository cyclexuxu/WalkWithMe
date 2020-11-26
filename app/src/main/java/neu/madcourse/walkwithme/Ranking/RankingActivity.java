package neu.madcourse.walkwithme.Ranking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import neu.madcourse.walkwithme.R;

public class RankingActivity extends AppCompatActivity {
    private List<ItemRank> itemRankList;
    private RecyclerView recyclerView;
    private RankAdapter rankAdapter;
    private DatabaseReference databaseReference;
    private String LOG = "RANKING_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRankList = new ArrayList<>();

        // fetch data from firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String username = ds.child("username").getValue(String.class);
                    Log.d(LOG, ds.child("Step Count").getValue(String.class));
                    int steps = Integer.parseInt(Objects.requireNonNull(ds.child("Step Count").getValue(String.class)));
                    int likesReceived =  Integer.parseInt(Objects.requireNonNull(ds.child("Likes").getValue(String.class)));
                    ItemRank itemRank = new ItemRank(username, steps, likesReceived);
                    itemRankList.add(itemRank);
                }
                processItemRankList(itemRankList);
                // pass the fetched data to adapter
                rankAdapter = new RankAdapter(itemRankList);
                recyclerView.setAdapter(rankAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void processItemRankList(List<ItemRank> itemRankList) {
        sortBySteps(itemRankList);
        addRankIdToEachItem(itemRankList);
    }

    private void addRankIdToEachItem(List<ItemRank> itemRankList) {
        for (int i = 0; i < itemRankList.size(); i++) {
            ItemRank currentItem = itemRankList.get(i);
            currentItem.setRankId(i + 1);
        }
    }

    private void sortBySteps(List<ItemRank> itemRankList) {
        Collections.sort(itemRankList, (itemOne, itemTwo) -> itemTwo.getSteps() - itemOne.getSteps());
    }
}