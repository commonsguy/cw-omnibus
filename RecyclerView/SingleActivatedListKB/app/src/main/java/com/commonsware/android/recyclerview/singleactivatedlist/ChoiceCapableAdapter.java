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

package com.commonsware.android.recyclerview.singleactivatedlist;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

abstract public class
    ChoiceCapableAdapter<T extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<T> {
  private static final long KEY_TIME_DELTA=250;
  private final ChoiceMode choiceMode;
  private final RecyclerView rv;
  private long lastDownKeyTime=-1L;
  private long lastUpKeyTime=-1L;

  public ChoiceCapableAdapter(RecyclerView rv,
                              ChoiceMode choiceMode) {
    super();
    this.rv=rv;
    this.choiceMode=choiceMode;
  }

  void onChecked(int position, boolean isChecked) {
    onChecked(position, isChecked, false);
  }

  void onChecked(int position, boolean isChecked, boolean updateUI) {
    if (choiceMode.isSingleChoice()) {
      int checked=choiceMode.getCheckedPosition();

      if (checked>=0) {
        RowController row=
            (RowController)rv.findViewHolderForAdapterPosition(checked);

        if (row!=null) {
          row.setChecked(false);
        }
      }
    }

    choiceMode.setChecked(position, isChecked);

    if (updateUI) {
      notifyItemChanged(position);
      rv.scrollToPosition(position);
    }
  }

  boolean isChecked(int position) {
    return(choiceMode.isChecked(position));
  }

  void onSaveInstanceState(Bundle state) {
    choiceMode.onSaveInstanceState(state);
  }

  void onRestoreInstanceState(Bundle state) {
    choiceMode.onRestoreInstanceState(state);
  }

  @Override
  public void onViewAttachedToWindow(T holder) {
    super.onViewAttachedToWindow(holder);

    if (holder.getAdapterPosition()!=choiceMode.getCheckedPosition()) {
      ((RowController)holder).setChecked(false);
    }
  }

  // inspired by http://stackoverflow.com/a/28838834/115145

  @Override
  public void onAttachedToRecyclerView(RecyclerView rv) {
    super.onAttachedToRecyclerView(rv);

    if (choiceMode.isSingleChoice()) {
      rv.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
          if (event.getAction()==KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
              case KeyEvent.KEYCODE_DPAD_DOWN:
                return(chooseNext());
              case KeyEvent.KEYCODE_DPAD_UP:
                return(choosePrevious());
            }
          }

          return(false);
        }
      });
    }
  }

  private boolean chooseNext() {
    long now=System.currentTimeMillis();
    boolean result=false;

    if (lastDownKeyTime==-1 || now-lastDownKeyTime>KEY_TIME_DELTA) {
      lastDownKeyTime=now;
      lastUpKeyTime=-1L;

      int checked=choiceMode.getCheckedPosition();

      if (checked<0) {
        onChecked(0, true, true);
        result=true;
      }
      else if (checked<getItemCount()-1) {
        onChecked(checked+1, true, true);
        result=true;
      }
    }

    return(result);
  }

  private boolean choosePrevious() {
    long now=System.currentTimeMillis();
    boolean result=false;

    if (lastUpKeyTime==-1 || now-lastUpKeyTime>KEY_TIME_DELTA) {
      lastUpKeyTime=now;
      lastDownKeyTime=-1L;

      int checked=choiceMode.getCheckedPosition();

      if (checked>0) {
        onChecked(checked-1, true, true);
        result=true;
      }
      else if (checked<0) {
        onChecked(0, true, true);
        result=true;
      }
    }

    return(result);
  }
}
