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

package com.commonsware.android.service.ouroboros;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract public class AbstractSillyService extends Service {
  abstract protected Class<? extends AbstractSillyService> getOtherClass();

  static final String EXTRA_FOREGROUND="foreground";
  static final String EXTRA_BROADCAST_HACK="hack";

  private final ScheduledExecutorService timer=
    Executors.newScheduledThreadPool(1);

  @Override
  public int onStartCommand(final Intent intent, int flags, int startId) {
    Log.e(getClass().getSimpleName(), "onStartCommand() called");

    timer.schedule(new Runnable() {
      @Override
      public void run() {
        Intent next=new Intent(AbstractSillyService.this, getOtherClass());

        if (intent.getBooleanExtra(EXTRA_FOREGROUND, false)) {
          Log.e(getClass().getSimpleName(), "starting foreground");
          next.putExtra(EXTRA_FOREGROUND, true);
          startForegroundService(next);
        }
        else if (intent.getBooleanExtra(EXTRA_BROADCAST_HACK, false)) {
          Log.e(getClass().getSimpleName(), "starting via broadcast hack");
          next.putExtra(EXTRA_BROADCAST_HACK, true);
          sendBroadcast(new Intent(AbstractSillyService.this, HackReceiver.class)
            .putExtra(Intent.EXTRA_INTENT, next));
        }
        else {
          Log.e(getClass().getSimpleName(), "starting normal");
          startService(next);
        }
      }
    }, 50, TimeUnit.SECONDS);

    return(START_STICKY);
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new IllegalStateException("Do. Not. Want.");
  }

  @Override
  public void onDestroy() {
    Log.e(getClass().getSimpleName(), "onDestroy() called");

    super.onDestroy();
  }
}
