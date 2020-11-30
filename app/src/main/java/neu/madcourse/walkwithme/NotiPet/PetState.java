package neu.madcourse.walkwithme.NotiPet;

public abstract class PetState {

    protected static int cHealth = 0;
    protected static int cHappiness = 0;
    protected static int cKnowledge = 0;
    protected static int meat = 3;
    protected final static int ONE_MEAT = 5;
    protected final static int ONE_MUSIC = 5;
    protected final static int ONE_NEWS = 5;

    public abstract PetState music() throws PetStarvingException;
    public abstract PetState tip() throws PetStarvingException;
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



    public int getcHealth() {
        return cHealth;
    }

    public void setcHealth(int cHealth) {
        PetState.cHealth = cHealth;
    }

    public int getcHappiness() {
        return cHappiness;
    }

    public void setcHappiness(int cHappiness) {
        PetState.cHappiness = cHappiness;
    }

    public int getcKnowledge() {
        return cKnowledge;
    }

    public void setcKnowledge(int cKnowledge) {
        PetState.cKnowledge = cKnowledge;
    }

    public void setMeat(int meat) {
        PetState.meat = meat;
    }

    public int getMeat() {
        return meat;
    }

}
