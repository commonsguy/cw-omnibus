/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 http://commonsware.com/Android
 */

package com.commonsware.android.recyclerview.actionmodelist2;

import android.os.Bundle;
import android.util.SparseBooleanArray;

public class MultiChoiceMode implements ChoiceMode {
  private static final String STATE_CHECK_STATES="checkStates";
  private ParcelableSparseBooleanArray checkStates=new ParcelableSparseBooleanArray();

  @Override
  public void setChecked(int position, boolean isChecked) {
    if (isChecked) {
      checkStates.put(position, isChecked);
    }
    else {
      checkStates.delete(position);
    }
  }

  @Override
  public boolean isChecked(int position) {
    return(checkStates.get(position, false));
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    state.putParcelable(STATE_CHECK_STATES, checkStates);
  }

  @Override
  public void onRestoreInstanceState(Bundle state) {
    checkStates=state.getParcelable(STATE_CHECK_STATES);
  }

  @Override
  public int getCheckedCount() {
    return(checkStates.size());
  }

  @Override
  public void clearChecks() {
    checkStates.clear();
  }

  @Override
  public void visitChecks(Visitor v) {
    SparseBooleanArray copy=checkStates.clone();

    for (int i=0;i<copy.size();i++) {
      v.onCheckedPosition(copy.keyAt(i));
    }
  }
}
