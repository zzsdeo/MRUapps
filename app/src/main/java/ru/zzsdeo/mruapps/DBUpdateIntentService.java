package ru.zzsdeo.mruapps;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;

public class DBUpdateIntentService extends IntentService {

    private static final String DB_UPDATE_INTENT_SERVICE_NAME = "DBUpdateIntentService";
    public static final String LAUNCH_ACTION = "launch_action";
    public static final String DELETE_PACKAGE_ACTION = "delete_package_action";

    public DBUpdateIntentService () {
        super(DB_UPDATE_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.getAction().equals(LAUNCH_ACTION)) {
            ResolveInfo appInfo = intent.getParcelableExtra(LaunchAppIntentService.PARCELABLE_EXTRA);

            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI, new String[]{StatisticTable.COLUMN_USAGE,
                    StatisticTable.COLUMN_ID},
                    StatisticTable.COLUMN_PACKAGE_NAME + " like " + "'" + appInfo.activityInfo.applicationInfo.packageName + "'" +
                    " and " + StatisticTable.COLUMN_APP_NAME + " like " + "'" + appInfo.loadLabel(getPackageManager()).toString() + "'",
                    null, null);
            ContentValues values = new ContentValues();
            if (c.moveToFirst()) {
                int usage = c.getInt(c.getColumnIndex(StatisticTable.COLUMN_USAGE));
                long id = c.getLong(c.getColumnIndex(StatisticTable.COLUMN_ID));
                usage++;
                values.put(StatisticTable.COLUMN_USAGE, usage);
                getContentResolver().update(DBContentProvider.CONTENT_URI, values, StatisticTable.COLUMN_ID + " = " + id, null);
            }
            c.close();
        }

        if (intent.getAction().equals(DELETE_PACKAGE_ACTION)) {
            String packageName = intent.getStringExtra(DeletePackageReceiver.PACKAGE_NAME_EXTRA);
            getContentResolver().delete(DBContentProvider.CONTENT_URI, StatisticTable.COLUMN_PACKAGE_NAME + " like " + "'" + packageName + "'", null);
        }

        ComponentName thisAppWidget = new ComponentName(getApplicationContext(), Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID : ids) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID, R.id.wgtGridView);
        }
    }
}
