package ru.zzsdeo.mruapps;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;

public class DBUpdateIntentService extends IntentService {

    public static final String DB_UPDATE_INTENT_SERVICE_NAME = "DBUpdateIntentService";

    public DBUpdateIntentService () {
        super(DB_UPDATE_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityInfo activityInfo = intent.getParcelableExtra(MainActivity.PARCELABLE_EXTRA);

        Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI, new String[]{StatisticTable.COLUMN_USAGE}, StatisticTable.COLUMN_PACKAGE_NAME + " like '" + activityInfo.applicationInfo.packageName + "'", null, null);
        ContentValues values = new ContentValues();
        if (c.moveToFirst()) {
            int usage = c.getInt(c.getColumnIndex(StatisticTable.COLUMN_USAGE));
            usage++;
            values.put(StatisticTable.COLUMN_USAGE, usage);
            getContentResolver().update(DBContentProvider.CONTENT_URI, values, StatisticTable.COLUMN_PACKAGE_NAME + " like '" + activityInfo.applicationInfo.packageName + "'", null);
        } else {
            values.put(StatisticTable.COLUMN_PACKAGE_NAME, activityInfo.applicationInfo.packageName);
            values.put(StatisticTable.COLUMN_USAGE, 1);
            values.put(StatisticTable.COLUMN_IGNORE, 0);
            getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
        }
        c.close();
    }
}
