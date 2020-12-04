package neu.madcourse.walkwithme.NotiPet;

public abstract class PetState {

    protected static int cHealth = 0;
    protected static int cHappiness = 0;
    protected static int cKnowledge = 0;
    protected static int meat = 3;
    protected static boolean isPlayingMusic = false;


    protected static int petLevel = 0;
    protected final static int ONE_MEAT = 5;
    protected final static int ONE_MUSIC = 5;
    protected final static int ONE_NEWS = 5;

    public abstract PetState music() throws PetStarvingException;
    public abstract PetState tip() throws PetStarvingException;
    public abstract PetState timeout();

    public abstract int getImage();


    public PetState feed() throws InsufficientMeatException {
        if (meat <= 0) {
            throw new InsufficientMeatException();
        }

        setcHealth(Math.min(100, cHealth + this.ONE_MEAT));
        meat --;

        PetState newState = new PetEatState();
        return newState;
    }


    public int getPetLevel() {
        return petLevel;
    }

    public void setPetLevel(int petLevel) {
        PetState.petLevel = petLevel;
    }

    public int getcHealth() {
        return cHealth;
    }

    public void setcHealth(int cHealth) {
        if (cHealth == 100 && cKnowledge == 100 && cHappiness == 100){
                int oldPetLevel = this.petLevel;
                setPetLevel(++oldPetLevel);
                setcKnowledge(0);
                setcHappiness(0);
                setcHealth(0);
        } else {
            PetState.cHealth = cHealth;
        }
    }

    public int getcHappiness() {
        return cHappiness;
    }

    public void setcHappiness(int cHappiness) {
        if (cHappiness == 100 && cHealth == 100 && cKnowledge == 100){
                int oldPetLevel = this.petLevel;
                setPetLevel(++oldPetLevel);
                setcKnowledge(0);
                setcHappiness(0);
                setcHealth(0);

        } else {
            PetState.cHappiness = cHappiness;
        }
    }

    public int getcKnowledge() {
        return cKnowledge;
    }

    public void setcKnowledge(int cKnowledge) {
        if (cKnowledge == 100 && this.getcHealth() == 100 && this.getcHappiness() == 100){
                int oldPetLevel = this.petLevel;
                setPetLevel(++oldPetLevel);
                setcHealth(0);
                setcHappiness(0);
                setcKnowledge(0);
            }
         else {
            PetState.cKnowledge = cKnowledge;
        }
    }

    public void setMeat(int meat) {
        PetState.meat = meat;
    }

    public int getMeat() {
        return meat;
    }

}
