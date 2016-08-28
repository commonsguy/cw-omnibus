/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import android.util.Log;

public class DemoJobService extends JobService {
  private volatile Thread job=null;

  @Override
  public boolean onStartJob(JobParameters params) {
    PersistableBundle pb=params.getExtras();

    if (pb.getBoolean(MainActivity.KEY_DOWNLOAD, false)) {
      job=new DownloadThread(params);
      job.start();

      return(true);
    }

    Log.d(getClass().getSimpleName(), "job invoked");

    return(false);
  }

  @Override
  synchronized public boolean onStopJob(JobParameters params) {
    if (job!=null) {
      Log.d(getClass().getSimpleName(), "job interrupted");
      job.interrupt();
    }

    return(false);
  }

  synchronized private void clearJob() {
    job=null;
  }

  private class DownloadThread extends Thread {
    private final JobParameters params;

    DownloadThread(JobParameters params) {
      this.params=params;
    }

    @Override
    public void run() {
      Log.d(getClass().getSimpleName(), "job begins");
      new DownloadJob().run();
      Log.d(getClass().getSimpleName(), "job ends");
      clearJob();
      jobFinished(params, false);
    }
  }
}
