package ru.zzsdeo.mruapps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

public class ChoseAppsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_apps);


    }

    class UpdateAppsList extends AsyncTask<Void, Integer, Cursor> {

        private ProgressDialog progressBar;
        private Context context;

        public UpdateAppsList (Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(context);
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setTitle(R.string.updating_apps_list);
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            progressBar.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Intent startupIntent = new Intent(Intent.ACTION_MAIN);
            startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> activities = getPackageManager().queryIntentActivities(startupIntent, 0);
            progressBar.setMax(activities.size());
            ContentValues values = new ContentValues();
            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI,
                    new String[]{StatisticTable.COLUMN_PACKAGE_NAME,
                            StatisticTable.COLUMN_APP_NAME,
                            StatisticTable.COLUMN_IGNORE}, null, null, null);
            if (c.moveToFirst()) {
                do {
                    for (int i = 0; i < activities.size(); i++) {
                        if (activities.get(i).activityInfo.applicationInfo.packageName)
                        publishProgress(i+1);
                    }
                } while (c.moveToNext());
            } else {
                int i = 1;
                for (ResolveInfo ri : activities) {
                    values.put(StatisticTable.COLUMN_PACKAGE_NAME, ri.activityInfo.applicationInfo.packageName);
                    values.put(StatisticTable.COLUMN_APP_NAME, ri.loadLabel(getPackageManager()).toString());
                    values.put(StatisticTable.COLUMN_USAGE, 0);
                    values.put(StatisticTable.COLUMN_IGNORE, 0);
                    getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                    values.clear();
                    publishProgress(i);
                    i++;
                }
            }
            return null;
        }
    }
}
