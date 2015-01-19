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
import android.os.Handler;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChoseAppsActivity extends Activity implements Runnable {

    //A ProgressDialog View
    private ProgressDialog progressDialog;
    //A thread, that will be used to execute code in parallel with the UI thread
    private Thread thread;
    //Create a Thread handler to queue code execution on a thread
    private Handler handler;

    private List<ResolveInfo> activities;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        activities = getPackageManager().queryIntentActivities(startupIntent, 0);

        //Create a new progress dialog.
        progressDialog = new ProgressDialog(ChoseAppsActivity.this);
        //Set the progress dialog to display a horizontal bar .
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //Set the dialog title to 'Loading...'.
        progressDialog.setTitle("Loading...");
        //Set the dialog message to 'Loading application View, please wait...'.
        progressDialog.setMessage("Loading application View, please wait...");
        //This dialog can't be canceled by pressing the back key.
        progressDialog.setCancelable(false);
        //This dialog isn't indeterminate.
        progressDialog.setIndeterminate(false);
        //The maximum number of progress items is 100.
        progressDialog.setMax(activities.size());
        //Set the current progress to zero.
        progressDialog.setProgress(0);
        //Display the progress dialog.
        progressDialog.show();

        //Initialize the handler
        handler = new Handler();
        //Initialize the thread
        thread = new Thread(this, "ProgressDialogThread");
        //start the thread
        thread.start();
    }

    //Initialize a counter integer to zero

    @Override
    public void run()
    {
        //Obtain the thread's token
        synchronized (thread)
        {

            ContentValues values = new ContentValues();
            Cursor c = getContentResolver().query(DBContentProvider.CONTENT_URI,
                    new String[]{StatisticTable.COLUMN_PACKAGE_NAME,
                            StatisticTable.COLUMN_APP_NAME,
                            StatisticTable.COLUMN_IGNORE}, null, null, null);
            int progress = 1;
            if (c.moveToFirst()) {
                do {
                    for (int i = 0; i < activities.size(); i++) {
                        if (!activities.get(i).activityInfo.applicationInfo.packageName.equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME))) &
                                activities.get(i).loadLabel(getPackageManager()).toString().equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_APP_NAME)))) {
                            activities.remove(i);
                        } else {
                            values.put(StatisticTable.COLUMN_PACKAGE_NAME, activities.get(i).activityInfo.applicationInfo.packageName);
                            values.put(StatisticTable.COLUMN_APP_NAME, activities.get(i).loadLabel(getPackageManager()).toString());
                            values.put(StatisticTable.COLUMN_USAGE, 0);
                            values.put(StatisticTable.COLUMN_IGNORE, 0);
                            getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                            values.clear();
                        }
                        final int finalProgress = progress;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Set the current progress.
                                progressDialog.setProgress(finalProgress);
                            }
                        });
                        progress++;
                    }
                    final int finalProgress = progress;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Set the current progress.
                            progressDialog.setProgress(finalProgress);
                        }
                    });
                    progress++;
                } while (c.moveToNext());
            } else {
                for (ResolveInfo ri : activities) {
                    values.put(StatisticTable.COLUMN_PACKAGE_NAME, ri.activityInfo.applicationInfo.packageName);
                    values.put(StatisticTable.COLUMN_APP_NAME, ri.loadLabel(getPackageManager()).toString());
                    values.put(StatisticTable.COLUMN_USAGE, 0);
                    values.put(StatisticTable.COLUMN_IGNORE, 0);
                    getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                    values.clear();
                    final int finalProgress = progress;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Set the current progress.
                            progressDialog.setProgress(finalProgress);
                        }
                    });
                    progress++;
                }
            }




            /*//While the counter is smaller than four
            while(counter <= activities.size())
            {
                //Wait 850 milliseconds
                thread.wait(850);
                //Increment the counter
                counter++;

                //update the changes to the UI thread
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //Set the current progress.
                        progressDialog.setProgress(counter);
                    }
                });
            }*/
        }

        //This works just like the onPostExecute method from the AsyncTask class
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                //Close the progress dialog
                progressDialog.dismiss();

                //Call the application's main View
                setContentView(R.layout.activity_chose_apps);
            }
        });

        //Try to "kill" the thread, by interrupting its execution
        synchronized (thread)
        {
            thread.interrupt();
        }
    }

    /*class UpdateAppsList extends AsyncTask<Cursor, Integer, Cursor> {

        private ProgressDialog progressBar;
        private Context context;
        private List<ResolveInfo> activities;

        public UpdateAppsList (Context context, List<ResolveInfo> activities) {
            this.context = context;
            this.activities = activities;
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(context);
            progressBar.setMax(activities.size());
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
        protected Cursor doInBackground(Cursor... cursors) {
            ContentValues values = new ContentValues();
            if (cursors[0].moveToFirst()) {
                do {
                    for (int i = 0; i < activities.size(); i++) {
                        if (activities.get(i).activityInfo.applicationInfo.packageName.equals(cursors[0].getString(cursors[0].getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME))) &
                                activities.get(i).loadLabel(context.getPackageManager()).toString().equals(cursors[0].getString(cursors[0].getColumnIndex(StatisticTable.COLUMN_APP_NAME)))) {
                            activities.remove(i);
                        } else {
                            values.put(StatisticTable.COLUMN_PACKAGE_NAME, activities.get(i).activityInfo.applicationInfo.packageName);
                            values.put(StatisticTable.COLUMN_APP_NAME, activities.get(i).loadLabel(context.getPackageManager()).toString());
                            values.put(StatisticTable.COLUMN_USAGE, 0);
                            values.put(StatisticTable.COLUMN_IGNORE, 0);
                            context.getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                            values.clear();
                        }
                        publishProgress(i+1);
                    }
                } while (cursors[0].moveToNext());
            } else {
                int i = 1;
                for (ResolveInfo ri : activities) {
                    values.put(StatisticTable.COLUMN_PACKAGE_NAME, ri.activityInfo.applicationInfo.packageName);
                    values.put(StatisticTable.COLUMN_APP_NAME, ri.loadLabel(context.getPackageManager()).toString());
                    values.put(StatisticTable.COLUMN_USAGE, 0);
                    values.put(StatisticTable.COLUMN_IGNORE, 0);
                    context.getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                    values.clear();
                    publishProgress(i);
                    i++;
                }
            }
            return context.getContentResolver().query(DBContentProvider.CONTENT_URI, new String[] {
                            StatisticTable.COLUMN_ID,
                            StatisticTable.COLUMN_PACKAGE_NAME,
                            StatisticTable.COLUMN_APP_NAME,
                            StatisticTable.COLUMN_IGNORE},
                            null, null, StatisticTable.COLUMN_APP_NAME);
        }
    }*/

    /*private class UpdateAppsList extends AsyncTask <Void, Integer, Cursor> {
        private ProgressDialog dialog;
        private Cursor c;
        private Context activity;

        public UpdateAppsList(ChoseAppsActivity activity) {
            dialog = new ProgressDialog(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle(R.string.updating_apps_list);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            setContentView(R.layout.activity_chose_apps);
            ListView mListView = (ListView) findViewById(R.id.choseAppsListView);
            *//*try {
                c1 = updateAppsList.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*//*
            ListAdapter listAdapter = new ListViewAdapter(activity, c, 0);
            mListView.setAdapter(listAdapter);
            if (c != null) {
                c.close();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.setProgress(values[0]);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Intent startupIntent = new Intent(Intent.ACTION_MAIN);
            startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> activities = getPackageManager().queryIntentActivities(startupIntent, 0);
            dialog.setMax(activities.size());
            ContentValues values = new ContentValues();
            c = getContentResolver().query(DBContentProvider.CONTENT_URI,
                    new String[]{StatisticTable.COLUMN_PACKAGE_NAME,
                            StatisticTable.COLUMN_APP_NAME,
                            StatisticTable.COLUMN_IGNORE}, null, null, null);
            int progress = 1;
            if (c.moveToFirst()) {
                do {
                    for (int i = 0; i < activities.size(); i++) {
                        if (activities.get(i).activityInfo.applicationInfo.packageName.equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_PACKAGE_NAME))) &
                                activities.get(i).loadLabel(getPackageManager()).toString().equals(c.getString(c.getColumnIndex(StatisticTable.COLUMN_APP_NAME)))) {
                            activities.remove(i);
                        } else {
                            values.put(StatisticTable.COLUMN_PACKAGE_NAME, activities.get(i).activityInfo.applicationInfo.packageName);
                            values.put(StatisticTable.COLUMN_APP_NAME, activities.get(i).loadLabel(getPackageManager()).toString());
                            values.put(StatisticTable.COLUMN_USAGE, 0);
                            values.put(StatisticTable.COLUMN_IGNORE, 0);
                            getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                            values.clear();
                        }
                        publishProgress(progress);
                        progress++;
                    }
                    publishProgress(progress);
                    progress++;
                } while (c.moveToNext());
            } else {
                for (ResolveInfo ri : activities) {
                    values.put(StatisticTable.COLUMN_PACKAGE_NAME, ri.activityInfo.applicationInfo.packageName);
                    values.put(StatisticTable.COLUMN_APP_NAME, ri.loadLabel(getPackageManager()).toString());
                    values.put(StatisticTable.COLUMN_USAGE, 0);
                    values.put(StatisticTable.COLUMN_IGNORE, 0);
                    getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
                    values.clear();
                    publishProgress(progress);
                    progress++;
                }
            }
            return getContentResolver().query(DBContentProvider.CONTENT_URI, new String[] {
                            StatisticTable.COLUMN_ID,
                            StatisticTable.COLUMN_PACKAGE_NAME,
                            StatisticTable.COLUMN_APP_NAME,
                            StatisticTable.COLUMN_IGNORE},
                    null, null, StatisticTable.COLUMN_APP_NAME);
        }

    }*/
}
