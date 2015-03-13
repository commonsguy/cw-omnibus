/***
  Copyright (c) 2012-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.eventbus;

import android.app.Activity;
import android.os.Bundle;

public class AsyncDemo extends Activity {
  private static final String MODEL_TAG="model";
  private ModelFragment mFrag=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mFrag=
        (ModelFragment)getFragmentManager().findFragmentByTag(MODEL_TAG);

    if (mFrag == null) {
      mFrag=new ModelFragment();

      getFragmentManager().beginTransaction().add(mFrag, MODEL_TAG)
                          .commit();
    }

    AsyncDemoFragment demo=
        (AsyncDemoFragment)getFragmentManager().findFragmentById(android.R.id.content);

    if (demo == null) {
      demo=new AsyncDemoFragment();
      getFragmentManager().beginTransaction()
                          .add(android.R.id.content, demo).commit();
    }

    demo.setModel(mFrag.getModel());
  }
}
