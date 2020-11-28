package neu.madcourse.walkwithme.Notifications;

import neu.madcourse.walkwithme.R;

public class NotiMessage {
    private final static String[] HEALTH_DIALOG = {"I'm hungry, do you mind walking for getting meats for me?", "Yummy"};
    private final static String[] HAPPINESS_DIALOG = {"I'm lonely, do you mind sharing a music with me?", "I'm happy!"};
    private final static String[] KNOWLEDGE_DIALOG = {"I want to learn something about health, do you want to know with me?", "Good to know!"};
    private String runProgressNoti;

    public String getRunProgressNoti() {
        return runProgressNoti;
    }

    public void setRunProgressNoti(String runProgressNoti) {
        this.runProgressNoti = runProgressNoti;
    }
}




