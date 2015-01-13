package ru.zzsdeo.mruapps;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class IgnoredAppsActivity extends Activity {

    private AppsCollection apps;
    public static final String IGNORED_PARCELABLE_EXTRA = "ignored_parcelable_extra";
    private GridView gView;
    private List<ResolveInfo> ignoredApps;
    public static final String IGNORED_RECEIVER_EXTRA = "ignored_receiver_extra";

    @Override
    protected void onStart() {
        super.onStart();
        ignoredApps = apps.getIgnoredApps();
        fillData(ignoredApps);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        apps = new AppsCollection(getApplicationContext());
        ignoredApps = apps.getIgnoredApps();
        gView = fillData(ignoredApps);
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
                        List<ResolveInfo> selectedApps = new ArrayList<ResolveInfo>();
                        if (id.length != 0) {
                            for (long l : id) {
                                selectedApps.add(ignoredApps.get((int) l));
                            }
                        }
                        Intent serviceIntent = new Intent(getApplicationContext(), DBUpdateIntentService.class);
                        RefreshGridViewReceiver refreshGridViewReceiver = new RefreshGridViewReceiver(new Handler());
                        serviceIntent.setAction(DBUpdateIntentService.REMOVE_FROM_IGNORE_ACTION);
                        serviceIntent.putParcelableArrayListExtra(IGNORED_PARCELABLE_EXTRA, (ArrayList<ResolveInfo>) selectedApps);
                        serviceIntent.putExtra(IGNORED_RECEIVER_EXTRA, refreshGridViewReceiver);
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
            ignoredApps = apps.getIgnoredApps();
            fillData(ignoredApps);
        }
    }

    private GridView fillData (List<ResolveInfo> activities) {
        GridView gv = (GridView) findViewById(R.id.gridView);
        AppsGridViewAdapter adapter = new AppsGridViewAdapter(this, R.layout.grid_item, activities);
        gv.setAdapter(adapter);
        return gv;
    }
}
