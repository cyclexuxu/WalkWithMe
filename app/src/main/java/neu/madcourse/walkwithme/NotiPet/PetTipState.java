package neu.madcourse.walkwithme.NotiPet;

import neu.madcourse.walkwithme.R;

public class PetTipState extends PetState {


    @Override
    public PetState music() {
        cHappiness = Math.max(100, cHappiness + ONE_MUSIC);
        PetState newState = new PetHappyState();
        return newState;
    }

    @Override
    public PetState tip() {
        return this;
    }

    @Override
    public PetState earnMeat() {
        return null;
    }

    @Override
    public PetState timeout() {
        return null;
    }

    @Override
    public int getImage() {
        return R.drawable.happy;
    }
}
