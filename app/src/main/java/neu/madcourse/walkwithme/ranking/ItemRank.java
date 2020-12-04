package neu.madcourse.walkwithme.ranking;

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
    }

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
}
