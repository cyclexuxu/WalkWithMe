package neu.madcourse.walkwithme.NotiPet;

import neu.madcourse.walkwithme.R;

public class PetHappyState extends PetState {


    @Override
    public PetState music() {

        return this;
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
        return R.drawable.music;
    }
}
