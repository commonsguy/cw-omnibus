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

package com.commonsware.android.anim.threepane;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class ThreePaneLayout extends LinearLayout {
  private static final int ANIM_DURATION=500;
  private View left=null;
  private View middle=null;
  private View right=null;
  private int leftWidth=-1;
  private int middleWidthNormal=-1;

  public ThreePaneLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    initSelf();
  }

  void initSelf() {
    setOrientation(HORIZONTAL);
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();

    left=getChildAt(0);
    middle=getChildAt(1);
    right=getChildAt(2);
  }

  public View getLeftView() {
    return(left);
  }

  public View getMiddleView() {
    return(middle);
  }

  public View getRightView() {
    return(right);
  }

  public void hideLeft() {
    if (leftWidth == -1) {
      leftWidth=left.getWidth();
      middleWidthNormal=middle.getWidth();
      resetWidget(left, leftWidth);
      resetWidget(middle, middleWidthNormal);
      resetWidget(right, middleWidthNormal);
      requestLayout();
    }

    translateWidgets(-1 * leftWidth, left, middle, right);

    ObjectAnimator.ofInt(this, "middleWidth", middleWidthNormal,
                         leftWidth).setDuration(ANIM_DURATION).start();
  }

  public void showLeft() {
    translateWidgets(leftWidth, left, middle, right);

    ObjectAnimator.ofInt(this, "middleWidth", leftWidth,
                         middleWidthNormal).setDuration(ANIM_DURATION)
                  .start();
  }

  @SuppressWarnings("unused")
  private void setMiddleWidth(int value) {
    middle.getLayoutParams().width=value;
    requestLayout();
  }

  @TargetApi(11)
  private void translateWidgets(int deltaX, View... views) {
    for (final View v : views) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
      }

      ViewPropertyAnimator.animate(v).translationXBy(deltaX)
                          .setDuration(ANIM_DURATION)
                          .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                v.setLayerType(View.LAYER_TYPE_NONE,
                                               null);
                              }
                            }
                          });
    }
  }

  private void resetWidget(View v, int width) {
    LinearLayout.LayoutParams p=
        (LinearLayout.LayoutParams)v.getLayoutParams();

    p.width=width;
    p.weight=0;
  }
}
