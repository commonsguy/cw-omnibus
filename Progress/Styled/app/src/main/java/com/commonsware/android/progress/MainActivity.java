/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.progress;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements Runnable {
  private static final int PERIOD_SECONDS=1;
  private ScheduledThreadPoolExecutor executor=
      new ScheduledThreadPoolExecutor(1);
  private ProgressBar primary=null;
  private ProgressBar secondary=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    primary=(ProgressBar)findViewById(R.id.progressHS);
    secondary=(ProgressBar)findViewById(R.id.progressHS2);

    executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    executor.scheduleAtFixedRate(this, 0, PERIOD_SECONDS,
                                 TimeUnit.SECONDS);
  }

  @Override
  public void onDestroy() {
    executor.shutdown();

    super.onDestroy();
  }

  @Override
  public void run() {
    if (primary.getProgress() < 100) {
      primary.incrementProgressBy(2);
      secondary.incrementProgressBy(2);

      if (secondary.getSecondaryProgress() == 100) {
        secondary.setSecondaryProgress(10);
      }
      else {
        secondary.incrementSecondaryProgressBy(10);
      }
    }
    else {
      executor.remove(this);
    }
  }
}
