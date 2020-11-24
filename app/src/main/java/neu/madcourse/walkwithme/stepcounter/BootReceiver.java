package neu.madcourse.walkwithme.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG= "Broadcast Receiver: ";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceLauncher = new Intent(context, StepService.class);
        context.startService(serviceLauncher);
        Log.e(TAG, "BootReceiver started StepService");
    }
}
