package neu.madcourse.walkwithme.Pedometer;


import neu.madcourse.walkwithme.R;

public class NofiticationConstants{
    //id 1 = service
    //id 2 = level change
    //id 3 = daily goal
    //id 4 = inactive
    //Remember to kill 2, 3 when log out

    public static final NotificationMessage SERVICE_START = new NotificationMessage("Your Step Service is now activated ", "Let's walk with me!", 1, R.drawable.happy);
    public static final NotificationMessage SERVICE_STOP = new NotificationMessage("\uD83D\uDE2D \uD83D\uDE2DStep Service is no longer activated ", "", 1, R.drawable.sleep);

    public static final NotificationMessage L2 = new NotificationMessage("Congratulations \uD83C\uDF8A \uD83C\uDF8A","You've leveled up to level 2!", 2, R.drawable.fireworks);
    public static final NotificationMessage L3 = new NotificationMessage("Hooray \uD83C\uDF89 \uD83C\uDF89", "That's level 3!", 2, R.drawable.fireworks);
    public static final NotificationMessage L4 = new NotificationMessage("Woo Hoo \uD83D\uDC4F \uD83D\uDC4F \uD83D\uDC4F", "You made it to level 4!", 2, R.drawable.fireworks);
    public static final NotificationMessage L5 = new NotificationMessage("❤Amazing❤", "You've reached level 5!", 2, R.drawable.fireworks);

    public static final NotificationMessage meetGoal = new NotificationMessage("\uD83D\uDC4F \uD83D\uDC4F Excellent! ", "You have achieved today's goal!", 3, R.drawable.happy);
    public static final NotificationMessage b1elowGoal = new NotificationMessage("\uD83D\uDD08 Almost there", "Only few steps away from your daily goal! \uD83D\uDEB6\u200D♂️ \uD83D\uDEB6\u200D♂️", 3, R.drawable.happy);
    public static final NotificationMessage b1elowGoal2 = new NotificationMessage("\uD83D\uDE30 Looks like you forgot the goal", "It't not too late to meet your daily goal!️", 3, R.drawable.sleep);

    public static final NotificationMessage THREE_DAYS_INACTIVE = new NotificationMessage("\uD83D\uDE30 We miss you!", "Why don't you check what is going in your app?", 4, R.drawable.music);

}
