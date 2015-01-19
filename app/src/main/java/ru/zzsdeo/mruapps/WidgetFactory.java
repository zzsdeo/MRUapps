package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<ResolveInfo> mMRUapps;
    //private int mAppWidgetId;

    public WidgetFactory (Context context, Intent intent) {
        mContext = context;
        //mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        //mMRUapps = apps.getMRUapps();
    }

    @Override
    public void onDataSetChanged() {
        mMRUapps.clear();
        //mMRUapps.addAll(apps.getMRUapps());
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
        //RemoteViews rView = new RemoteViews(mContext.getPackageName(), R.layout.grid_item);
        //rView.setTextViewText(R.id.name, mMRUapps.get(i).loadLabel(mContext.getPackageManager()));
        //rView.setImageViewBitmap(R.id.icon, Utils.convertToBitmap(mMRUapps.get(i).loadIcon(mContext.getPackageManager()), Utils.ICON_WIDTH, Utils.ICON_HEIGHT));

        Bundle extras = new Bundle();
        extras.putInt(Widget.EXTRA_ITEM, i);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        //rView.setOnClickFillInIntent(R.id.gridItemId, fillInIntent);

        //return rView;
        return  null;
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
