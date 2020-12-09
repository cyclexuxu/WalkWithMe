package neu.madcourse.walkwithme.ranking;

import java.util.Objects;

public class ItemRank {
    private int rankId;
    private String username;
    private int steps;
    private int likesReceived;
    private boolean isLikeClicked;

    public ItemRank(String username, int steps, int likesReceived) {
        this.username = username;
        this.steps = steps;
        this.likesReceived = likesReceived;
        this.isLikeClicked = false;
    }

    public ItemRank(String username, int steps, int likesReceived, boolean likeClicked) {
        this.username = username;
        this.steps = steps;
        this.likesReceived = likesReceived;
        this.isLikeClicked = likeClicked;
    }

    public ItemRank(){}

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getLikesReceived() {
        return likesReceived;
    }

    public void setLikesReceived(int likesReceived) {
        this.likesReceived = likesReceived;
    }

    public boolean isLikeClicked() {
        return isLikeClicked;
    }

    public void setLikeClicked(boolean likeClicked) {
        isLikeClicked = likeClicked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRank itemRank = (ItemRank) o;
        return steps == itemRank.steps &&
                likesReceived == itemRank.likesReceived &&
                isLikeClicked == itemRank.isLikeClicked &&
                Objects.equals(username, itemRank.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, steps, likesReceived, isLikeClicked);
    }
}
