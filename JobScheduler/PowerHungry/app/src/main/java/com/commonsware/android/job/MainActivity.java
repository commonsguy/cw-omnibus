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

package com.commonsware.android.job;

import android.app.AlarmManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AbstractPermissionActivity
    implements CompoundButton.OnCheckedChangeListener {
  private static final long[] PERIODS={
      60000,
      AlarmManager.INTERVAL_FIFTEEN_MINUTES,
      AlarmManager.INTERVAL_HALF_HOUR,
      AlarmManager.INTERVAL_HOUR
  };
  private static final int JOB_ID=1337;
  static final String KEY_DOWNLOAD="isDownload";
  private Spinner type=null;
  private Spinner period=null;
  private Switch download=null;
  private Switch scheduled=null;
  private AlarmManager alarms=null;
  private JobScheduler jobs=null;

  @Override
  protected String[] getDesiredPermissions() {
    return(new String[]{WRITE_EXTERNAL_STORAGE});
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_sorry, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @SuppressWarnings("ResourceType")
  @Override
  public void onReady(Bundle savedInstanceState) {
    setContentView(R.layout.main);
    type=findViewById(R.id.type);

    ArrayAdapter<String> types=
        new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            getResources().getStringArray(R.array.types));

    types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    type.setAdapter(types);

    period=findViewById(R.id.period);

    ArrayAdapter<String> periods=
        new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            getResources().getStringArray(R.array.periods));

    periods.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    period.setAdapter(periods);

    download=findViewById(R.id.download);
    scheduled=findViewById(R.id.scheduled);
    scheduled.setOnCheckedChangeListener(this);

    alarms=(AlarmManager)getSystemService(ALARM_SERVICE);
    jobs=(JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    toggleWidgets(!isChecked);

    switch(type.getSelectedItemPosition()) {
      case 0:
        manageExact(isChecked);
        break;

      case 1:
        manageInexact(isChecked);
        break;

      case 2:
        manageJobScheduler(isChecked);
        break;
    }
  }

  private void toggleWidgets(boolean enable) {
    type.setEnabled(enable);
    period.setEnabled(enable);
    download.setEnabled(enable);
  }

  private void manageExact(boolean start) {
    if (start) {
      long period=getPeriod();

      PollReceiver.scheduleExactAlarm(this, alarms, period,
          download.isChecked());
    }
    else {
      PollReceiver.cancelAlarm(this, alarms);
    }
  }

  private void manageInexact(boolean start) {
    if (start) {
      long period=getPeriod();

      PollReceiver.scheduleInexactAlarm(this, alarms, period,
          download.isChecked());
    }
    else {
      PollReceiver.cancelAlarm(this, alarms);
    }
  }

  private void manageJobScheduler(boolean start) {
    if (start) {
      JobInfo.Builder b=new JobInfo.Builder(JOB_ID,
          new ComponentName(this, DemoJobService.class));
      PersistableBundle pb=new PersistableBundle();

      if (download.isChecked()) {
        pb.putBoolean(KEY_DOWNLOAD, true);
        b.setExtras(pb).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
      } else {
        b.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
      }

      b.setPeriodic(getPeriod()).setPersisted(false)
          .setRequiresCharging(false).setRequiresDeviceIdle(true);

      jobs.schedule(b.build());
    }
    else {
      jobs.cancel(JOB_ID);
    }
  }

  private long getPeriod() {
    return(PERIODS[period.getSelectedItemPosition()]);
  }
}