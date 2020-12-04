package neu.madcourse.walkwithme.NotiPet;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import neu.madcourse.walkwithme.R;

public class CustomToast {

    public static final int LENGTH_SHORT = 1;
    public static final int LENGTH_LONG = 3;

    Toast toast;
    TextView toastView;
    Context context;

    public CustomToast(Context context){
        toast = new Toast(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastRoot = inflater.inflate(R.layout.customized_toast, null);
        toastView = (TextView)toastRoot.findViewById(R.id.per_Toast);
        toast.setView(toastRoot);

    }

    public void setDuration(int duration){
        toast.setDuration(duration);
    }

    public void setText(CharSequence text){
        toastView.setText(text);
    }


    public void setGravity(int xOffset, int yOffset){
        toast.setGravity(Gravity.BOTTOM|Gravity.LEFT, xOffset, yOffset);
    }

    public static CustomToast makeText(Context context, CharSequence text, int duration, int xOffset, int yOffset){
        CustomToast customToast = new CustomToast(context);
        customToast.setText(text);
        customToast.setGravity(xOffset, yOffset);
        customToast.setDuration(duration);

        return customToast;
    }

    public void show(){
        toast.show();
    }

}
