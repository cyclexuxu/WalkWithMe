package neu.madcourse.walkwithme.NotiPet;

import neu.madcourse.walkwithme.R;

public class PetStarveState extends PetState{

    @Override
    public PetState music() {
        return this;
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
        return new PetSleepState();
    }

    @Override
    public int getImage() {
        return R.drawable.starve;
    }
}
