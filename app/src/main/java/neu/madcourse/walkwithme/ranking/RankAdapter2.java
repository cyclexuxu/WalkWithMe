package neu.madcourse.walkwithme.ranking;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import neu.madcourse.walkwithme.R;
import neu.madcourse.walkwithme.userlog.LoginActivity;

public class RankAdapter2 extends RecyclerView.Adapter {
    private String LOG = "RankAdapter";
    List<ItemRank> itemRankList;
    private boolean textClick;
    private static String TAG = "RANK ADAPTER 2";
    private DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.currentUser).child("Rankings"); //dy
    public RankAdapter2(List<ItemRank> itemRankList) {
        this.itemRankList = itemRankList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        // textClick = true;
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;
        ItemRank itemRank = itemRankList.get(position);
        // Log.d(LOG, String.valueOf(itemRank.getRankId()));
        viewHolderClass.tvRankId.setText(String.valueOf(itemRank.getRankId()));
        viewHolderClass.tvUsername.setText(itemRank.getUsername());
        viewHolderClass.tvSteps.setText(String.valueOf(itemRank.getSteps()));
        viewHolderClass.tvLikes.setText(String.valueOf(itemRank.getLikesReceived()));

        viewHolderClass.checkLikeStatus(itemRank);
        //  add like action

        String friend = String.valueOf(itemRank.getUsername());
        DatabaseReference likeRef =  FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.currentUser).child("Rankings").child(friend);
        DatabaseReference users =  FirebaseDatabase.getInstance().getReference().child("users");
        // viewHolderClass.getLikeStatus()
        viewHolderClass.ibLike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textClick = true;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(textClick) {
                            viewHolderClass.ibLike.setImageResource(R.drawable.ic_action_like);
                            Log.d(TAG, "Click the heart!");
                            Log.d(TAG, "Click the heart! Friend is  " + friend);
                            //viewHolderClass.ibLike.setImageResource(R.drawable.ic_action_like);
                            likeRef.child("likeClicked").setValue(true);
                            final String today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                            itemRank.setLikesReceived(itemRank.getLikesReceived() + 1);
                            //viewHolderClass.ibLike.setImageResource(R.drawable.ic_action_like);
                            //users.child(itemRank.getUsername()).child("Likes").child(today).setValue(itemRank.getLikesReceived());
                            likeRef.child("likesReceived").setValue(itemRank.getLikesReceived());
                            textClick = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }


    @Override
    public int getItemCount() {
        return itemRankList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView tvRankId, tvUsername, tvSteps, tvLikes;
        ImageButton ibLike;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            tvRankId = itemView.findViewById(R.id.tvRank);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvSteps = itemView.findViewById(R.id.tvSteps);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ibLike = itemView.findViewById(R.id.btnLikeIcon);
        }

        public void checkLikeStatus(ItemRank itemRank) {
            String id = String.valueOf(itemRank.getUsername());//dy
            DatabaseReference isClickedRef =  FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.currentUser).child("Rankings").child(id).child("likeClicked"); //dy
            isClickedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isLikedClicked = (Boolean) snapshot.getValue();
                    if (isLikedClicked) {
                        ibLike.setImageResource(R.drawable.ic_action_like);
                        tvLikes.setText(String.valueOf(itemRank.getLikesReceived()));
                        // isClickedRef.child(String.valueOf(itemRank.getRankId())).child("likesReceived").setValue(itemRank.getLikesReceived());
                        // isClickedRef.child(String.valueOf(itemRank.getRankId())).child("likeClicked").setValue(true);
                    } else {
                        ibLike.setImageResource(R.drawable.ic_action_dislike);
                    }
                    // Log.d(LOG, String.valueOf(isLikedClicked) + "~~~~~~~~~~~~~~");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

//    @Override
//    public long getItemId(int position) {
//        Product product = mProductList.get(position);
//        return product.pid;
//    }

}
