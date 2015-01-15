package ru.zzsdeo.mruapps;


import android.app.IntentService;
import android.content.Intent;

public class ManageCacheIntentService extends IntentService {

    private static final String MANAGE_CACHE_INTENT_SERVICE_NAME = "DBUpdateIntentService";

    public ManageCacheIntentService() {
        super(MANAGE_CACHE_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Utils.createCache(getApplicationContext());
    }
}
