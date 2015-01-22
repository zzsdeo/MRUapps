package ru.zzsdeo.mruapps;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

public class ChoseAppsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListViewAdapter adapter;

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
    protected void onStop() {
        super.onStop();

        ComponentName thisAppWidget = new ComponentName(getApplicationContext(), Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID : ids) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID, R.id.wgtGridView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        new UpdateAppsListAsyncTask(this).execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, DBContentProvider.CONTENT_URI, new String[] {
                StatisticTable.COLUMN_ID,
                StatisticTable.COLUMN_APP_NAME,
                StatisticTable.COLUMN_IGNORE},
                null, null, StatisticTable.COLUMN_APP_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    private class UpdateAppsListAsyncTask extends AsyncTask<Void, Integer, Void> {

        private ProgressDialog progressBar;
        private Context context;

        public UpdateAppsListAsyncTask (Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(context);
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setTitle(R.string.loading);
            progressBar.setMessage(getString(R.string.updating_apps_list));
            progressBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... option) {
            progressBar.setProgress(option[0]);
        }

        @Override
        protected void onPostExecute(Void unused) {
            progressBar.dismiss();
            getLoaderManager().initLoader(0, null, ChoseAppsActivity.this);
            setContentView(R.layout.activity_chose_apps);
            ListView listView = (ListView) findViewById(R.id.choseAppsListView);
            adapter = new ListViewAdapter(context, null, 0);
            listView.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... unused) {
            Intent startupIntent = new Intent(Intent.ACTION_MAIN);
            startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> activities = getPackageManager().queryIntentActivities(startupIntent, 0);
            progressBar.setMax(activities.size());
            ContentValues values = new ContentValues();

            int progress = 1;
            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI,
                    new String[]{StatisticTable.COLUMN_PACKAGE_NAME,
                            StatisticTable.COLUMN_APP_NAME,
                            StatisticTable.COLUMN_IGNORE}, null, null, null);
            if (c.moveToFirst()) {
                do {
                    for (int i = 0; i < activities.size(); i++) {
                        if (activities.get(i).activityInfo.applicationInfo.packageName.equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME))) &
                                activities.get(i).loadLabel(getPackageManager()).toString().equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_APP_NAME)))) {
                            activities.remove(i);
                            publishProgress(progress);
                            progress++;
                        }
                    }
                    publishProgress(progress);
                    progress++;
                } while (c.moveToNext());
                c.close();
            }
            for (ResolveInfo ri : activities) {
                values.put(StatisticTable.COLUMN_PACKAGE_NAME, ri.activityInfo.applicationInfo.packageName);
                values.put(StatisticTable.COLUMN_ACTIVITY_NAME, ri.activityInfo.name);
                values.put(StatisticTable.COLUMN_APP_NAME, ri.loadLabel(getPackageManager()).toString());
                values.put(StatisticTable.COLUMN_USAGE, 0);
                values.put(StatisticTable.COLUMN_IGNORE, 1);
                getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                values.clear();
                publishProgress(progress);
                progress++;
            }
            return null;
        }
    }
}