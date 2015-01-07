package ru.zzsdeo.mruapps;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;
    int mAppWidgetId;

    public WidgetFactory (Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        //mCursor = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI, new String[] {StatisticTable.COLUMN_ID, StatisticTable.COLUMN_APP_NAME, StatisticTable.COLUMN_APP_ICON}, null, null, StatisticTable.COLUMN_USAGE + " DESC");
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (mCursor.moveToPosition(i)) {
            RemoteViews rView = new RemoteViews(mContext.getPackageName(), R.layout.grid_item);
            //rView.setTextViewText(R.id.name, mCursor.getString(mCursor.getColumnIndex(StatisticTable.COLUMN_APP_NAME)));
            //rView.setImageViewResource(R.id.icon, mCursor.getInt(mCursor.getColumnIndex(StatisticTable.COLUMN_APP_ICON)));
            return rView;
        } else {
            return null;
        }
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
        if (mCursor.moveToPosition(i)) {
            return mCursor.getLong(mCursor.getColumnIndex(StatisticTable.COLUMN_ID));
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
