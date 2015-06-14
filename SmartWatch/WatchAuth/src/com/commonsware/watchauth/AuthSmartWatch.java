/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.watchauth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

class AuthSmartWatch extends ControlExtension implements
    View.OnClickListener {
  private static final Bitmap.Config BITMAP_CONFIG=
      Bitmap.Config.RGB_565;
  private final int width;
  private final int height;
  private ViewGroup content=null;

  AuthSmartWatch(final String hostAppPackageName, final Context context) {
    super(context, hostAppPackageName);
    width=getSupportedControlWidth(context);
    height=getSupportedControlHeight(context);
  }

  public static int getSupportedControlWidth(Context context) {
    return context.getResources()
                  .getDimensionPixelSize(R.dimen.smart_watch_control_width);
  }

  public static int getSupportedControlHeight(Context context) {
    return context.getResources()
                  .getDimensionPixelSize(R.dimen.smart_watch_control_height);
  }

  @Override
  public void onResume() {
    if (content == null) {
      LinearLayout root=new LinearLayout(mContext);
      root.setLayoutParams(new LayoutParams(width, height));

      content=
          (ViewGroup)LayoutInflater.from(mContext)
                                   .inflate(R.layout.main, root);
      content.measure(width, height);
      content.layout(0, 0, content.getMeasuredWidth(),
                     content.getMeasuredHeight());
      content.findViewById(R.id.confirm).setOnClickListener(this);
    }

    Bitmap mBackground=
        Bitmap.createBitmap(width, height, BITMAP_CONFIG);

    mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);

    Canvas canvas=new Canvas(mBackground);
    content.draw(canvas);

    showBitmap(mBackground);
  }

  @Override
  public void onTouch(final ControlTouchEvent event) {
    if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE) {
      View match=
          findBestTouchMatch(content, event.getX(), event.getY());

      if (match != content) {
        match.performClick();
      }
    }
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.confirm) {
      Intent i=new Intent(v.getContext(), AuthDetectionService.class);

      i.setAction(AuthDetectionService.CMD_VALIDATE);
      v.getContext().startService(i);
    }
  }

  static View findBestTouchMatch(ViewGroup parent, int x, int y) {
    Rect r=new Rect();

    for (int i=0; i < parent.getChildCount(); i++) {
      View child=parent.getChildAt(i);

      child.getHitRect(r);

      if (r.contains(x, y)) {
        if (child instanceof ViewGroup) {
          return(findBestTouchMatch((ViewGroup)child,
                                    x - child.getLeft(),
                                    y - child.getTop()));
        }
        else {
          return(child);
        }
      }
    }

    return(parent);
  }
}
