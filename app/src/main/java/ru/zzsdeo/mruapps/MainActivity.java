package ru.zzsdeo.mruapps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.LruCache;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    public static String MRUAPPS_PACKAGE_NAME;
    private AppsCollection apps;
    public static final String PARCELABLE_EXTRA = "parcelable_extra";
    private GridView gView;
    private List<ResolveInfo> mruApps;
    private List<AppsNamesAndIcons> mruAppsNamesAndIcons;
    public static final String RECEIVER_EXTRA = "receiver_extra";
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onStart() {
        super.onStart();
        refreshGridView();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ignoredItem:
                startActivity(new Intent(getApplicationContext(), IgnoredAppsActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        MRUAPPS_PACKAGE_NAME = getPackageName();

        apps = new AppsCollection(getApplicationContext());
        mruApps = apps.getMRUapps();
        mruAppsNamesAndIcons = apps.getAppsNamesAndIcons();
        gView = fillData(mruAppsNamesAndIcons);
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResolveInfo resolveInfo = (ResolveInfo) adapterView.getAdapter().getItem(i);
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (activityInfo == null) return;
                startActivityAndUpdateDB(activityInfo, getApplicationContext());
            }
        });
        gView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        gView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.setTitle(getString(R.string.selected) + " " + gView.getCheckedItemCount());
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.itemIgnore:
                        long[] id = gView.getCheckedItemIds();
                        List<ResolveInfo> ignoredApps = new ArrayList<ResolveInfo>();
                        if (id.length != 0) {
                            for (long l : id) {
                                ignoredApps.add(mruApps.get((int) l));
                            }
                        }
                        Intent serviceIntent = new Intent(getApplicationContext(), DBUpdateIntentService.class);
                        RefreshGridViewReceiver refreshGridViewReceiver = new RefreshGridViewReceiver(new Handler());
                        serviceIntent.setAction(DBUpdateIntentService.IGNORE_ACTION);
                        serviceIntent.putParcelableArrayListExtra(PARCELABLE_EXTRA, (ArrayList<ResolveInfo>) ignoredApps);
                        serviceIntent.putExtra(RECEIVER_EXTRA, refreshGridViewReceiver);
                        startService(serviceIntent);
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
    }

    class RefreshGridViewReceiver extends ResultReceiver {

        public RefreshGridViewReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            refreshGridView();
        }
    }

    private GridView fillData (List<AppsNamesAndIcons> appsNamesAndIcons) {
        GridView gv = (GridView) findViewById(R.id.gridView);
        AppsGridViewAdapter adapter = new AppsGridViewAdapter(this, R.layout.grid_item, appsNamesAndIcons);
        gv.setAdapter(adapter);
        return gv;
    }

    private void refreshGridView () {
        mruApps = apps.getMRUapps();
        mruAppsNamesAndIcons = apps.getAppsNamesAndIcons();
        fillData(mruAppsNamesAndIcons);
    }

    public void startActivityAndUpdateDB (ActivityInfo activityInfo, Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        Intent serviceIntent = new Intent(context, DBUpdateIntentService.class);
        serviceIntent.setAction(DBUpdateIntentService.LAUNCH_ACTION);
        serviceIntent.putExtra(PARCELABLE_EXTRA, activityInfo);
        context.startService(serviceIntent);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
