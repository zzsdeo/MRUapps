package ru.zzsdeo.mruapps;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.List;

public class Widget extends AppWidgetProvider {

    public static final String CLICK_ACTION = "click_action";
    public static final String EXTRA_ITEM = "extra_item";
    public static final String SETTINGS_ACTION = "settings_action";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_ACTION)) {
            //int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);

            Intent serviceIntent = new Intent(context, DBUpdateIntentService.class);
            serviceIntent.setAction(DBUpdateIntentService.LAUNCH_ACTION);
            serviceIntent.putExtra(EXTRA_ITEM, viewIndex);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
            rv.setRemoteAdapter(R.id.wgtGridView, intent);

            Intent clickIntent = new Intent(context, Widget.class);
            clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            clickIntent.setAction(CLICK_ACTION);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.wgtGridView, clickPendingIntent);

            Intent settingsIntent = new Intent(context, ChoseAppsActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            settingsIntent.setAction(SETTINGS_ACTION);
            settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent mruAppPendingIntent = PendingIntent.getActivity(context, appWidgetId, settingsIntent, 0);
            rv.setOnClickPendingIntent(R.id.wgtImageButton, mruAppPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }
}