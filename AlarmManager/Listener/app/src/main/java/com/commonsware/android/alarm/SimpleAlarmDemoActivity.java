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

package com.commonsware.android.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class SimpleAlarmDemoActivity extends Activity
  implements AlarmManager.OnAlarmListener {
  private static final int PERIOD=5000;
  private static final int WINDOW=10000;
  private AlarmManager mgr=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mgr=getSystemService(AlarmManager.class);
    schedule();
  }

  @Override
  public void onDestroy() {
    mgr.cancel(this);

    super.onDestroy();
  }

  @Override
  public void onAlarm() {
    Toast.makeText(this, R.string.toast, Toast.LENGTH_SHORT).show();
    Log.d(getClass().getSimpleName(), "I ran!");
    schedule();
  }

  private void schedule() {
    mgr.setWindow(AlarmManager.ELAPSED_REALTIME,
      SystemClock.elapsedRealtime()+PERIOD, WINDOW,
      getClass().getSimpleName(), this, null);
  }
}