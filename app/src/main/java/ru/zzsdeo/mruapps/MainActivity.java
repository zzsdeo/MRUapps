package ru.zzsdeo.mruapps;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    Cursor c;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null) {
            c.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gv = (GridView) findViewById(R.id.gridView);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        AppsGridViewAdapter adapter = new AppsGridViewAdapter(this, R.layout.grid_item, activities);

        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResolveInfo resolveInfo = (ResolveInfo) adapterView.getAdapter().getItem(i);
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (activityInfo == null) return;

                c = getContentResolver().query(DBContentProvider.CONTENT_URI, new String[] {StatisticTable.COLUMN_USAGE}, StatisticTable.COLUMN_PACKAGE_NAME + " like '" + activityInfo.applicationInfo.packageName + "'", null, null);
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

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
