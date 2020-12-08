package neu.madcourse.walkwithme.rankingFra;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.LogDescriptor;
import com.google.firebase.FirebaseError;
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

import io.grpc.internal.LogExceptionRunnable;
import neu.madcourse.walkwithme.R;
import neu.madcourse.walkwithme.ranking.DRankingData;
import neu.madcourse.walkwithme.ranking.DRankingData2;
import neu.madcourse.walkwithme.ranking.ItemRank;
import neu.madcourse.walkwithme.ranking.RankAdapter;
import neu.madcourse.walkwithme.ranking.RankAdapter2;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class RankFragment2 extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView;
    private RankAdapter2 rankAdapter;
    private DatabaseReference allUsers;
    private DatabaseReference step_ref;
    private String LOG = "RANKING_ACTIVITY";
    private TextView tvCurrentUser;
    private SharedPreferences sharedPreferences;
    private TextView etDateOfToday;
    List<String> usernames;
    Map<String, ItemRank> map;
    private View viewTest = null;
    List<ItemRank> itemRankList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("rank", Context.MODE_PRIVATE);
        Log.d(LOG, "onCreated: ");
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        Log.d(LOG, "onViewCreated: ");
        //DRankingData2 dRankingData = new DRankingData2();
        //dRankingData.getFriendsSteps();

        etDateOfToday = view.findViewById(R.id.etToday);
        String dateOfToday = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        String title = dateOfToday + "  You ranked here";
        etDateOfToday.setText(title);
        tvCurrentUser = view.findViewById(R.id.tvUserName);
        tvCurrentUser.setText(LoginActivity.currentUser);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usernames = new ArrayList<>();
        map = new HashMap<>(); //easy to search friends
        viewTest= view;

        //map all users step
        allUsers = FirebaseDatabase.getInstance().getReference().child("users");
        //getAllUsers();
        final String today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // ItemRank itemRank = ds.child("username");
                    String username = ds.child("username").getValue(String.class);

                    map.put(username, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

            // fetch data from firebase
        //databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Log.d(LOG, "updateRank...");
        step_ref = FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.currentUser);
        step_ref.child("Rankings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(LOG, "onChildAdded: ");
                itemRankList = new ArrayList<>();
                for(DataSnapshot d : snapshot.getChildren()) {
                    String username = d.child("username").getValue(String.class);
                    int steps = Integer.parseInt(String.valueOf(d.child("steps").getValue(Long.class)));
                    int likesReceived = Integer.parseInt(String.valueOf(d.child("likesReceived").getValue(Long.class)));
                    Log.d(LOG, "onDataChange: number of likes");
                    //boolean likeClicked = Boolean.valueOf(d.child("likesClicked").getValue(String.class));
                    //Log.d(LOG, "onDataChange: number of likes is " + likeClicked);
                    ItemRank itemRank = new ItemRank(username, steps, likesReceived);
                    itemRankList.add(itemRank);
                    map.put(username, itemRank);
                    usernames.add(username);

                    Log.d(LOG, "Set Adapter...");
                    Log.d(LOG, itemRankList.toString());
                }

//
                processItemRankList(view, itemRankList);
                rankAdapter = new RankAdapter2(itemRankList);
                //rankAdapter.setHasStableIds(true);
                recyclerView.setAdapter(rankAdapter);
               // ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//

        final FloatingActionButton search = view.findViewById(R.id.addNewFriend);
        Log.d("Search Friends", "about to click");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Search Friends", "onClick: click add friend");
                startSearchDialog(v);
            }
        });


    }

    private void updateRank(View view) {
        //show rank
    }


//
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rank, container, false);
    }


    private void processItemRankList(@NonNull final View view, List<ItemRank> itemRankList) {
        sortBySteps(itemRankList);
        addRankIdToEachItem(view, itemRankList);
    }

    private void addRankIdToEachItem(@NonNull final View view, List<ItemRank> itemRankList) {
        int indexOfCurrentUser = 0;
        for (int i = 0; i < itemRankList.size(); i++) {
            ItemRank currentItem = itemRankList.get(i);
            currentItem.setRankId(i + 1);
            if (currentItem.getUsername().equals(LoginActivity.currentUser)) {
                Log.d(LOG, "find current user " + currentItem.getUsername());
                indexOfCurrentUser = i;
                setCurrentUserStatus(view, currentItem);
            }
        }
        itemRankList.remove(indexOfCurrentUser);
    }

    private void setCurrentUserStatus(@NonNull final View view, ItemRank currentItem) {
        Log.d( "setCurrentUserStatus: ", "starting");
        Log.d( "setCurrentUserStatus is null?: ", currentItem.getRankId()+"");

        TextView tvRank = view.findViewById(R.id.tvRank);
        tvRank.setText(currentItem.getRankId() + "");
        TextView tvSteps = view.findViewById(R.id.tvSteps);
        tvSteps.setText(String.valueOf(currentItem.getSteps()));
        TextView tvLikes = view.findViewById(R.id.tvLikes);
        tvLikes.setText(String.valueOf(currentItem.getLikesReceived()));
    }

    private void sortBySteps(List<ItemRank> itemRankList) {
        Collections.sort(itemRankList, (itemOne, itemTwo) -> itemTwo.getSteps() - itemOne.getSteps());
    }

    public void startSearchDialog(View view){
        final Dialog d = new Dialog(getActivity());
        d.setTitle("SearchFriends");
        d.setContentView(R.layout.dailog_search_friend);
        Button exist = (Button) d.findViewById(R.id.exist);
        Button add = (Button) d.findViewById(R.id.add);
        EditText input = (EditText) d.findViewById(R.id.search);

        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //reset goal
                Log.d("Search Friend", "add button is clicked");
                String friend = input.getText().toString();
                Log.v("EditText", friend);
                if(searchFriend(friend)) {
                    //add friend
                    //add to friend list

                    step_ref.child("Friends").child(friend).setValue(true);
                    Toast.makeText(d.getContext(), "You are now friend with "+ friend, Toast.LENGTH_SHORT).show();
                    d.dismiss();

                }else{
                    Toast.makeText(d.getContext(), "We cannot find the username", Toast.LENGTH_SHORT).show();
                }
            }
        });
        exist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d("Search Friend", "exist button is clicked");
                d.dismiss();
            }
        });
        d.show();

    }

    private boolean searchFriend(String friend) {
        Log.d("searchFriend: ", "start to search");
        Log.d("searchFriend: ", map.toString());
       //check map
        if(map.containsKey(friend)){
            Log.d("searchFriend: ", friend + " is in list");
            return true;
        }else{
            Log.d("searchFriend: ", friend + " is NOT in list");
            return false;
        }
    }

    @Override
    public void onClick(View view) {

    }
}