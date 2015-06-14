/***
  Copyright (c) 2014-2015 CommonsWare, LLC
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

package com.commonsware.android.deepbg;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DemoScheduledService extends IntentService {
  public DemoScheduledService() {
    super("DemoScheduledService");
    Log.d(getClass().getSimpleName(), "constructor");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(getClass().getSimpleName(), "onCreate");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(getClass().getSimpleName(), "onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  protected void onHandleIntent(Intent i) {
    Log.d(getClass().getSimpleName(), "onHandleIntent");
    DownloadJob.log(this, "scheduled work begins");

    if (i.getBooleanExtra(PollReceiver.EXTRA_IS_DOWNLOAD, false)) {
      new DownloadJob(this).run();  // do synchronously, as we are on
                                // a background thread already
    }

    DownloadJob.log(this, "scheduled work ends");
    PollReceiver.completeWakefulIntent(i);
  }
}
