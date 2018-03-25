/***
  Copyright (c) 2013-2017 CommonsWare, LLC
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

package com.commonsware.android.rv.transcript;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;

public class EventDemoActivity extends FragmentActivity {
  private static final int JOB_ID=1337;

  @Override @SuppressLint("MissingPermission")
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
      getSupportFragmentManager().beginTransaction()
                          .add(android.R.id.content,
                               new EventLogFragment()).commit();

      JobScheduler jobs=
        (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
      ComponentName cn=new ComponentName(this, ScheduledService.class);
      JobInfo.Builder b=new JobInfo.Builder(JOB_ID, cn)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
        .setPeriodic(60000)
        .setPersisted(false)
        .setRequiresCharging(false)
        .setRequiresDeviceIdle(false);

      jobs.schedule(b.build());
    }
  }
}