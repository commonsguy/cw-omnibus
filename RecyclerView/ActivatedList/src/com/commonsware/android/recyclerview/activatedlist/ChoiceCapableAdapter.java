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

package com.commonsware.android.recyclerview.activatedlist;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

abstract public class
    ChoiceCapableAdapter<T extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<T> {
  private final ChoiceMode choiceMode;

  public ChoiceCapableAdapter(ChoiceMode choiceMode) {
    super();
    this.choiceMode=choiceMode;
  }

  void onChecked(int position, boolean isChecked) {
    choiceMode.setChecked(position, isChecked);
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
}
