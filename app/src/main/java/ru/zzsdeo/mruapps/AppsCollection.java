package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppsCollection {

    private Context mContext;
    private PackageManager pm;
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;
    private LruCache<String, Bitmap> mMemoryCache;
    private Set<String> inProgressSet = Collections.synchronizedSet(new HashSet<String>());


    public AppsCollection (Context context) {
        mContext = context;
        pm = context.getPackageManager();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
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

    public List<ResolveInfo> getIgnoredApps () {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Cursor c = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI, new String[]{StatisticTable.COLUMN_PACKAGE_NAME}, StatisticTable.COLUMN_IGNORE + " = " + 1, null, null);
        List<ResolveInfo> ignoredActivities = new ArrayList<ResolveInfo>();

        if (c.moveToFirst()) {
            do {
                for (int i = 0; i < activities.size(); i++) {
                    if (activities.get(i).activityInfo.applicationInfo.packageName.equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME)))) {
                        ignoredActivities.add(activities.get(i));
                    }
                }
            } while (c.moveToNext());
        }
        c.close();

        Collections.sort(ignoredActivities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        resolveInfo.loadLabel(pm).toString(),
                        resolveInfo2.loadLabel(pm).toString());
            }
        });

        return ignoredActivities;
    }

    public List<AppsNamesAndIcons> getAppsNamesAndIcons () {
        List<ResolveInfo> mruApps = getMRUapps();
        List<AppsNamesAndIcons> listAppsNamesAndIcons = new ArrayList<AppsNamesAndIcons>();
        for (ResolveInfo ri : mruApps) {
            AppsNamesAndIcons appsNamesAndIcons = new AppsNamesAndIcons();
            appsNamesAndIcons.setName(ri.loadLabel(pm).toString());
            appsNamesAndIcons.setIcon(Utils.convertToBitmap(ri.loadIcon(pm), ICON_WIDTH, ICON_HEIGHT));
            listAppsNamesAndIcons.add(appsNamesAndIcons);
        }
        return listAppsNamesAndIcons;
    }

    public List<AppsNamesAndIcons> getIgnoredAppsNamesAndIcons () {
        List<ResolveInfo> ignoredApps = getIgnoredApps();
        List<AppsNamesAndIcons> listAppsNamesAndIcons = new ArrayList<AppsNamesAndIcons>();
        for (ResolveInfo ri : ignoredApps) {
            AppsNamesAndIcons appsNamesAndIcons = new AppsNamesAndIcons();
            String name = ri.loadLabel(pm).toString();
            appsNamesAndIcons.setName(name);
            Bitmap icon = getBitmapFromMemCache(name);
            if (icon != null) {
                appsNamesAndIcons.setIcon(icon);
            } else {
                new BitmapWorkerTask(name, appsNamesAndIcons, ri).execute();
            }
            listAppsNamesAndIcons.add(appsNamesAndIcons);
        }
        return listAppsNamesAndIcons;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {

        private String name;
        private AppsNamesAndIcons appsNamesAndIcons;
        private ResolveInfo ri;

        BitmapWorkerTask(String name, AppsNamesAndIcons appsNamesAndIcons, ResolveInfo ri) {
            this.name = name;
            this.appsNamesAndIcons = appsNamesAndIcons;
            this.ri = ri;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (inProgressSet.contains(name)) {
                return null;
            }
            Bitmap fromMemoryCache = getBitmapFromMemCache(name);
            if (fromMemoryCache != null) {
                return fromMemoryCache;
            }
            inProgressSet.add(name);
            return Utils.convertToBitmap(ri.loadIcon(pm), ICON_WIDTH, ICON_HEIGHT);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) return;
            inProgressSet.remove(name);
            addBitmapToMemoryCache(name, bitmap);
            appsNamesAndIcons.setIcon(bitmap);
        }
    }
}