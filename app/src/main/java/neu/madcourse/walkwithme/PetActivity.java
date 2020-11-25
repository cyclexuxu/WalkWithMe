package neu.madcourse.walkwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class PetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        ImageView corgi = (ImageView)findViewById(R.id.imageView);
//        ObjectAnimator animation = ObjectAnimator.ofFloat(corgi, "translationX", 100f);
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
        animation.setDuration(2000);
        corgi.startAnimation(animation);
    }

}