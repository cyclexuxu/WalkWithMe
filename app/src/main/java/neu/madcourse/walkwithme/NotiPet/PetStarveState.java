package neu.madcourse.walkwithme.NotiPet;

import neu.madcourse.walkwithme.R;

public class PetStarveState extends PetState{

    @Override
    public PetState music() {
        return null;
    }

    @Override
    public PetState tip() {
        return null;
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
        return R.drawable.starve;
    }
}
