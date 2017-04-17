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

package com.commonsware.android.preso.slides;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import static com.commonsware.android.preso.slides.ControlReceiver.EXTRA_DELTA;
import static com.commonsware.android.preso.slides.ControlReceiver.EXTRA_STOP;

public class PresentationActivity extends Activity {
  private ImageView slide;
  private int position;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preso);

    slide=(ImageView)findViewById(R.id.slide);

    SlidePositionEvent event=
      EventBus.getDefault().getStickyEvent(SlidePositionEvent.class);

    if (event==null) {
      updateSlide();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    EventBus.getDefault().register(this);
  }

  @Override
  protected void onStop() {
    EventBus.getDefault().unregister(this);

    super.onStop();
  }

  @Subscribe(sticky=true, threadMode=ThreadMode.MAIN)
  public void onSlideChanged(SlidePositionEvent event) {
    position=event.position;
    updateSlide();
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onBroadcast(Intent i) {
    if (i.getBooleanExtra(EXTRA_STOP, false)) {
      ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancelAll();
      finish();
    }
    else {
      int delta=i.getIntExtra(EXTRA_DELTA, 0);

      if (position+delta>=0 && position+delta<SlidesAdapter.SLIDES.length) {
        position+=delta;
        updateSlide();
      }
    }
  }

  private void updateSlide() {
    slide.setImageResource(SlidesAdapter.SLIDES[position]);
  }

  static class SlidePositionEvent {
    final int position;

    SlidePositionEvent(int position) {
      this.position=position;
    }
  }
}
