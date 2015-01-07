package ru.zzsdeo.mruapps;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ResolveInfo;

import java.util.List;

public class MRUappsLoader extends AsyncTaskLoader<List<ResolveInfo>> {

    AppsCollection apps;

    public MRUappsLoader(Context context) {
        super(context);
        apps = new AppsCollection(context);
    }

    @Override
    public List<ResolveInfo> loadInBackground() {
        return apps.getMRUapps();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
