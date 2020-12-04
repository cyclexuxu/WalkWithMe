package neu.madcourse.walkwithme.NotiPet;

import android.widget.Toast;

import neu.madcourse.walkwithme.R;

public class PetStarveState extends PetState{

    @Override
    public PetState music() throws PetStarvingException {
        throw new PetStarvingException();
    }

    @Override
    public PetState tip() throws PetStarvingException {
        throw new PetStarvingException();
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
