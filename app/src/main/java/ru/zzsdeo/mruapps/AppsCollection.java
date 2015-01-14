package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public void createCache () {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        /*try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "ru.zzsdeo.mymoneybalance"
                        + "//databases//" + "myDB";
                String backupDBPath = "/MyMoneyBalance/database/myDB";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "База данных успешно экспортирована", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (final Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Ошибка!\n" + e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }*/



        String file_path = Environment.getDataDirectory().getAbsolutePath() +
                "/icon_cache";
        File dir = new File(file_path);
        String filename = activities.get(1).activityInfo.applicationInfo.packageName;
        Bitmap bitmap = Utils.convertToBitmap(activities.get(1).loadIcon(pm), 100, 100);

        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, filename+".png");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        try {
            if (fOut != null) {
                fOut.flush();
                fOut.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
