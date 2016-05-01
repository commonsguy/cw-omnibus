/***
  Copyright (c) 2008-2016 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.okhttp3.progress;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import java.io.File;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class Downloader extends IntentService {
  private static int NOTIFY_ID=1337;
  private static int FOREGROUND_ID=1338;

  private NotificationManager mgr;

  public Downloader() {
    super("Downloader");
  }

  @Override
  public void onHandleIntent(Intent i) {
    mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    try {
      String filename=i.getData().getLastPathSegment();
      NotificationCompat.Builder builder=
        buildForeground(filename);
      final Notification notif=builder.build();

      startForeground(FOREGROUND_ID, notif);

      File root=
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

      root.mkdirs();

      File output=new File(root, filename);

      if (output.exists()) {
        output.delete();
      }

      final ProgressResponseBody.Listener progressListener=
        new ProgressResponseBody.Listener() {
          long lastUpdateTime=0L;

          @Override
          public void onProgressChange(long bytesRead,
                                       long contentLength,
                                       boolean done) {
            long now=SystemClock.uptimeMillis();

            if (now-lastUpdateTime>1000) {
              notif
                .contentView
                .setProgressBar(android.R.id.progress,
                  (int)contentLength, (int)bytesRead, false);
              mgr.notify(FOREGROUND_ID, notif);
              lastUpdateTime=now;
            }
          }
        };

      Interceptor nightTrain=new Interceptor() {
        @Override
        public Response intercept(Chain chain)
          throws IOException {
          Response original=chain.proceed(chain.request());
          Response.Builder b=original
            .newBuilder()
            .body(
              new ProgressResponseBody(original.body(),
                  progressListener));

          return(b.build());
        }
      };

      OkHttpClient client=new OkHttpClient.Builder()
        .addNetworkInterceptor(nightTrain)
        .build();
      Request request=
        new Request.Builder().url(i.getData().toString()).build();
      Response response=client.newCall(request).execute();
      String contentType=response.header("Content-type");
      BufferedSink sink=Okio.buffer(Okio.sink(new File(output.getPath())));

      sink.writeAll(response.body().source());
      sink.close();

      stopForeground(true);
      raiseNotification(contentType, output, null);
    }
    catch (IOException e2) {
      stopForeground(true);
      raiseNotification(null, null, e2);
    }
  }

  private void raiseNotification(String contentType, File output,
                                 Exception e) {
    NotificationCompat.Builder b=new NotificationCompat.Builder(this);

    b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
     .setWhen(System.currentTimeMillis());

    if (e == null) {
      b.setContentTitle(getString(R.string.download_complete))
       .setContentText(getString(R.string.fun))
       .setSmallIcon(android.R.drawable.stat_sys_download_done)
       .setTicker(getString(R.string.download_complete));

      Intent outbound=new Intent(Intent.ACTION_VIEW);

      outbound.setDataAndType(Uri.fromFile(output), contentType);

      b.setContentIntent(PendingIntent.getActivity(this, 0, outbound, 0));
    }
    else {
      b.setContentTitle(getString(R.string.exception))
       .setContentText(e.getMessage())
       .setSmallIcon(android.R.drawable.stat_notify_error)
       .setTicker(getString(R.string.exception));
    }

    mgr.notify(NOTIFY_ID, b.build());
  }

  private NotificationCompat.Builder buildForeground(
    String filename) {
    NotificationCompat.Builder b=new NotificationCompat.Builder(this);
    RemoteViews content=new RemoteViews(getPackageName(),
      R.layout.notif_content);

    content.setTextViewText(android.R.id.title, "Downloading: "+filename);

    b.setOngoing(true)
      .setContent(content)
      .setSmallIcon(android.R.drawable.stat_sys_download);

    return(b);
  }
}
