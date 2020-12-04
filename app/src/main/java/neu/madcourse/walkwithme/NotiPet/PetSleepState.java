package neu.madcourse.walkwithme.NotiPet;

import neu.madcourse.walkwithme.R;

public class PetSleepState extends PetState{


    @Override
    public PetState music() {
        if (cHealth != 0){
            setcHappiness(Math.min(100, cHappiness + ONE_MUSIC));
            PetState newState = new PetHappyState();
            return newState;
        } else {
            System.out.println("Feed Me first!");
            return new PetStarveState();
        }
    }

    @Override
    public PetState tip() {
        if (cHealth != 0){
            setcKnowledge(Math.min(100, cKnowledge + ONE_NEWS));
            PetState newState = new PetTipState();
            return newState;
        } else {
            System.out.println("Feed Me first!");
            return new PetStarveState();
        }
    }


    @Override
    public PetState timeout() {
        return this;
    }

    @Override
    public int getImage() {
        return R.drawable.sleep;
    }
}
