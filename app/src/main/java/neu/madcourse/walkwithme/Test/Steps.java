package neu.madcourse.walkwithme.Test;

public class Steps {

    private long steps;

    private String date ;

    public Steps(long steps,String date) {
        this.steps = steps;
        this.date = date;
    }

    public Steps(){

    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
