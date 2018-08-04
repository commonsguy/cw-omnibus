/***
 Copyright (c) 2012-2016 CommonsWare, LLC
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

package com.commonsware.android.strictmode.greymarker;

import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.strictmode.Violation;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(Suite.class)
@Suite.SuiteClasses({BadHorseTest.class})
public class GreylistSuite {
  private static final String TAG="GreylistSuite";
  private static final ExecutorService LISTENER_EXECUTOR=Executors.newSingleThreadExecutor();
  private static File LOG_DIR=
    new File(InstrumentationRegistry.getTargetContext().getExternalCacheDir(), "__greylist");

  @BeforeClass
  public static void init() {
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.P) {
      if (LOG_DIR.listFiles()!=null) {
        for (File file : LOG_DIR.listFiles()) {
          if (!file.isDirectory()) {
            file.delete();
          }
        }
      }

      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectNonSdkApiUsage()
        .penaltyListener(LISTENER_EXECUTOR, GREYLISTENER)
        .build());
    }
  }

  @AfterClass
  public static void term() {
    LISTENER_EXECUTOR.shutdown();

    try {
      LISTENER_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      Log.e(TAG, "Saving stack traces took too long!", e);
    }
  }

  private final static StrictMode.OnVmViolationListener GREYLISTENER=
    new StrictMode.OnVmViolationListener() {
      @Override
      public void onVmViolation(Violation violation) {
        LOG_DIR.mkdirs();

        String name=Long.toString(SystemClock.uptimeMillis())+".txt";
        File trace=new File(LOG_DIR, name);

        try {
          FileOutputStream fos=new FileOutputStream(trace);
          OutputStreamWriter osw=new OutputStreamWriter(fos);
          PrintWriter out=new PrintWriter(osw);

          violation.printStackTrace(out);
          out.flush();
          fos.getFD().sync();
          out.close();
        }
        catch (IOException e) {
          Log.e(TAG, "Exception writing trace", e);
        }
      }
  };
}
