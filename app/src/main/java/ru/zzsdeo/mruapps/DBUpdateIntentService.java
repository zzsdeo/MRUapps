package ru.zzsdeo.mruapps;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.SyncStateContract;

import java.util.List;

public class DBUpdateIntentService extends IntentService {

    private static final String DB_UPDATE_INTENT_SERVICE_NAME = "DBUpdateIntentService";
    public static final String LAUNCH_ACTION = "launch_action";
    public static final String IGNORE_ACTION = "ignore_action";
    public static final String REMOVE_FROM_IGNORE_ACTION = "remove_from_ignore_action";
    public static final int RESULT_OK = 1;

    public DBUpdateIntentService () {
        super(DB_UPDATE_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.getAction().equals(LAUNCH_ACTION)) {
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

        if (intent.getAction().equals(IGNORE_ACTION)) {
            List<ResolveInfo> ignoredApps = intent.getParcelableArrayListExtra(MainActivity.PARCELABLE_EXTRA);
            ResultReceiver receiver = intent.getParcelableExtra(MainActivity.RECEIVER_EXTRA);
            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI, new String[] {StatisticTable.COLUMN_PACKAGE_NAME}, null, null, null);
            ContentValues values = new ContentValues();
            if (c.moveToFirst()) {
                do {
                    if (ignoredApps.size() != 0) {
                        for (int i = 0; i < ignoredApps.size(); i++) {
                            if (c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME)).equals(ignoredApps.get(i).activityInfo.applicationInfo.packageName)) {
                                values.put(StatisticTable.COLUMN_IGNORE, 1);
                                getContentResolver().update(DBContentProvider.CONTENT_URI, values, StatisticTable.COLUMN_PACKAGE_NAME + " like '" + ignoredApps.get(i).activityInfo.applicationInfo.packageName + "'", null);
                                values.clear();
                                ignoredApps.remove(i);
                            }
                        }
                    } else {
                        break;
                    }
                } while (c.moveToNext());
            }
            if (ignoredApps.size() != 0) {
                for (ResolveInfo ri : ignoredApps) {
                    values.put(StatisticTable.COLUMN_PACKAGE_NAME, ri.activityInfo.applicationInfo.packageName);
                    values.put(StatisticTable.COLUMN_USAGE, 0);
                    values.put(StatisticTable.COLUMN_IGNORE, 1);
                    getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                    values.clear();
                }
            }
            c.close();
            receiver.send(RESULT_OK, null);
        }

        if (intent.getAction().equals(REMOVE_FROM_IGNORE_ACTION)) {
            List<ResolveInfo> ignoredApps = intent.getParcelableArrayListExtra(IgnoredAppsActivity.IGNORED_PARCELABLE_EXTRA);
            ResultReceiver receiver = intent.getParcelableExtra(IgnoredAppsActivity.IGNORED_RECEIVER_EXTRA);
            ContentValues values = new ContentValues();
            values.put(StatisticTable.COLUMN_IGNORE, 0);
            for (ResolveInfo ri : ignoredApps) {
                getContentResolver().update(DBContentProvider.CONTENT_URI, values, StatisticTable.COLUMN_PACKAGE_NAME + " like '" + ri.activityInfo.applicationInfo.packageName + "'", null);
            }
            receiver.send(RESULT_OK, null);
        }

        ComponentName thisAppWidget = new ComponentName(getApplicationContext(), Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID : ids) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID, R.id.wgtGridView);
        }
    }
}
