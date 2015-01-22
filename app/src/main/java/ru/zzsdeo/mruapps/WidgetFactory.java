package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;
    //private int mAppWidgetId;

    public WidgetFactory (Context context, Intent intent) {
        mContext = context;
        //mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mCursor = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI, new String []
                {StatisticTable.COLUMN_ACTIVITY_NAME,
                StatisticTable.COLUMN_APP_NAME},
                StatisticTable.COLUMN_IGNORE + " = " + 0,
                null, StatisticTable.COLUMN_USAGE + " DESC, " + StatisticTable.COLUMN_APP_NAME);
    }

    @Override
    public void onDataSetChanged() {
        mCursor.close();
        mCursor = mContext.getContentResolver().query(DBContentProvider.CONTENT_URI, new String []
                {StatisticTable.COLUMN_ACTIVITY_NAME,
                StatisticTable.COLUMN_APP_NAME},
                StatisticTable.COLUMN_IGNORE + " = " + 0,
                null, StatisticTable.COLUMN_USAGE + " DESC, " + StatisticTable.COLUMN_APP_NAME);
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
        RemoteViews rView = new RemoteViews(mContext.getPackageName(), R.layout.grid_item);
        if (mCursor.moveToPosition(i)) {
            rView.setTextViewText(R.id.name, mCursor.getString(mCursor.getColumnIndex(StatisticTable.COLUMN_APP_NAME)));
            rView.setImageViewBitmap(R.id.icon, Utils.getIcon(mContext, mCursor.getString(mCursor.getColumnIndex(StatisticTable.COLUMN_ACTIVITY_NAME))));
        }

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