/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.recyclerview.videolist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

// inspired by http://stackoverflow.com/a/22101290/115145

public class ResizeableVideoView extends VideoView {
  public ResizeableVideoView(Context context) {
    super(context);
  }

  public ResizeableVideoView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ResizeableVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public ResizeableVideoView(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    getHolder().setSizeFromLayout();
  }
}
