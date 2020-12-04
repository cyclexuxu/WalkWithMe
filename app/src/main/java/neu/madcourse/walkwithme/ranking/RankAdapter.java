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

import java.util.List;

import neu.madcourse.walkwithme.R;

public class RankAdapter extends RecyclerView.Adapter {
    private String LOG = "RankAdapter";
    List<ItemRank> itemRankList;
    private boolean textClick;
    private DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("Rankings");;

    public RankAdapter(List<ItemRank> itemRankList) {
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

        // viewHolderClass.getLikeStatus()
        viewHolderClass.ibLike.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                textClick = true;
                likeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (textClick) {
                            viewHolderClass.ibLike.setImageResource(R.drawable.ic_action_like);
                            viewHolderClass.tvLikes.setText(String.valueOf(itemRank.getLikesReceived() + 1));
                            itemRank.setLikesReceived(itemRank.getLikesReceived() + 1);
                            likeReference.child(String.valueOf(itemRank.getRankId())).child("likesReceived").setValue(itemRank.getLikesReceived());
                            likeReference.child(String.valueOf(itemRank.getRankId())).child("likeClicked").setValue(true);
                            itemRank.setLikeClicked(true);
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
            String id = String.valueOf(itemRank.getRankId());
            DatabaseReference isClicked =  FirebaseDatabase.getInstance().getReference("Rankings").child(id).child("likeClicked");
            isClicked.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isLikedClicked = (Boolean) snapshot.getValue();
                    if (isLikedClicked) {
                        ibLike.setImageResource(R.drawable.ic_action_like);
                    }
                    // Log.d(LOG, String.valueOf(isLikedClicked) + "~~~~~~~~~~~~~~");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
