/***
 Copyright (c) 2014-2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.deepbg;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

class DownloadJob implements Runnable {
  static final Uri TO_DOWNLOAD=
      Uri.parse("https://commonsware.com/Android/excerpt.pdf");
  private final Context app;
  private SharedPreferences prefs;

  static void log(Context ctxt, String msg) {
    log(new File(ctxt.getCacheDir(), "deep_background_internal.log"), msg);
    log(new File(ctxt.getExternalCacheDir(), "deep_background_external.log"), msg);
  }

  static void log(File log, String msg) {
    Log.d("DownloadLog", "Logging to "+log.getAbsolutePath());

    log.getParentFile().mkdirs();

    try {
      FileOutputStream fos=new FileOutputStream(log, true);
      PrintWriter out=new PrintWriter(new OutputStreamWriter(fos));

      out.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ ")
                  .format(new Date()));
      out.println(msg);
      out.flush();
      fos.getFD().sync();
      out.close();
    }
    catch (Exception e) {
      Log.e("DeepBackground", "Exception writing to log: "+msg, e);
    }
  }

  DownloadJob(Context ctxt) {
    this.app=ctxt.getApplicationContext();
  }

  @Override
  public void run() {
    prefs=PreferenceManager.getDefaultSharedPreferences(app);

    int crashFreq=prefs.getInt(MainActivity.PREF_CRASH_FREQ, -1);

    if (crashFreq>0) {
      int countdown=prefs.getInt(MainActivity.PREF_CRASH_COUNTDOWN, 25);

      if (countdown==1) {
        prefs
            .edit()
            .putInt(MainActivity.PREF_CRASH_COUNTDOWN, crashFreq)
            .commit();
        DownloadJob.log(app, "Intentional crash");
        throw new RuntimeException("App done blow'd up");
      }
      else {
        prefs
            .edit()
            .putInt(MainActivity.PREF_CRASH_COUNTDOWN, countdown-1)
            .commit();
      }
    }

    try {
      File root=
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

      root.mkdirs();

      File output=new File(root, TO_DOWNLOAD.getLastPathSegment());

      if (output.exists()) {
        output.delete();
      }

      URL url=new URL(TO_DOWNLOAD.toString());
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
        DownloadJob.log(app, "Download completed");
      }
      finally {
        fos.getFD().sync();
        out.close();
        c.disconnect();
      }
    }
    catch (IOException e2) {
      DownloadJob.log(app, "Exception in download");
      Log.e("DownloadJob", "Exception in download", e2);
    }
  }
}
