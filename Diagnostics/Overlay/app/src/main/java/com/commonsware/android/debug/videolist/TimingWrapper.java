/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.debug.videolist;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class TimingWrapper<T extends RecyclerView.ViewHolder> extends RVAdapterWrapper<T> {
  private static final String TAG="RVAdapterWrapper";
  private final Activity host;
  private final WindowManager wm;
  private View v;

  public TimingWrapper(RecyclerView.Adapter<T> wrapped, Activity host) {
    super(wrapped);
    
    this.host=host;
    wm=(WindowManager)host.getSystemService(Context.WINDOW_SERVICE);
  }

  @Override
  public T onCreateViewHolder(final ViewGroup parent, final int viewType) {
    long start=SystemClock.uptimeMillis();
    T result=super.onCreateViewHolder(parent, viewType);

    warn(SystemClock.uptimeMillis() - start);

    return(result);
  }

  @Override
  public void onBindViewHolder(final T holder, final int position) {
    long start=SystemClock.uptimeMillis();

    super.onBindViewHolder(holder, position);
    warn(SystemClock.uptimeMillis() - start);
  }

  private void warn(long delta) {
    if (delta>7) {
      String msg=String.format("RVAdapterWrapper violation: ~duration= %d ms",
          delta);

      Log.e(TAG, msg, new LogStackTrace());

      if (v==null) {
        WindowManager.LayoutParams params=new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);

        v=new View(host);
        v.setBackgroundResource(R.drawable.border);
        wm.addView(v, params);

        v.postDelayed(new Runnable() {
          @Override
          public void run() {
            wm.removeView(v);
            v=null;
          }
        }, 500);
      }
    }
  }

  private static class LogStackTrace extends Exception {}
}
