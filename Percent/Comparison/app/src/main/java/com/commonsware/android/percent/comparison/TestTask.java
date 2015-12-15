/***
 Copyright (c) 2012-15 CommonsWare, LLC
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

package com.commonsware.android.percent.comparison;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

class TestTask extends
  AsyncTask<View, Void, Void> {
  private static final int PASSES=10000000;
  private final Context ctxt;

  public TestTask(Context ctxt) {
    super();

    this.ctxt=ctxt.getApplicationContext();
  }

  @SuppressWarnings("ResourceType")
  @Override
  protected Void doInBackground(View... params) {
    View test=params[0];

    test.measure(480, 800);

    long start=SystemClock.uptimeMillis();

    for (int i=0; i<PASSES; i++) {
      test.layout(0, 0, 480, 800);
    }

    long split=SystemClock.uptimeMillis();

    Log.d("PerfTest",
      String.format("%d layout passes in %d ms", PASSES,
        split-start));

    for (int i=0; i<PASSES; i++) {
      test.measure(480, 800);
      test.layout(0, 0, 480, 800);
    }

    Log.d("PerfTest",
      String.format("%d measure & layout passes in %d ms",
        PASSES,
        SystemClock.uptimeMillis()-split));

    return (null);
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    Toast
      .makeText(ctxt, "Test complete", Toast.LENGTH_LONG)
      .show();
  }
}
