package ru.zzsdeo.mruapps;


import android.app.IntentService;
import android.content.Intent;

public class LaunchAppIntentService extends IntentService {

    private static final String LAUNCH_APP_INTENT_SERVICE_NAME = "LaunchAppIntentService";
    public static final String PARCELABLE_EXTRA = "parcelable_extra";

    public LaunchAppIntentService() {
        super(LAUNCH_APP_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppsCollection apps = new AppsCollection(getApplicationContext());
        int viewIndex = intent.getIntExtra(Widget.VIEW_INDEX_EXTRA, 0);

        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.setClassName(apps.getMRUapps().get(viewIndex).activityInfo.applicationInfo.packageName, apps.getMRUapps().get(viewIndex).activityInfo.name);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(launchIntent);

        Intent serviceIntent = new Intent(getApplicationContext(), DBUpdateIntentService.class);
        serviceIntent.setAction(DBUpdateIntentService.LAUNCH_ACTION);
        serviceIntent.putExtra(PARCELABLE_EXTRA, apps.getMRUapps().get(viewIndex));
        getApplicationContext().startService(serviceIntent);
    }
}
