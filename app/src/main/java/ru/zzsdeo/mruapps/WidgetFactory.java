package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    /*private Context mContext;
    private List<ResolveInfo> mMRUapps;
    private AppsCollection apps;
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;
    private Set<String> inProgressSet = Collections.synchronizedSet(new HashSet<String>());

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
        String pkg = mMRUapps.get(i).activityInfo.applicationInfo.packageName;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String path = new File(mContext.getFilesDir() + File.separator + Utils.ICON_CACHE_SUBDIR, pkg).getAbsolutePath();
        Bitmap icon = BitmapFactory.decodeFile(path, options);
        if (icon == null) {
            icon = Utils.createCachedIcon(mContext, mMRUapps.get(i));
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
    }*/

    ////////////////////////////////ASYNKNASK///////////////////////////////////
    /*private Context mContext;
    private List<ResolveInfo> mMRUapps;
    private AppsCollection apps;
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;
    private Set<String> inProgressSet = Collections.synchronizedSet(new HashSet<String>());

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
        String pkg = mMRUapps.get(i).activityInfo.applicationInfo.packageName;
        new ImageFetcher(pkg, rView, mMRUapps.get(i)).execute();

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

    class ImageFetcher extends AsyncTask<Void, Void, Bitmap> {

        private String imageName;
        private RemoteViews imageView;
        private ResolveInfo ri;

        ImageFetcher(String imageName, RemoteViews imageView, ResolveInfo ri) {
            this.imageName = imageName;
            this.imageView = imageView;
            this.ri = ri;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (inProgressSet.contains(imageName)) {
                return null;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            String path = new File(mContext.getFilesDir() + File.separator + Utils.ICON_CACHE_SUBDIR, imageName).getAbsolutePath();
            Bitmap fromMemoryCache = BitmapFactory.decodeFile(path, options);
            if (fromMemoryCache != null) {
                return fromMemoryCache;
            }
            inProgressSet.add(imageName);
            return Utils.convertToBitmap(ri.loadIcon(mContext.getPackageManager()), ICON_WIDTH, ICON_HEIGHT);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) return;
            inProgressSet.remove(imageName);
            Utils.createCachedIcon(mContext, ri);
            imageView.setImageViewBitmap(R.id.icon, bitmap);
        }
    }*/

    ////////////////////////////////////LRU//////////////////////////////////////////////

    private Context mContext;
    private List<ResolveInfo> mMRUapps;
    private AppsCollection apps;
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;
    private BitmapLruCache mBitmapLruCache;

    //private int mAppWidgetId;

    public WidgetFactory (Context context, Intent intent) {
        mContext = context;
        apps = new AppsCollection(context);
        mBitmapLruCache = new BitmapLruCache();
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
        String pkg = mMRUapps.get(i).activityInfo.applicationInfo.packageName;
        Bitmap icon = mBitmapLruCache.getBitmapFromMemCache(pkg);
        if (icon == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            String path = new File(mContext.getFilesDir() + File.separator + Utils.ICON_CACHE_SUBDIR, pkg).getAbsolutePath();
            icon = BitmapFactory.decodeFile(path, options);
            if (icon == null) {
                icon = Utils.createCachedIcon(mContext, mMRUapps.get(i));
            }
            mBitmapLruCache.addBitmapToMemoryCache(pkg, icon);
        }
        rView.setImageViewBitmap(R.id.icon, icon);
        /*String pkg = mMRUapps.get(i).activityInfo.applicationInfo.packageName;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String path = new File(mContext.getFilesDir() + File.separator + Utils.ICON_CACHE_SUBDIR, pkg).getAbsolutePath();
        Bitmap icon = BitmapFactory.decodeFile(path, options);
        if (icon == null) {
            icon = Utils.createCachedIcon(mContext, mMRUapps.get(i));
        }
        rView.setImageViewBitmap(R.id.icon, icon);*/

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
