/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.debug.webserver;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import com.commonsware.android.webserver.WebServerService;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;

public class PicassoDiagnosticService extends WebServerService {
  @Override
  protected boolean configureRoutes(AsyncHttpServer server) {
    server.get("/stop", new StopRequestCallback());

    return(true);
  }

  @Override
  protected int getPort() {
    return(4999);
  }

  @Override
  protected int getMaxIdleTimeSeconds() {
    return(120);
  }

  @Override
  protected int getMaxSequentialInvalidRequests() {
    return(10);
  }

  @Override
  protected void buildForegroundNotification(NotificationCompat.Builder b) {
    Intent iActivity=new Intent(this, PicassoDiagnosticActivity.class);
    PendingIntent piActivity=PendingIntent.getActivity(this, 0,
      iActivity, 0);

    b.setContentTitle(getString(R.string.app_name))
      .setContentIntent(piActivity)
      .setSmallIcon(R.drawable.ic_launcher)
      .setTicker(getString(R.string.app_name));
  }

  @Override
  protected Context getContextForPath(String relpath) {
    if ("picasso.hbs".equals(relpath)) {
      StatsSnapshot ss=Picasso
        .with(PicassoDiagnosticService.this)
        .getSnapshot();
      String formattedTime=DateUtils.formatDateTime(PicassoDiagnosticService.this,
        ss.timeStamp,
        DateUtils.FORMAT_SHOW_TIME);

      return(Context
        .newBuilder(ss)
        .combine("formattedTime", formattedTime)
        .resolver(FieldValueResolver.INSTANCE)
        .build());
    }

    throw new IllegalStateException("Did not recognize "+relpath);
  }

  private class StopRequestCallback implements HttpServerRequestCallback {
    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
      response.send("Goodbye, cruel world!");
      stopSelf();
    }
  }
}
