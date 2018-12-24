package com.example.mansi.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.mansi.Entities.CategoryEntity;
import com.example.mansi.MainActivity;
import com.example.mansi.R;
import com.example.mansi.Widgets.SharedPrefUtil;
import com.example.mansi.Widgets.WidgetService;

import java.util.ArrayList;


/**
 * Implementation of App Widget functionality.
 */
public class CategoryWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        ArrayList<CategoryEntity> categorys = SharedPrefUtil.loadCategories(context);


        if (categorys != null) {

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.category_widget);

            views.setTextViewText(R.id.appwidget_text,"Categories");
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            views.setRemoteAdapter(R.id.category_widget_listview, intent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.category_widget_listview);
        }else {
            Log.d("Category","Null");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

