package ru.zzsdeo.mruapps;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
            rv.setRemoteAdapter(R.id.wgtGridView, intent);
            //rv.setEmptyView(android.R.id.list, R.id.tvEmptyList);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }


        /*RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        widgetView.setRemoteAdapter(R.id.wgtGridView, intent);*/
    }
}