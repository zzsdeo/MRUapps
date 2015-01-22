package ru.zzsdeo.mruapps;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DBUpdateIntentService extends IntentService {

    private static final String DB_UPDATE_INTENT_SERVICE_NAME = "DBUpdateIntentService";
    public static final String LAUNCH_ACTION = "launch_action";
    public static final String DELETE_PACKAGE_ACTION = "delete_package_action";
    public static final String UPDATE_PACKAGE_ACTION = "update_package_action";


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

        if (intent.getAction().equals(UPDATE_PACKAGE_ACTION)) {
            String packageName = intent.getStringExtra(DeletePackageReceiver.PACKAGE_NAME_EXTRA);
            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI,
                    new String[] {StatisticTable.COLUMN_ACTIVITY_NAME, StatisticTable.COLUMN_IGNORE},
                    StatisticTable.COLUMN_PACKAGE_NAME + " like " + "'" + packageName + "'",
                    null, null);
            if (c.moveToFirst()) {
                do {
                    Utils.deleteIcon(getApplicationContext(), c.getString(c.getColumnIndex(StatisticTable.COLUMN_ACTIVITY_NAME)));
                } while (c.moveToNext());
            }

            Intent startupIntent = new Intent(Intent.ACTION_MAIN);
            startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> activities = getPackageManager().queryIntentActivities(startupIntent, 0);

            List<ResolveInfo> updatedActivities = new ArrayList<ResolveInfo>();

            int ignore = 1;
            if (c.moveToFirst()) {
                do {
                    if (c.getInt(c.getColumnIndex(StatisticTable.COLUMN_IGNORE)) == 0) {
                        ignore = 0;
                    }
                    for (int i = 0; i < activities.size(); i++) {
                        if (c.getString(c.getColumnIndex(StatisticTable.COLUMN_ACTIVITY_NAME)).equals(activities.get(i).activityInfo.name)) {
                            updatedActivities.add(activities.get(i));
                            activities.remove(i);
                            if (updatedActivities.size() == c.getCount()) {
                                for (ResolveInfo ri : updatedActivities) {
                                    Utils.createIcon(getApplicationContext(), ri);
                                }
                                break;
                            }
                        }
                    }
                } while (c.moveToNext() & updatedActivities.size() != c.getCount());
            }

            if (updatedActivities.size() != c.getCount()) {
                getContentResolver().delete(DBContentProvider.CONTENT_URI, StatisticTable.COLUMN_PACKAGE_NAME + " like " + "'" + packageName + "'", null);
                ContentValues values = new ContentValues();
                for (ResolveInfo ri : updatedActivities) {
                    values.put(StatisticTable.COLUMN_PACKAGE_NAME, ri.activityInfo.applicationInfo.packageName);
                    values.put(StatisticTable.COLUMN_ACTIVITY_NAME, ri.activityInfo.name);
                    values.put(StatisticTable.COLUMN_APP_NAME, ri.loadLabel(getPackageManager()).toString());
                    values.put(StatisticTable.COLUMN_USAGE, 0);
                    values.put(StatisticTable.COLUMN_IGNORE, ignore);
                    getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                    values.clear();
                    Utils.createIcon(getApplicationContext(), ri);
                }
            }
            c.close();
        }

        ComponentName thisAppWidget = new ComponentName(getApplicationContext(), Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID : ids) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID, R.id.wgtGridView);
        }
    }
}
