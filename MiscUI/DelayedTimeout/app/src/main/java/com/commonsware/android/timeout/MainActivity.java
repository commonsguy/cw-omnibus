/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.timeout;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

public class MainActivity extends Activity implements Runnable {
  private static int TIMEOUT_POLL_PERIOD=15000; // 15 seconds
  private static int TIMEOUT_PERIOD=300000; // 5 minutes
  private View content=null;
  private long lastActivity=SystemClock.uptimeMillis();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    content=findViewById(android.R.id.content);
    content.setKeepScreenOn(true);
    run();
  }

  @Override
  public void onDestroy() {
    content.removeCallbacks(this);

    super.onDestroy();
  }

  @Override
  public void run() {
    if ((SystemClock.uptimeMillis() - lastActivity) > TIMEOUT_PERIOD) {
      content.setKeepScreenOn(false);
    }

    content.postDelayed(this, TIMEOUT_POLL_PERIOD);
  }

  public void onClick(View v) {
    lastActivity=SystemClock.uptimeMillis();
  }
}
