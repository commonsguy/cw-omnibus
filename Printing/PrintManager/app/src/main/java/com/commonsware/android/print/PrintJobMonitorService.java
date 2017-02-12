/***
  Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.print;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PrintJobMonitorService extends Service implements Runnable {
  private static final int POLL_PERIOD=3;
  private PrintManager mgr=null;
  private ScheduledExecutorService executor=
      Executors.newSingleThreadScheduledExecutor();
  private long lastPrintJobTime=SystemClock.elapsedRealtime();

  @Override
  public void onCreate() {
    super.onCreate();

    mgr=(PrintManager)getSystemService(PRINT_SERVICE);
    executor.scheduleAtFixedRate(this, POLL_PERIOD, POLL_PERIOD,
                                 TimeUnit.SECONDS);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return(super.onStartCommand(intent, flags, startId));
  }

  @Override
  public void onDestroy() {
    executor.shutdown();

    super.onDestroy();
  }

  @Override
  public void run() {
    for (PrintJob job : mgr.getPrintJobs()) {
      if (job.getInfo().getState() == PrintJobInfo.STATE_CREATED
          || job.isQueued() || job.isStarted()) {
        lastPrintJobTime=SystemClock.elapsedRealtime();
      }
    }

    long delta=SystemClock.elapsedRealtime() - lastPrintJobTime;

    if (delta > POLL_PERIOD * 2) {
      stopSelf();
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return(null);
  }
}
