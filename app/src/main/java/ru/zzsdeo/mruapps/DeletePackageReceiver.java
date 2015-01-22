package ru.zzsdeo.mruapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeletePackageReceiver extends BroadcastReceiver {

    public static final String PACKAGE_NAME_EXTRA = "package_name_extra";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DBUpdateIntentService.class);
        i.putExtra(PACKAGE_NAME_EXTRA, intent.getData().getSchemeSpecificPart());
        if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
            i.setAction(DBUpdateIntentService.UPDATE_PACKAGE_ACTION);
        } else {
            i.setAction(DBUpdateIntentService.DELETE_PACKAGE_ACTION);
        }
        context.startService(i);
    }
}