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
            int viewIndex = intent.getIntExtra(Widget.EXTRA_ITEM, 0);
            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI, new String []
                            {StatisticTable.COLUMN_PACKAGE_NAME,
                            StatisticTable.COLUMN_ACTIVITY_NAME,
                            StatisticTable.COLUMN_USAGE},
                            StatisticTable.COLUMN_IGNORE + " = " + 0,
                            null, StatisticTable.COLUMN_USAGE + " DESC, " + StatisticTable.COLUMN_APP_NAME);

            if (c.moveToPosition(viewIndex)) {
                String activityName = c.getString(c.getColumnIndex(StatisticTable.COLUMN_ACTIVITY_NAME));
                Intent launchIntent = new Intent(Intent.ACTION_MAIN);
                launchIntent.setClassName(c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME)), activityName);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);

                int usage = c.getInt(c.getColumnIndex(StatisticTable.COLUMN_USAGE));
                ContentValues values = new ContentValues();
                usage++;
                values.put(StatisticTable.COLUMN_USAGE, usage);
                getContentResolver().update(DBContentProvider.CONTENT_URI, values, StatisticTable.COLUMN_ACTIVITY_NAME + " like " + "'" + activityName + "'", null);
            }
            c.close();
        }

        if (intent.getAction().equals(DELETE_PACKAGE_ACTION)) {
            String packageName = intent.getStringExtra(DeletePackageReceiver.PACKAGE_NAME_EXTRA);
            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI,
                    new String[] {StatisticTable.COLUMN_ACTIVITY_NAME},
                    StatisticTable.COLUMN_PACKAGE_NAME + " like " + "'" + packageName + "'",
                    null, null);
            if (c.moveToFirst()) {
                do {
                    Utils.deleteIcon(getApplicationContext(), c.getString(c.getColumnIndex(StatisticTable.COLUMN_ACTIVITY_NAME)));
                } while (c.moveToNext());
            }
            c.close();
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
