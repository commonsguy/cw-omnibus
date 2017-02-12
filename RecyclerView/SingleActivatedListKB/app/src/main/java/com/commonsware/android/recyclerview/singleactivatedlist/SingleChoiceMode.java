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

public class SingleChoiceMode implements ChoiceMode {
  private static final String STATE_CHECKED="checkedPosition";
  private int checkedPosition=-1;

  @Override
  public boolean isSingleChoice() {
    return(true);
  }

  @Override
  public int getCheckedPosition() {
    return(checkedPosition);
  }

  @Override
  public void setChecked(int position, boolean isChecked) {
    if (isChecked) {
      checkedPosition=position;
    }
    else if (isChecked(position)) {
      checkedPosition=-1;
    }
  }

  @Override
  public boolean isChecked(int position) {
    return(checkedPosition==position);
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    state.putInt(STATE_CHECKED, checkedPosition);
  }

  @Override
  public void onRestoreInstanceState(Bundle state) {
    checkedPosition=state.getInt(STATE_CHECKED, -1);
  }
}
