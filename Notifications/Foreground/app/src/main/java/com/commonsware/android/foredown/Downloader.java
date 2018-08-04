/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.foredown;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader extends IntentService {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final String AUTHORITY=
    BuildConfig.APPLICATION_ID+".provider";
  private static int NOTIFY_ID=1337;
  private static int FOREGROUND_ID=1338;

  public Downloader() {
    super("Downloader");
  }

  @Override
  public void onHandleIntent(Intent i) {
    NotificationManager mgr=
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {

      NotificationChannel c=new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT);

      c.enableLights(true);
      c.setLightColor(0xFFFF0000);

      mgr.createNotificationChannel(c);
    }

    String filename=i.getData().getLastPathSegment();

    startForeground(FOREGROUND_ID,
      buildForegroundNotification(filename));

    try {
      File output=new File(getFilesDir(), filename);

      if (output.exists()) {
        output.delete();
      }

      URL url=new URL(i.getData().toString());
      HttpURLConnection c=(HttpURLConnection)url.openConnection();
      FileOutputStream fos=new FileOutputStream(output.getPath());
      BufferedOutputStream out=new BufferedOutputStream(fos);

      try {
        InputStream in=c.getInputStream();
        byte[] buffer=new byte[8192];
        int len=0;

        while ((len=in.read(buffer)) >= 0) {
          out.write(buffer, 0, len);
        }

        out.flush();
      }
      finally {
        fos.getFD().sync();
        out.close();
        c.disconnect();
      }

      raiseNotification(i, output, null);
    }
    catch (IOException e2) {
      raiseNotification(i, null, e2);
    }
    finally {
      stopForeground(true);
    }
  }

  private void raiseNotification(Intent inbound, File output,
                                 Exception e) {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setAutoCancel(true).setWhen(System.currentTimeMillis());

    if (e == null) {
      b.setContentTitle(getString(R.string.download_complete))
       .setContentText(getString(R.string.fun))
       .setSmallIcon(android.R.drawable.stat_sys_download_done);

      Intent outbound=new Intent(Intent.ACTION_VIEW);
      Uri outputUri=
        FileProvider.getUriForFile(this, AUTHORITY, output);

      outbound.setDataAndType(outputUri, inbound.getType());
      outbound.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      PendingIntent pi=PendingIntent.getActivity(this, 0,
        outbound, PendingIntent.FLAG_UPDATE_CURRENT);

      b.setContentIntent(pi);
    }
    else {
      b.setContentTitle(getString(R.string.exception))
       .setContentText(e.getMessage())
       .setSmallIcon(android.R.drawable.stat_notify_error);
    }

    NotificationManager mgr=
        (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    mgr.notify(NOTIFY_ID, b.build());
  }

  private Notification buildForegroundNotification(String filename) {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setOngoing(true)
     .setContentTitle(getString(R.string.downloading))
     .setContentText(filename)
     .setSmallIcon(android.R.drawable.stat_sys_download);

    return(b.build());
  }
}
