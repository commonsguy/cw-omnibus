/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.appwidget.dice;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

public class AppWidget extends AppWidgetProvider {
  private static final int[] IMAGES= { R.drawable.die_1,
      R.drawable.die_2, R.drawable.die_3, R.drawable.die_4,
      R.drawable.die_5, R.drawable.die_6 };

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction() == null) {
      updateWidget(context,
                   AppWidgetManager.getInstance(context),
                   intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                      -1));
    }
    else {
      super.onReceive(context, intent);
    }
  }

  @Override
  public void onUpdate(Context ctxt, AppWidgetManager mgr,
                       int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      updateWidget(ctxt, mgr, appWidgetId);
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void updateWidget(Context ctxt, AppWidgetManager mgr,
                            int appWidgetId) {
    int layout=R.layout.widget;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      int category=
          mgr.getAppWidgetOptions(appWidgetId)
             .getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY,
                     -1);

      layout=
          (category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD)
              ? R.layout.lockscreen : R.layout.widget;
    }

    RemoteViews updateViews=
        new RemoteViews(ctxt.getPackageName(), layout);
    Intent i=new Intent(ctxt, AppWidget.class);

    i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

    PendingIntent pi=
        PendingIntent.getBroadcast(ctxt, appWidgetId, i,
                                   PendingIntent.FLAG_UPDATE_CURRENT);

    updateViews.setImageViewResource(R.id.left_die,
                                     IMAGES[(int)(Math.random() * 6)]);
    updateViews.setOnClickPendingIntent(R.id.left_die, pi);
    updateViews.setImageViewResource(R.id.right_die,
                                     IMAGES[(int)(Math.random() * 6)]);
    updateViews.setOnClickPendingIntent(R.id.right_die, pi);
    updateViews.setOnClickPendingIntent(R.id.background, pi);

    if (layout == R.layout.lockscreen) {
      updateViews.setImageViewResource(R.id.middle_die,
                                       IMAGES[(int)(Math.random() * 6)]);
      updateViews.setOnClickPendingIntent(R.id.middle_die, pi);
    }

    mgr.updateAppWidget(appWidgetId, updateViews);
  }
}