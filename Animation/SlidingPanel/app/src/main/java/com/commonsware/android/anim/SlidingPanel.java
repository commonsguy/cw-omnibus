/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

   
package com.commonsware.android.anim;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class SlidingPanel extends LinearLayout {
  private int speed=300;
  private boolean isOpen=false;
  
  public SlidingPanel(final Context ctxt, AttributeSet attrs) {
    super(ctxt, attrs);
    
    TypedArray a=ctxt.obtainStyledAttributes(attrs,
                                              R.styleable.SlidingPanel,
                                              0, 0);
    
    speed=a.getInt(R.styleable.SlidingPanel_speed, 300);
    
    a.recycle();
  }
  
  public void toggle() {
    TranslateAnimation anim=null;
    
    isOpen=!isOpen;
    
    if (isOpen) {
      setVisibility(View.VISIBLE);
      anim=new TranslateAnimation(0.0f, 0.0f,
                                  getHeight(),
                                  0.0f);
    }
    else {
      anim=new TranslateAnimation(0.0f, 0.0f, 0.0f,
                                  getHeight());
      anim.setAnimationListener(collapseListener);
    }
    
    anim.setDuration(speed);
    anim.setInterpolator(new AccelerateInterpolator(1.0f));
    startAnimation(anim);
  }
  
  Animation.AnimationListener collapseListener=new Animation.AnimationListener() {
    public void onAnimationEnd(Animation animation) {
      setVisibility(View.INVISIBLE);
    }
    
    public void onAnimationRepeat(Animation animation) {
      // not needed
    }
    
    public void onAnimationStart(Animation animation) {
      // not needed
    }
  };
}