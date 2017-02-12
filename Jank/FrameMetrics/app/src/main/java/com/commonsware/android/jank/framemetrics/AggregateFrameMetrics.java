/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.jank.framemetrics;

import android.util.Log;
import android.view.FrameMetrics;

public class AggregateFrameMetrics {
  int droppedReports;
  long animationDuration;
  long commandIssueDuration;
  long drawDuration;
  long inputHandlingDuration;
  long layoutMeasureDuration;
  long swapBuffersDuration;
  long syncDuration;
  long unknownDelayDuration;
  long totalDuration;

  void add(FrameMetrics metrics, int droppedReports) {
    this.droppedReports+=droppedReports;

    animationDuration+=
      metrics.getMetric(FrameMetrics.ANIMATION_DURATION);
    commandIssueDuration+=
      metrics.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION);
    drawDuration+=metrics.getMetric(FrameMetrics.DRAW_DURATION);
    inputHandlingDuration+=
      metrics.getMetric(FrameMetrics.INPUT_HANDLING_DURATION);
    layoutMeasureDuration+=
      metrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION);
    swapBuffersDuration+=
      metrics.getMetric(FrameMetrics.SWAP_BUFFERS_DURATION);
    syncDuration+=metrics.getMetric(FrameMetrics.SYNC_DURATION);
    unknownDelayDuration+=
      metrics.getMetric(FrameMetrics.UNKNOWN_DELAY_DURATION);
    totalDuration+=metrics.getMetric(FrameMetrics.TOTAL_DURATION);
  }

  void log(String tag) {
    Log.d(tag, String.format("animation: %dns", animationDuration));
    Log.d(tag, String.format("command issue: %dns", commandIssueDuration));
    Log.d(tag, String.format("draw: %dns", drawDuration));
    Log.d(tag, String.format("input handling: %dns", inputHandlingDuration));
    Log.d(tag, String.format("layout measure: %dns", layoutMeasureDuration));
    Log.d(tag, String.format("swap buffers: %dns", swapBuffersDuration));
    Log.d(tag, String.format("sync: %dns", syncDuration));
    Log.d(tag, String.format("unknown: %dns", unknownDelayDuration));
    Log.d(tag, String.format("total: %dns", totalDuration));
    Log.d(tag, String.format("%d dropped reports", droppedReports));
  }
}
