/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.job.work;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;
import okio.HashingSource;

public class WorkService extends JobService {
  private static final int JOB_ID=1337;
  private static final String EXTRA_WORK_INDEX="workIndex";
  private ExecutorService threadPool=Executors.newFixedThreadPool(3);
  private OkHttpClient ok=new OkHttpClient();

  public static JobWorkItem buildWorkItem(int workIndex, String url) {
    Intent i=new Intent();

    i.setData(Uri.parse(url));
    i.putExtra(EXTRA_WORK_INDEX, workIndex);

    return(new JobWorkItem(i));
  }

  public static JobInfo enqueueWork(Context ctxt, JobInfo jobInfo, List<JobWorkItem> work) {
    JobScheduler jobScheduler=ctxt.getSystemService(JobScheduler.class);

    if (jobInfo==null) {
      ComponentName cn=new ComponentName(ctxt, WorkService.class);

      jobInfo=new JobInfo.Builder(JOB_ID, cn)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        .build();
    }

    for (JobWorkItem item : work) {
      jobScheduler.enqueue(jobInfo, item);
    }

    return(jobInfo);
  }

  @Override
  public boolean onStartJob(JobParameters params) {
    scheduleWork(params);

    return(true);
  }

  @Override
  public boolean onStopJob(JobParameters params) {
    threadPool.shutdown();

    return(true);
  }

  private void scheduleWork(final JobParameters params) {
    if (!threadPool.isShutdown()) {
      JobWorkItem item;

      while ((item=params.dequeueWork())!=null) {
        final int workIndex=item.getIntent().getIntExtra(EXTRA_WORK_INDEX, -1);
        final String url=item.getIntent().getData().toString();
        final JobWorkItem itemToDo=item;

        threadPool.execute(new Runnable() {
          @Override
          public void run() {
            download(workIndex, url);
            params.completeWork(itemToDo);
            scheduleWork(params);
          }
        });
      }
    }
  }

  private void download(int workIndex, String url) {
    try {
      Response response=ok.newCall(new Request.Builder().url(url).build()).execute();
      HashingSource hashingSource=HashingSource.sha256(response.body().source());

      EventBus.getDefault().post(new Result(hashingSource.hash(), workIndex, null));
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception from OkHttp", e);
      EventBus.getDefault().post(new Result(null, workIndex, e));
    }
  }

  public static class Result {
    public final ByteString hash;
    public final int workIndex;
    public final Exception e;

    Result(ByteString hash, int workIndex, Exception e) {
      this.hash=hash;
      this.workIndex=workIndex;
      this.e=e;
    }
  }
}
