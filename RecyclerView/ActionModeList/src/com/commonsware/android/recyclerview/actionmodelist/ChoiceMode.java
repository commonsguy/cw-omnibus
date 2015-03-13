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

package com.commonsware.android.recyclerview.actionmodelist;

import android.os.Bundle;

public interface ChoiceMode {
  void setChecked(int position, boolean isChecked);
  boolean isChecked(int position);
  void onSaveInstanceState(Bundle state);
  void onRestoreInstanceState(Bundle state);
  int getCheckedCount();
  void clearChecks();
}
