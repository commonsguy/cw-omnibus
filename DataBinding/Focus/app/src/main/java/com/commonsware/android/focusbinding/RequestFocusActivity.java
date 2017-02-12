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

package com.commonsware.android.focusbinding;

import android.app.Activity;
import android.content.res.Configuration;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

public class RequestFocusActivity extends Activity {
  private static final String TRUE="true";
  private static final String IF_HARD_KEYBOARD="ifHardKeyboard";
  private static final String IF_NO_HARD_KEYBOARD="ifNoHardKeyboard";

  @BindingAdapter("app:requestFocus")
  public static void bindRequestFocus(View v, String focusMode) {
    Configuration cfg=v.getResources().getConfiguration();
    boolean hasNoKeyboard=
      cfg.keyboard==Configuration.KEYBOARD_NOKEYS;
    boolean keyboardHidden=
      cfg.hardKeyboardHidden==Configuration.HARDKEYBOARDHIDDEN_YES;
    boolean result=false;

    if (TRUE.equals(focusMode)) {
      result=true;
    }
    else if (IF_HARD_KEYBOARD.equals(focusMode)) {
      if (!hasNoKeyboard && !keyboardHidden) {
        result=true;
      }
    }
    else if (IF_NO_HARD_KEYBOARD.equals(focusMode)) {
      if (hasNoKeyboard || keyboardHidden) {
        result=true;
        if (hasNoKeyboard) v.setFocusableInTouchMode(true);
      }
    }
    else {
      throw new IllegalArgumentException("Unexpected focusMode value: "+focusMode);
    }

    if (result) {
      v.setFocusable(true);
      v.requestFocus();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DataBindingUtil.setContentView(this, R.layout.request_focus);
  }
}