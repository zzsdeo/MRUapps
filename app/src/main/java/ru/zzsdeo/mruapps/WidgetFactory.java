package ru.zzsdeo.mruapps;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<ResolveInfo> mMRUapps;
    private AppsCollection apps;
    //private int mAppWidgetId;

    public WidgetFactory (Context context, Intent intent) {
        mContext = context;
        apps = new AppsCollection(context);
        //mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mMRUapps = apps.getMRUapps();
    }

    @Override
    public void onDataSetChanged() {
        mMRUapps.clear();
        mMRUapps.addAll(apps.getMRUapps());
    }

    @Override
    public void onDestroy() {
        mMRUapps.clear();
    }

    @Override
    public int getCount() {
        return mMRUapps.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rView = new RemoteViews(mContext.getPackageName(), R.layout.grid_item);
        rView.setTextViewText(R.id.name, mMRUapps.get(i).loadLabel(mContext.getPackageManager()));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String path = new File(mContext.getCacheDir(), mMRUapps.get(i).activityInfo.applicationInfo.packageName).getAbsolutePath();
        Bitmap icon = BitmapFactory.decodeFile(path, options);
        if (icon == null) {
            icon = Utils.convertToBitmap(mMRUapps.get(i).loadIcon(mContext.getPackageManager()), Utils.ICON_WIDTH, Utils.ICON_HEIGHT);
        }
        rView.setImageViewBitmap(R.id.icon, icon);

        Bundle extras = new Bundle();
        extras.putInt(Widget.EXTRA_ITEM, i);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rView.setOnClickFillInIntent(R.id.gridItemId, fillInIntent);

        return rView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
