package neu.madcourse.walkwithme.Ranking;

public class ItemRank {
    private int rankId;
    private String userName;
    private int steps;
    private int likesReceived;

    public ItemRank(String userName, int steps, int likesReceived) {
        this.userName = userName;
        this.steps = steps;
        this.likesReceived = likesReceived;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


}
