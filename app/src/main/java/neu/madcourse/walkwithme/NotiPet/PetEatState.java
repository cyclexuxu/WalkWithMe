package neu.madcourse.walkwithme.NotiPet;

import neu.madcourse.walkwithme.R;

public class PetEatState extends PetState {

    public PetEatState() {
        // set time out 10s.

    }

    @Override
    public PetState feed() throws InsufficientMeatException {
        return this;
    }

    @Override
    public PetState music() {
        cHappiness = Math.max(100, cHappiness + ONE_MUSIC);
        PetState newState = new PetHappyState();
        return newState;
    }

    @Override
    public PetState tip() {
        cKnowledge = Math.max(100, cKnowledge + ONE_NEWS);
        PetState newState = new PetTipState();
        return newState;
    }

    @Override
    public PetState earnMeat() {
        return null;
    }

    @Override
    public PetState timeout() {
        return new PetSleepState();
    }

    @Override
    public int getImage() {
        return R.drawable.eat;
    }
}
