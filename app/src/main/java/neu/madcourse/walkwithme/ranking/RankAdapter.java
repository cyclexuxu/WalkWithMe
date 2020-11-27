package neu.madcourse.walkwithme.ranking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import neu.madcourse.walkwithme.R;

public class RankAdapter extends RecyclerView.Adapter {
    private String LOG = "RankAdapter";
    List<ItemRank> itemRankList;


    public RankAdapter(List<ItemRank> itemRankList) {
        this.itemRankList = itemRankList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);

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
    }

    @Override
    public int getItemCount() {
        return itemRankList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView tvRankId, tvUsername, tvSteps, tvLikes;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            tvRankId = itemView.findViewById(R.id.tvRank);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvSteps = itemView.findViewById(R.id.tvSteps);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }
    }
}
