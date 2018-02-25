/***
 Copyright (c) 2014-2016 CommonsWare, LLC
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

package com.commonsware.android.jobsched.content;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import static android.provider.ContactsContract.Contacts.CONTENT_URI;

public class DemoJobService extends JobService {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final int ME_MYSELF_AND_I=3493;
  private static final int NOTIFY_ID=2343;

  static void schedule(Context ctxt) {
    ComponentName cn=
      new ComponentName(ctxt, DemoJobService.class);
    JobInfo.TriggerContentUri trigger=
      new JobInfo.TriggerContentUri(CONTENT_URI,
        JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS);
    JobInfo.Builder b=
      new JobInfo.Builder(ME_MYSELF_AND_I, cn)
        .addTriggerContentUri(trigger);
    JobScheduler jobScheduler=
      (JobScheduler)ctxt.getSystemService(Context.JOB_SCHEDULER_SERVICE);

    jobScheduler.schedule(b.build());
  }

  @Override
  public boolean onStartJob(JobParameters params) {
    NotificationManager mgr=
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentTitle("You added a contact!")
        .setSmallIcon(android.R.drawable.stat_notify_more);

    mgr.notify(NOTIFY_ID, b.build());

    return(false);
  }

  @Override
  synchronized public boolean onStopJob(JobParameters params) {
    return(false);
  }
}
