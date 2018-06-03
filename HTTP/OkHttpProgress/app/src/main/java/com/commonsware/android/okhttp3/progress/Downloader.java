/***
  Copyright (c) 2008-2016 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.okhttp3.progress;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import java.io.File;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class Downloader extends IntentService {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static int NOTIFY_ID=1337;
  private static int FOREGROUND_ID=1338;

  private NotificationManager mgr;

  public Downloader() {
    super("Downloader");
  }

  @Override
  public void onHandleIntent(Intent i) {
    mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    try {
      String filename=i.getData().getLastPathSegment();
      final NotificationCompat.Builder builder=
        buildForeground(filename);

      startForeground(FOREGROUND_ID, builder.build());

      File root=getExternalFilesDir(null);

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
              builder.setProgress((int)contentLength,
                (int)bytesRead, false);
              mgr.notify(FOREGROUND_ID, builder.build());
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
      BufferedSink sink=Okio.buffer(Okio.sink(new File(output.getPath())));

      sink.writeAll(response.body().source());
      sink.close();

      stopForeground(true);
      raiseNotification(null);
    }
    catch (IOException e2) {
      stopForeground(true);
      raiseNotification(e2);
    }
  }

  private void raiseNotification(Exception e) {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
     .setWhen(System.currentTimeMillis());

    if (e == null) {
      b.setContentTitle(getString(R.string.download_complete))
       .setSmallIcon(android.R.drawable.stat_sys_download_done)
       .setTicker(getString(R.string.download_complete));
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
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setContentTitle(getString(R.string.downloading))
     .setContentText(filename)
     .setSmallIcon(android.R.drawable.stat_sys_download)
     .setOnlyAlertOnce(true)
     .setOngoing(true);

    return(b);
  }
}
