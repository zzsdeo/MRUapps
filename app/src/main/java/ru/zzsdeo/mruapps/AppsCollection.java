package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsCollection {

    private Context mContext;
    private PackageManager pm;

    public AppsCollection (Context context) {
        mContext = context;
        pm = context.getPackageManager();
    }

    public List<ResolveInfo> getMRUapps () {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Cursor c = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI,
                new String[]{StatisticTable.COLUMN_PACKAGE_NAME,
                StatisticTable.COLUMN_APP_NAME},
                StatisticTable.COLUMN_IGNORE + " = " + 0,
                null, null);
        List<ResolveInfo> MRUActivities = new ArrayList<ResolveInfo>();
        if (c.moveToFirst()) {
            do {
                for (int i = 0; i < activities.size(); i++) {
                    if (activities.get(i).activityInfo.applicationInfo.packageName.equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME))) &
                            activities.get(i).loadLabel(pm).toString().equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_APP_NAME)))) {
                        MRUActivities.add(activities.get(i));
                        activities.remove(i);
                    }
                }
            } while (c.moveToNext());
        }
        c.close();

        Collections.sort(MRUActivities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
                int k = 0;
                Cursor c = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI,
                        new String[]{StatisticTable.COLUMN_USAGE},
                        StatisticTable.COLUMN_PACKAGE_NAME + " like " + "'" + resolveInfo.activityInfo.applicationInfo.packageName + "'" +
                                " and " + StatisticTable.COLUMN_APP_NAME + " like " + "'" + resolveInfo.loadLabel(pm).toString() + "'",
                        null, StatisticTable.COLUMN_USAGE + " DESC");
                Cursor c2 = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI,
                        new String[]{StatisticTable.COLUMN_USAGE},
                        StatisticTable.COLUMN_PACKAGE_NAME + " like " + "'" + resolveInfo2.activityInfo.applicationInfo.packageName + "'" +
                                " and " + StatisticTable.COLUMN_APP_NAME + " like " + "'" + resolveInfo2.loadLabel(pm).toString() + "'",
                        null, StatisticTable.COLUMN_USAGE + " DESC");
                if (c.moveToFirst() & c2.moveToFirst()) {
                    if (c.getInt(c.getColumnIndex(StatisticTable.COLUMN_USAGE)) < c2.getInt(c2.getColumnIndex(StatisticTable.COLUMN_USAGE))) {
                        k = 1;
                    } else if (c.getInt(c.getColumnIndex(StatisticTable.COLUMN_USAGE)) > c2.getInt(c2.getColumnIndex(StatisticTable.COLUMN_USAGE))) {
                        k = -1;
                    } else {
                        k = 0;
                    }
                }
                c.close();
                c2.close();
                return k;
            }
        });
        return MRUActivities;
    }
}
