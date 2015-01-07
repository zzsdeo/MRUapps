package ru.zzsdeo.mruapps;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks <List<ResolveInfo>> {

    /*public static String MRUAPPS_PACKAGE_NAME;
    AppsCollection apps;
    public static final String PARCELABLE_EXTRA = "parcelable_extra";

    @Override
    protected void onStart() {
        super.onStart();
        fillData(apps.getMRUapps());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MRUAPPS_PACKAGE_NAME = getPackageName();

        apps = new AppsCollection(getApplicationContext());
        fillData(apps.getMRUapps()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResolveInfo resolveInfo = (ResolveInfo) adapterView.getAdapter().getItem(i);
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (activityInfo == null) return;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Intent serviceIntent = new Intent(getApplicationContext(), DBUpdateIntentService.class);
                serviceIntent.putExtra(PARCELABLE_EXTRA, activityInfo);
                startService(serviceIntent);
            }
        });
    }

    private GridView fillData (List<ResolveInfo> MRUActivities) {
        GridView gv = (GridView) findViewById(R.id.gridView);
        AppsGridViewAdapter adapter = new AppsGridViewAdapter(this, R.layout.grid_item, MRUActivities);
        gv.setAdapter(adapter);
        return gv;
    }*/

    public static String MRUAPPS_PACKAGE_NAME;
    AppsCollection apps;
    public static final String PARCELABLE_EXTRA = "parcelable_extra";
    AppsGridViewAdapter adapter;
    GridView gv;
    Loader<List<ResolveInfo>> mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MRUAPPS_PACKAGE_NAME = getPackageName();

        apps = new AppsCollection(getApplicationContext());

        gv = (GridView) findViewById(R.id.gridView);
        adapter = new AppsGridViewAdapter(getApplicationContext(), R.layout.grid_item);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResolveInfo resolveInfo = (ResolveInfo) adapterView.getAdapter().getItem(i);
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (activityInfo == null) return;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Intent serviceIntent = new Intent(getApplicationContext(), DBUpdateIntentService.class);
                serviceIntent.putExtra(PARCELABLE_EXTRA, activityInfo);
                startService(serviceIntent);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<ResolveInfo>> onCreateLoader(int i, Bundle bundle) {
        return new MRUappsLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<List<ResolveInfo>> listLoader, List<ResolveInfo> resolveInfos) {
        adapter.setData(resolveInfos);
    }

    @Override
    public void onLoaderReset(Loader<List<ResolveInfo>> listLoader) {
        adapter.setData(null);
    }
}
