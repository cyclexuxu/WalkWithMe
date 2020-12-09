package neu.madcourse.walkwithme.Pedometer;

public class NotificationMessage {
    public String title = "";
    public String body = "";
    public int msgId = 0;
    public int imgSrc = 0;

    public NotificationMessage(String title, String body, int msgId, int imgSrc) {
        this.title= title;
        this.body= body;
        this.msgId = msgId;
        this.imgSrc = imgSrc;
    }
}
