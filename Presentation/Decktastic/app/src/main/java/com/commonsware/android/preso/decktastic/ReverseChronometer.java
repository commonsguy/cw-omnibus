/***
  Copyright (c) 2013-14 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

public class ReverseChronometer extends TextView implements Runnable {
  long startTime=0L;
  long overallDuration=0L;
  long warningDuration=0L;
  boolean isRunning=false;

  public ReverseChronometer(Context context) {
    super(context);

    reset();
  }

  public ReverseChronometer(Context context, AttributeSet attrs) {
    super(context, attrs);

    reset();
  }

  @Override
  public void run() {
    isRunning=true;

    long elapsedSeconds=
        (SystemClock.elapsedRealtime() - startTime) / 1000;

    if (elapsedSeconds < overallDuration) {
      long remainingSeconds=overallDuration - elapsedSeconds;
      long minutes=remainingSeconds / 60;
      long seconds=remainingSeconds - (60 * minutes);

      setText(String.format("%d:%02d", minutes, seconds));

      if (warningDuration > 0 && remainingSeconds < warningDuration) {
        setTextColor(0xFFFF6600); // orange
      }
      else {
        setTextColor(Color.WHITE);
      }

      postDelayed(this, 1000);
    }
    else {
      setText("0:00");
      setTextColor(Color.RED);
      isRunning=false;
    }
  }

  public void reset() {
    startTime=SystemClock.elapsedRealtime();
    setText("--:--");
    setTextColor(Color.WHITE);
  }

  public void stop() {
    removeCallbacks(this);
    isRunning=false;
  }

  public boolean isRunning() {
    return(isRunning);
  }

  public void setOverallDuration(long overallDuration) {
    this.overallDuration=overallDuration;
  }

  public void setWarningDuration(long warningDuration) {
    this.warningDuration=warningDuration;
  }
}
