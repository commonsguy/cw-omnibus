/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.assist.logger;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssistLoggerSession extends
  VoiceInteractionSession {
  private File logDir=null;

  public AssistLoggerSession(Context context) {
    super(context);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    if (Environment.MEDIA_MOUNTED
      .equals(Environment.getExternalStorageState())) {
      String logDirName=
        "assistlogger_"+
          new SimpleDateFormat("yyyyMMdd'-'HHmmss").format(new Date());

      logDir=
        new File(getContext().getExternalCacheDir(), logDirName);
      logDir.mkdirs();
    }
  }

  @Override
  public void onAssistStructureFailure(Throwable failure) {
    super.onAssistStructureFailure(failure);

    Log.e(getClass().getSimpleName(), "onAssistStructureFailure",
      failure);
  }

  @Override
  public void onHandleScreenshot(Bitmap screenshot) {
    super.onHandleScreenshot(screenshot);

    if (screenshot!=null) {
      new ScreenshotThread(logDir, screenshot).start();
    }
  }

  @Override
  public void onHandleAssist(Bundle data,
                             AssistStructure structure,
                             AssistContent content) {
    super.onHandleAssist(data, structure, content);

    new AssistDumpThread(logDir, data, structure, content).start();
  }

  private static class ScreenshotThread extends Thread {
    private final File logDir;
    private final Bitmap screenshot;

    ScreenshotThread(File logDir, Bitmap screenshot) {
      this.logDir=logDir;
      this.screenshot=screenshot;
    }

    @Override
    public void run() {
      if (logDir!=null) {
        try {
          File f=new File(logDir, "screenshot.png");
          FileOutputStream fos=new FileOutputStream(f);

          screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
          fos.flush();
          fos.getFD().sync();
          fos.close();
          Log.d(getClass().getSimpleName(),
            "screenshot written to: "+f.getAbsolutePath());
        }
        catch (IOException e) {
          Log.e(getClass().getSimpleName(),
            "Exception writing out screenshot", e);
        }
      }
      else {
        Log.d(getClass().getSimpleName(),
          String.format("onHandleScreenshot: %dx%d",
            screenshot.getWidth(), screenshot.getHeight()));
      }
    }
  }
}
