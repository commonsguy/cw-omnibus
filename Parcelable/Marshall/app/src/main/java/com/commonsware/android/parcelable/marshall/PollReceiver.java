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

package com.commonsware.android.parcelable.marshall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class PollReceiver extends BroadcastReceiver {
  private static final String EXTRA_THINGY=
    "heyYouCanNameYourExtrasWhateverYouWant";
  private static final int PERIOD=60000; // 1 minute
  private static final int INITIAL_DELAY=5000; // 5 seconds

  @Override
  public void onReceive(Context ctxt, Intent i) {
    Thingy thingy=
      Parcelables.toParcelable(i.getByteArrayExtra(EXTRA_THINGY),
        Thingy.CREATOR);

    if (i.getAction() == null) {
      ScheduledService.enqueueWork(ctxt);
    }
    else {
      scheduleAlarms(ctxt);
    }
  }

  static void scheduleAlarms(Context ctxt) {
    AlarmManager mgr=
        (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
    Thingy thingy=
      new Thingy(mgr.getClass().getCanonicalName(), mgr.hashCode());
    Intent i=
      new Intent(ctxt, PollReceiver.class)
      .putExtra(EXTRA_THINGY, Parcelables.toByteArray(thingy));
    PendingIntent pi=PendingIntent.getBroadcast(ctxt, 0, i, 0);

    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                     SystemClock.elapsedRealtime() + INITIAL_DELAY,
                     PERIOD, pi);
  }
}
