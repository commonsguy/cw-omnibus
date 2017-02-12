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

package com.commonsware.android.colormixer;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class ColorMixer extends RelativeLayout {
  private static final String SUPERSTATE="superState";
  private static final String COLOR="color";
  private View swatch=null;
  private SeekBar red=null;
  private SeekBar blue=null;
  private SeekBar green=null;
  private OnColorChangedListener listener=null;
  
  public ColorMixer(Context context) {
    this(context, null);
  }
  
  public ColorMixer(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public ColorMixer(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    
    ((Activity)getContext())
        .getLayoutInflater()
        .inflate(R.layout.mixer, this, true);
    
    swatch=findViewById(R.id.swatch);
        
    red=(SeekBar)findViewById(R.id.red);
    red.setMax(0xFF);
    red.setOnSeekBarChangeListener(onMix);

    green=(SeekBar)findViewById(R.id.green);
    green.setMax(0xFF);
    green.setOnSeekBarChangeListener(onMix);

    blue=(SeekBar)findViewById(R.id.blue);
    blue.setMax(0xFF);
    blue.setOnSeekBarChangeListener(onMix);
    
    if (attrs!=null) {
      TypedArray a=getContext()
                      .obtainStyledAttributes(attrs,
                                                R.styleable.ColorMixer,
                                                0, 0);
      
      setColor(a.getInt(R.styleable.ColorMixer_initialColor,
                        0xFFA4C639));
      a.recycle();
    }
  }
  
  @Override
  public Parcelable onSaveInstanceState() {
    Bundle state=new Bundle();
    
    state.putParcelable(SUPERSTATE, super.onSaveInstanceState());
    state.putInt(COLOR, getColor());

    return(state);
  }

  @Override
  public void onRestoreInstanceState(Parcelable ss) {
    Bundle state=(Bundle)ss;
    
    super.onRestoreInstanceState(state.getParcelable(SUPERSTATE));

    setColor(state.getInt(COLOR));
  }
  
  public OnColorChangedListener getOnColorChangedListener() {
    return(listener);
  }
  
  public void setOnColorChangedListener(OnColorChangedListener listener) {
    this.listener=listener;
  }
  
  public int getColor() {
    return(Color.argb(0xFF, red.getProgress(),
                      green.getProgress(), blue.getProgress()));
  }
  
  public void setColor(int color) {
    red.setProgress(Color.red(color));
    green.setProgress(Color.green(color));
    blue.setProgress(Color.blue(color));
    swatch.setBackgroundColor(color);
  }
  
  private SeekBar.OnSeekBarChangeListener onMix=new SeekBar.OnSeekBarChangeListener() {
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
      int color=getColor();
      
      swatch.setBackgroundColor(color);
      
      if (listener!=null) {
        listener.onColorChange(color);
      }
    }
    
    public void onStartTrackingTouch(SeekBar seekBar) {
      // unused
    }
    
    public void onStopTrackingTouch(SeekBar seekBar) {
      // unused
    }
  };
  
  public interface OnColorChangedListener {
    public void onColorChange(int argb);
  }
}