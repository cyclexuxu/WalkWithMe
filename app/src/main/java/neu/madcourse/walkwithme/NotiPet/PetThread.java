package neu.madcourse.walkwithme.NotiPet;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class PetThread implements Runnable{

    public static Handler mHandler;

    public void run() {
        Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // process incoming messages here
            }
        };

        Looper.loop();
    }

}
