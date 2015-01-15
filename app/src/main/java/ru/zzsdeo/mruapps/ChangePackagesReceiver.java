package ru.zzsdeo.mruapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ChangePackagesReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_FIRST_LAUNCH)) {

            context.startService(new Intent(context, ManageCacheIntentService.class));
        }
    }
}
