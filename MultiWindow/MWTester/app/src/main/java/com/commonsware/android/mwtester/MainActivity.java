/***
  Copyright (c) 2008-2016 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.mwtester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
  private static final String PLAIN=
    "com.commonsware.android.mwsampler.PLAIN";
  private static final String LAYOUT=
    "com.commonsware.android.mwsampler.LAYOUT";
  private static final String OPT_OUT=
    "com.commonsware.android.mwsampler.OPT_OUT";
  private static final String LANDSCAPE=
    "com.commonsware.android.mwsampler.LANDSCAPE";

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
  }

  public void launchPlainSameTask(View v) {
    launch(PLAIN, 0);
  }

  public void launchLayoutSameTask(View v) {
    launch(LAYOUT, 0);
  }

  public void launchOptOutSameTask(View v) {
    launch(OPT_OUT, 0);
  }

  public void launchLandscapeSameTask(View v) {
    launch(LANDSCAPE, 0);
  }

  public void launchPlainNewTask(View v) {
    launch(PLAIN, Intent.FLAG_ACTIVITY_NEW_TASK);
  }

  public void launchLayoutNewTask(View v) {
    launch(LAYOUT, Intent.FLAG_ACTIVITY_NEW_TASK);
  }

  public void launchOptOutNewTask(View v) {
    launch(OPT_OUT, Intent.FLAG_ACTIVITY_NEW_TASK);
  }

  public void launchLandscapeNewTask(View v) {
    launch(LANDSCAPE, Intent.FLAG_ACTIVITY_NEW_TASK);
  }

  private void launch(String action, int flags) {
    startActivity(new Intent(action).addFlags(flags));
  }
}
