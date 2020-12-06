package neu.madcourse.walkwithme.ranking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import neu.madcourse.walkwithme.R;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class RankingActivity extends AppCompatActivity {
    private List<ItemRank> itemRankList;
    private RecyclerView recyclerView;
    private RankAdapter rankAdapter;
    private DatabaseReference databaseReference;
    private String LOG = "RANKING_ACTIVITY";
    private TextView tvCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);


        // DRankingData dRankingData = new DRankingData();

        tvCurrentUser = findViewById(R.id.tvUserName);
        tvCurrentUser.setText(LoginActivity.currentUser);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRankList = new ArrayList<>();

        // fetch data from firebase
        // databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference = FirebaseDatabase.getInstance().getReference("Rankings");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    // ItemRank itemRank = ds.child("username");
                    String username = ds.child("username").getValue(String.class);

                    // Log.d(LOG, String.valueOf(ds.child("Step Count").getValue(Long.class)));
                    int steps = Integer.parseInt(String.valueOf(ds.child("steps").getValue(Long.class)));
                    int likesReceived =  Integer.parseInt(String.valueOf(ds.child("likesReceived").getValue(Long.class)));
                    ItemRank itemRank = new ItemRank(username, steps, likesReceived);
                    itemRankList.add(itemRank);
                }
                processItemRankList(itemRankList);
                // need to filter out the current user

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
        int indexOfCurrentUser = -1;
        for (int i = 0; i < itemRankList.size(); i++) {
            ItemRank currentItem = itemRankList.get(i);
            currentItem.setRankId(i + 1);
            if (currentItem.getUsername().equals(LoginActivity.currentUser)) {
                indexOfCurrentUser = i;
                setCurrentUserStatus(currentItem);
            }
        }
        itemRankList.remove(indexOfCurrentUser);

    }

    private void setCurrentUserStatus(ItemRank currentItem) {
        TextView tvRank = findViewById(R.id.tvRank);
        tvRank.setText(String.valueOf(currentItem.getRankId()));
        TextView tvSteps = findViewById(R.id.tvSteps);
        tvSteps.setText(String.valueOf(currentItem.getSteps()));
        TextView tvLikes = findViewById(R.id.tvLikes);
        tvLikes.setText(String.valueOf(currentItem.getLikesReceived()));

    }

    private void sortBySteps(List<ItemRank> itemRankList) {
        Collections.sort(itemRankList, (itemOne, itemTwo) -> itemTwo.getSteps() - itemOne.getSteps());
    }

}