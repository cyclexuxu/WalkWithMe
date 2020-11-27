package neu.madcourse.walkwithme.NotiPet;

import android.app.AlarmManager;

import neu.madcourse.walkwithme.R;

public abstract class PetState {

    protected static int cHealth = 0;
    protected static int cHappiness = 0;
    protected static int cKnowledge = 0;
    protected static int meat = 3;
    protected final static int ONE_MEAT = 5;
    protected final static int ONE_MUSIC = 5;
    protected final static int ONE_NEWS = 5;

    public abstract PetState music();
    public abstract PetState tip();
    public abstract PetState earnMeat();
    public abstract PetState timeout();

    public abstract int getImage();


    public PetState feed() throws InsufficientMeatException {
        if (meat <= 0) {
            throw new InsufficientMeatException();
        }

        cHealth = Math.min(100, cHealth + this.ONE_MEAT);
        meat --;

        PetState newState = new PetEatState();
        return newState;
    }


    private static String[] cHealthDialog = {"I'm hungry, do you mind walking for getting meats for me?", "Yummy"};
    private static int[] healthImage = {R.drawable.eat, R.drawable.starve};

    private static String[] cHappinessDialog = {"I'm lonely, do you mind sharing a music with me?", "I'm happy!"};
    private static int[] happinessImage = {R.drawable.music};

    private static String[] cKnowledgeDialog = {"I want to learn something about health, do you want to know with me?", "Good to know!"};
    private static int[] knowledgeImage = {R.drawable.happy};

    private static int[] onTouchImage = {R.drawable.sleep, R.drawable.run};


    public int getcHealth() {
        return cHealth;
    }

    public static void setcHealth(int cHealth) {
        PetState.cHealth = cHealth;
    }

    public int getcHappiness() {
        return cHappiness;
    }

    public static void setcHappiness(int cHappiness) {
        PetState.cHappiness = cHappiness;
    }

    public int getcKnowledge() {
        return cKnowledge;
    }

    public static void setcKnowledge(int cKnowledge) {
        PetState.cKnowledge = cKnowledge;
    }

    public static String[] getcHealthDialog() {
        return cHealthDialog;
    }

    public static void setcHealthDialog(String[] cHealthDialog) {
        PetState.cHealthDialog = cHealthDialog;
    }

    public static int[] getHealthImage() {
        return healthImage;
    }

    public static void setHealthImage(int[] healthImage) {
        PetState.healthImage = healthImage;
    }

    public static String[] getcHappinessDialog() {
        return cHappinessDialog;
    }

    public static void setcHappinessDialog(String[] cHappinessDialog) {
        PetState.cHappinessDialog = cHappinessDialog;
    }

    public static int[] getHappinessImage() {
        return happinessImage;
    }

    public static void setHappinessImage(int[] happinessImage) {
        PetState.happinessImage = happinessImage;
    }

    public static String[] getcKnowledgeDialog() {
        return cKnowledgeDialog;
    }

    public static void setcKnowledgeDialog(String[] cKnowledgeDialog) {
        PetState.cKnowledgeDialog = cKnowledgeDialog;
    }

    public static int[] getKnowledgeImage() {
        return knowledgeImage;
    }

    public static void setKnowledgeImage(int[] knowledgeImage) {
        PetState.knowledgeImage = knowledgeImage;
    }

    public static int[] getOnTouchImage() {
        return onTouchImage;
    }

    public static void setOnTouchImage(int[] onTouchImage) {
        PetState.onTouchImage = onTouchImage;
    }
}
