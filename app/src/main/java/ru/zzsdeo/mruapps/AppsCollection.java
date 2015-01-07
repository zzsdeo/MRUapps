package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsCollection {

    Context mContext;
    PackageManager pm;

    public AppsCollection (Context context) {
        mContext = context;
        pm = context.getPackageManager();
    }

    public List<ResolveInfo> getMRUapps () {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Cursor c = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI, new String[]{StatisticTable.COLUMN_PACKAGE_NAME, StatisticTable.COLUMN_IGNORE}, null, null, StatisticTable.COLUMN_USAGE + " DESC");
        List<ResolveInfo> MRUActivities = new ArrayList<ResolveInfo>();

        if (c.moveToFirst()) {
            do {
                for (int i = 0; i < activities.size(); i++) {
                    if (activities.get(i).activityInfo.applicationInfo.packageName.equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME)))) {
                        if (c.getInt(c.getColumnIndex(StatisticTable.COLUMN_IGNORE)) == 0) {
                            MRUActivities.add(activities.get(i));
                        }
                        activities.remove(i);
                    }
                }
            } while (c.moveToNext());
        }
        c.close();

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        resolveInfo.loadLabel(pm).toString(),
                        resolveInfo2.loadLabel(pm).toString());
            }
        });

        MRUActivities.addAll(activities);
        return MRUActivities;
    }
}