/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.tasks.docs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

public class EditorActivity extends Activity {
  public static final String EXTRA_TITLE="title";
  public static final String EXTRA_TEXT="text";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getFragmentManager().findFragmentById(android.R.id.content)==null) {
      String title=getIntent().getStringExtra(EXTRA_TITLE);
      CharSequence text=getIntent().getCharSequenceExtra(EXTRA_TEXT);
      EditorFragment f=EditorFragment.newInstance(title, text);

      getFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, f)
          .commit();

      String appTitle=getString(R.string.editor_activity_title_prefix)+title;

      setTitle(appTitle);

      if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
        setTaskDescription(buildTaskDesc(appTitle));
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private ActivityManager.TaskDescription buildTaskDesc(String appTitle) {
    Bitmap icon=BitmapFactory.decodeResource(getResources(),
                                              R.drawable.ic_launcher);

    return(new ActivityManager.TaskDescription(appTitle, icon,
                                                Color.BLACK));
  }
}